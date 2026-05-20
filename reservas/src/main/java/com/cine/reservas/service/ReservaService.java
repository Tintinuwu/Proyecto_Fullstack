package com.cine.reservas.service;

import com.cine.reservas.client.PagoCliente;
import com.cine.reservas.dto.PagoRequestDTO;
import com.cine.reservas.dto.PagoResponseDTO;
import com.cine.reservas.dto.ReservaRequestDTO;
import com.cine.reservas.dto.ReservaResponseDTO;
import com.cine.reservas.model.Resrvas_model;
import com.cine.reservas.repository.Reserva_repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaService {

    private final Reserva_repository reservaRepository;
    private final PagoCliente pagoCliente;  // ← NUEVO: cliente Feign

    private ReservaResponseDTO mapToDTO(Resrvas_model reserva) {
        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getUsuarioId(),
                reserva.getFuncionId(),
                reserva.getCantidadDeAsientos(),
                reserva.getTotal(),
                reserva.getEstado(),
                reserva.getFechaReserva()
        );
    }

    private Resrvas_model mapToEntity(ReservaRequestDTO dto) {
        Resrvas_model reserva = new Resrvas_model();
        reserva.setUsuarioId(dto.getUsuario_id());
        reserva.setFuncionId(dto.getFuncion_id());
        reserva.setCantidadDeAsientos(dto.getCantidad_de_asientos());
        reserva.setTotal(dto.getTotal());
        reserva.setEstado("PENDIENTE");
        return reserva;
    }

    public List<ReservaResponseDTO> obtenerTodas() {
        return reservaRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ReservaResponseDTO> obtenerPorId(Long id) {
        return reservaRepository.findById(id).map(this::mapToDTO);
    }

    public List<ReservaResponseDTO> obtenerPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ReservaResponseDTO> obtenerPorFuncion(Long funcionId) {
        return reservaRepository.findByFuncionId(funcionId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ReservaResponseDTO> obtenerPorEstado(String estado) {
        return reservaRepository.findByEstado(estado).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────────────
    // metodo guardar modificado (con llamada a ms-pagos)
    // ──────────────────────────────────────────────────────
    public ReservaResponseDTO guardar(ReservaRequestDTO dto) {
        // 1. Guardar reserva como PENDIENTE
        Resrvas_model reserva = mapToEntity(dto);
        Resrvas_model reservaGuardada = reservaRepository.save(reserva);
        log.info("Reserva creada con ID: {} en estado PENDIENTE", reservaGuardada.getId());

        // 2. Llamar a ms-pagos para procesar el pago
        try {
            PagoRequestDTO pagoRequest = new PagoRequestDTO(
                    reservaGuardada.getId(),
                    dto.getTotal(),
                    "TARJETA"
            );

            PagoResponseDTO pagoResponse = pagoCliente.procesarPago(pagoRequest);
            log.info("Respuesta de ms-pagos: {}", pagoResponse.getEstado());

            // 3. Actualizar estado según respuesta del pago
            if ("APROBADO".equals(pagoResponse.getEstado())) {
                reservaGuardada.setEstado("CONFIRMADA");
                log.info("Pago aprobado, reserva {} confirmada", reservaGuardada.getId());
            } else {
                reservaGuardada.setEstado("CANCELADA");
                log.warn("Pago {}: reserva {} cancelada", pagoResponse.getEstado(), reservaGuardada.getId());
            }

            return mapToDTO(reservaRepository.save(reservaGuardada));

        } catch (Exception e) {
            // Si hay error al llamar a ms-pagos, cancelar la reserva
            reservaGuardada.setEstado("CANCELADA");
            reservaRepository.save(reservaGuardada);
            log.error("Error al llamar a ms-pagos: {}", e.getMessage());
            throw new RuntimeException("Error al procesar el pago: " + e.getMessage());
        }
    }

    public Optional<ReservaResponseDTO> cancelar(Long id) {
        return reservaRepository.findById(id).map(reserva -> {
            reserva.setEstado("CANCELADA");
            return mapToDTO(reservaRepository.save(reserva));
        });
    }

    public Optional<ReservaResponseDTO> confirmar(Long id) {
        return reservaRepository.findById(id).map(reserva -> {
            reserva.setEstado("CONFIRMADA");
            return mapToDTO(reservaRepository.save(reserva));
        });
    }

    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }
}