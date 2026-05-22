package com.cine.ms_peliculas.controller;

import com.cine.ms_peliculas.dto.PeliculaRequestDTO;
import com.cine.ms_peliculas.dto.PeliculaResponseDTO;
import com.cine.ms_peliculas.service.PeliculaService;
import com.cine.ms_peliculas.dto.PeliculaRequestDTO;
import com.cine.ms_peliculas.dto.PeliculaResponseDTO;
import com.cine.ms_peliculas.service.PeliculaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/peliculas")
@RequiredArgsConstructor
public class PeliculaController {

    private final PeliculaService peliculaService;

    @GetMapping
    public ResponseEntity<List<PeliculaResponseDTO>> obtenerTodas() {
        log.info("GET /api/peliculas - Listando catálogo completo");
        return ResponseEntity.ok(peliculaService.obtenerTodas());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<PeliculaResponseDTO>> obtenerActivas() {
        log.info("GET /api/peliculas/activas - Listando películas activas");
        return ResponseEntity.ok(peliculaService.obtenerActivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeliculaResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/peliculas/{} - Buscando película por ID", id);
        return peliculaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PeliculaResponseDTO> crear(@Valid @RequestBody PeliculaRequestDTO dto) {
        log.info("POST /api/peliculas - Creando nueva película");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(peliculaService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PeliculaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PeliculaRequestDTO dto) {
        log.info("PUT /api/peliculas/{} - Actualizando película", id);
        return peliculaService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/peliculas/{} - Retirando película del catálogo", id);
        boolean eliminado = peliculaService.eliminar(id);
        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("mensaje", "Película retirada del catálogo correctamente"));
    }

    @GetMapping("/{id}/existe")
    public ResponseEntity<Map<String, Boolean>> existePelicula(@PathVariable Long id) {
        log.info("GET /api/peliculas/{}/existe - Validando existencia para MS-Programación", id);
        return ResponseEntity.ok(Map.of("existe", peliculaService.existePelicula(id)));
    }
}