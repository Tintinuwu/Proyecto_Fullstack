package com.cine.pagos.service;

import com.cine.pagos.dto.PagoRequestDTO;
import com.cine.pagos.dto.PagoResponseDTO;
import com.cine.pagos.model.Pago;
import com.cine.pagos.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;

    private PagoResponseDTO mapToDTO(Pago pago) {
        return new PagoResponseDTO(
                pago.getId(),
                pago.getReservaId(),
                pago.getMonto(),
                pago.getMetodo(),
                pago.getEstado(),
                pago.getCodigoTransaccion(),
                pago.getFechaPago(),
                pago.getMensajeError()
        );
    }

    // Simula el procesamiento de un pago (aprobado, rechazado o timeout)
    private String simularEstadoPago() {
        int random = (int) (Math.random() * 100);
        if (random < 70) {
            return "APROBADO";
        } else if (random < 90) {
            return "RECHAZADO";
        } else {
            return "TIMEOUT";
        }
    }

    public PagoResponseDTO procesarPago(PagoRequestDTO dto) {
        log.info("Procesando pago para reserva ID: {}", dto.getReservaId());

        Pago pago = new Pago();
        pago.setReservaId(dto.getReservaId());
        pago.setMonto(dto.getMonto());
        pago.setMetodo(dto.getMetodo());
        pago.setFechaPago(LocalDateTime.now());
        pago.setCodigoTransaccion("TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8));

        String estado = simularEstadoPago();
        pago.setEstado(estado);

        if (estado.equals("RECHAZADO")) {
            pago.setMensajeError("Pago rechazado por el banco");
        } else if (estado.equals("TIMEOUT")) {
            pago.setMensajeError("Tiempo de espera agotado");
        }

        log.info("Pago procesado con estado: {}", estado);
        return mapToDTO(pagoRepository.save(pago));
    }

    public Optional<PagoResponseDTO> obtenerPagoPorReserva(Long reservaId) {
        return pagoRepository.findByReservaId(reservaId).map(this::mapToDTO);
    }

    public Optional<PagoResponseDTO> obtenerPagoPorId(Long id) {
        return pagoRepository.findById(id).map(this::mapToDTO);
    }
}