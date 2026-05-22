package com.cine.pelicula.service;

import com.cine.pelicula.dto.PeliculaRequestDTO;
import com.cine.pelicula.dto.PeliculaResponseDTO;
import com.cine.pelicula.model.Pelicula;
import com.cine.pelicula.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Capa de servicio para la gestión del catálogo de películas.
 * Toda la lógica de negocio reside aquí. El controller nunca toma decisiones,
 * solo delega y retorna la respuesta HTTP correspondiente.
 *
 * @Slf4j inyecta el logger automáticamente: log.info(), log.warn(), log.error()
 * @RequiredArgsConstructor genera el constructor con los campos final (inyección por constructor)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PeliculaService {

    private final PeliculaRepository peliculaRepository;

    // ──────────────────────────────────────────────────────
    // Mapeos entre entidad y DTO
    // ──────────────────────────────────────────────────────

    /**
     * Convierte la entidad JPA al DTO de salida.
     * Este método se usa siempre antes de responder al cliente,
     * garantizando que nunca se exponga el modelo interno directamente.
     */
    private PeliculaResponseDTO mapToDTO(Pelicula pelicula) {
        return new PeliculaResponseDTO(
                pelicula.getId(),
                pelicula.getTitulo(),
                pelicula.getGenero(),
                pelicula.getClasificacion(),
                pelicula.getDuracionMin(),
                pelicula.getSinopsis(),
                pelicula.getActivo()
        );
    }

    /**
     * Construye una entidad Pelicula desde el DTO de entrada.
     * El campo 'activo' se inicializa en @PrePersist de la entidad.
     */
    private Pelicula mapToEntity(PeliculaRequestDTO dto) {
        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo(dto.getTitulo());
        pelicula.setGenero(dto.getGenero());
        pelicula.setClasificacion(dto.getClasificacion());
        pelicula.setDuracionMin(dto.getDuracionMin());
        pelicula.setSinopsis(dto.getSinopsis());
        return pelicula;
    }

    // ──────────────────────────────────────────────────────
    // Operaciones CRUD
    // ──────────────────────────────────────────────────────

    /**
     * Agrega una nueva película al catálogo.
     * Regla de negocio: no se permiten títulos duplicados (insensible a mayúsculas).
     */
    public PeliculaResponseDTO crear(PeliculaRequestDTO dto) {
        log.info("Intentando crear película: '{}'", dto.getTitulo());

        if (peliculaRepository.existsByTituloIgnoreCase(dto.getTitulo())) {
            log.warn("Creación rechazada - título ya existe: '{}'", dto.getTitulo());
            throw new IllegalArgumentException("Ya existe una película con el título: " + dto.getTitulo());
        }

        Pelicula guardada = peliculaRepository.save(mapToEntity(dto));
        log.info("Película creada con ID: {}", guardada.getId());
        return mapToDTO(guardada);
    }

    /**
     * Retorna el catálogo completo, incluyendo películas inactivas.
     * Útil para administración interna.
     */
    public List<PeliculaResponseDTO> obtenerTodas() {
        log.info("Consultando catálogo completo de películas");
        return peliculaRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retorna solo las películas activas del catálogo.
     * Este es el endpoint público para los clientes del cine.
     */
    public List<PeliculaResponseDTO> obtenerActivas() {
        log.info("Consultando películas activas del catálogo");
        return peliculaRepository.findByActivoTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retorna películas filtradas por género.
     */
    public List<PeliculaResponseDTO> obtenerPorGenero(String genero) {
        log.info("Filtrando películas por género: '{}'", genero);
        return peliculaRepository.findByGeneroAndActivoTrue(genero).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca una película por su ID.
     */
    public Optional<PeliculaResponseDTO> obtenerPorId(Long id) {
        log.debug("Buscando película con ID: {}", id);
        return peliculaRepository.findById(id).map(this::mapToDTO);
    }

    /**
     * Actualiza los datos de una película existente.
     * Regla de negocio: si el título cambia, no puede coincidir con el de otra película.
     */
    public Optional<PeliculaResponseDTO> actualizar(Long id, PeliculaRequestDTO dto) {
        log.info("Actualizando película con ID: {}", id);

        return peliculaRepository.findById(id).map(pelicula -> {
            // Solo validar duplicado si el título realmente cambió
            if (!pelicula.getTitulo().equalsIgnoreCase(dto.getTitulo())
                    && peliculaRepository.existsByTituloIgnoreCase(dto.getTitulo())) {
                log.warn("Actualización rechazada - título ya en uso: '{}'", dto.getTitulo());
                throw new IllegalArgumentException("Ya existe una película con el título: " + dto.getTitulo());
            }

            pelicula.setTitulo(dto.getTitulo());
            pelicula.setGenero(dto.getGenero());
            pelicula.setClasificacion(dto.getClasificacion());
            pelicula.setDuracionMin(dto.getDuracionMin());
            pelicula.setSinopsis(dto.getSinopsis());

            Pelicula actualizada = peliculaRepository.save(pelicula);
            log.info("Película ID {} actualizada correctamente", actualizada.getId());
            return mapToDTO(actualizada);
        });
    }

    /**
     * Soft delete: desactiva una película sin eliminarla físicamente.
     * Esto protege el historial de funciones y reservas que la referencian.
     */
    public boolean eliminar(Long id) {
        return peliculaRepository.findById(id).map(pelicula -> {
            pelicula.setActivo(false);
            peliculaRepository.save(pelicula);
            log.info("Película ID {} desactivada (soft delete)", id);
            return true;
        }).orElseGet(() -> {
            log.warn("Eliminación fallida - película no encontrada con ID: {}", id);
            return false;
        });
    }

    /**
     * Verifica si una película existe y está activa en el catálogo.
     * Este endpoint es consumido por MS-Programación antes de crear una función,
     * para asegurarse de que la película referenciada exista y esté disponible.
     */
    public boolean existePelicula(Long id) {
        log.debug("Verificando existencia de película ID: {}", id);
        boolean existe = peliculaRepository.findById(id)
                .map(Pelicula::getActivo)
                .orElse(false);
        log.debug("Película ID {} activa: {}", id, existe);
        return existe;
    }
}