package com.cine.pagos.controller;

import com.cine.pagos.dto.PagoRequestDTO;
import com.cine.pagos.dto.PagoResponseDTO;
import com.cine.pagos.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponseDTO> procesarPago(@Valid @RequestBody PagoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pagoService.procesarPago(dto));
    }

    @GetMapping("/reserva/{reservaId}")
    public ResponseEntity<PagoResponseDTO> obtenerPorReserva(@PathVariable Long reservaId) {
        return pagoService.obtenerPagoPorReserva(reservaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return pagoService.obtenerPagoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}