package com.cine.ms_usuarios.service;

import com.cine.ms_usuarios.dto.UsuarioRequestDTO;
import com.cine.ms_usuarios.dto.UsuarioResponseDTO;
import com.cine.ms_usuarios.model.Usuario;
import com.cine.ms_usuarios.repository.UsuarioRepository;
import com.cine.ms_usuarios.dto.UsuarioRequestDTO;
import com.cine.ms_usuarios.dto.UsuarioResponseDTO;
import com.cine.ms_usuarios.model.Usuario;
import com.cine.ms_usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Capa de servicio para la gestión de usuarios.
 * Aquí reside toda la lógica de negocio: validaciones, reglas y transformaciones.
 * El controller nunca toma decisiones; solo delega aquí.
 *
 * @Slf4j inyecta automáticamente el logger: log.info(), log.warn(), log.error()
 * @RequiredArgsConstructor genera el constructor con los campos final (inyección por constructor)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // ──────────────────────────────────────────────────────
    // Mapeos entre entidad y DTO
    // ──────────────────────────────────────────────────────

    /**
     * Convierte una entidad Usuario a su DTO de respuesta.
     * Nunca incluye la contraseña en la salida.
     */
    private UsuarioResponseDTO mapToDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getActivo(),
                usuario.getFechaRegistro()
        );
    }

    /**
     * Construye una entidad Usuario desde el DTO de entrada.
     * La fecha de registro se asigna en @PrePersist, no aquí.
     */
    private Usuario mapToEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());
        usuario.setRol(dto.getRol());
        return usuario;
    }

    // ──────────────────────────────────────────────────────
    // Operaciones CRUD
    // ──────────────────────────────────────────────────────

    /**
     * Registra un nuevo usuario.
     * Regla de negocio: no se permiten emails duplicados en el sistema.
     */
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {
        log.info("Intentando crear usuario con email: {}", dto.getEmail());

        // Validación de unicidad de email antes de persistir
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            log.warn("Creación rechazada - email ya registrado: {}", dto.getEmail());
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + dto.getEmail());
        }

        Usuario guardado = usuarioRepository.save(mapToEntity(dto));
        log.info("Usuario creado exitosamente con ID: {}", guardado.getId());
        return mapToDTO(guardado);
    }

    /**
     * Retorna todos los usuarios registrados, sin importar su estado.
     */
    public List<UsuarioResponseDTO> obtenerTodos() {
        log.info("Consultando todos los usuarios del sistema");
        return usuarioRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retorna solo los usuarios con activo = true.
     */
    public List<UsuarioResponseDTO> obtenerActivos() {
        log.info("Consultando usuarios activos");
        return usuarioRepository.findByActivoTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca un usuario por su ID.
     * Retorna Optional para que el controller decida qué HTTP status usar.
     */
    public Optional<UsuarioResponseDTO> obtenerPorId(Long id) {
        log.debug("Buscando usuario con ID: {}", id);
        return usuarioRepository.findById(id).map(this::mapToDTO);
    }

    /**
     * Actualiza los datos de un usuario existente.
     * Regla de negocio: si el email cambia, no puede coincidir con el de otro usuario.
     */
    public Optional<UsuarioResponseDTO> actualizar(Long id, UsuarioRequestDTO dto) {
        log.info("Actualizando usuario con ID: {}", id);

        return usuarioRepository.findById(id).map(usuario -> {
            // Solo validar email duplicado si realmente cambió
            if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
                log.warn("Actualización rechazada - email en uso: {}", dto.getEmail());
                throw new IllegalArgumentException("El email ya está en uso: " + dto.getEmail());
            }

            usuario.setNombre(dto.getNombre());
            usuario.setApellido(dto.getApellido());
            usuario.setEmail(dto.getEmail());
            usuario.setPassword(dto.getPassword());
            usuario.setRol(dto.getRol());

            Usuario actualizado = usuarioRepository.save(usuario);
            log.info("Usuario ID {} actualizado correctamente", actualizado.getId());
            return mapToDTO(actualizado);
        });
    }

    /**
     * Soft delete: marca al usuario como inactivo en lugar de eliminarlo físicamente.
     * Esto preserva el historial de reservas asociadas al usuario.
     */
    public boolean eliminar(Long id) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
            log.info("Usuario ID {} desactivado (soft delete)", id);
            return true;
        }).orElseGet(() -> {
            log.warn("Eliminación fallida - usuario no encontrado con ID: {}", id);
            return false;
        });
    }

    /**
     * Verifica si un usuario existe y está activo.
     * Endpoint consumido por MS-Reservas antes de crear una reserva.
     */
    public boolean existeUsuario(Long id) {
        log.debug("Verificando existencia del usuario ID: {}", id);
        boolean existe = usuarioRepository.findById(id)
                .map(Usuario::getActivo)
                .orElse(false);
        log.debug("Usuario ID {} activo: {}", id, existe);
        return existe;
    }
}
