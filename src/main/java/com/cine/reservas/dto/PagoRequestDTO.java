package com.cine.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// dto para enviar datos del pago a ms-pagos
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequestDTO {
    private Long reservaId;   // id de la reserva que se paga
    private Double monto;      // Monto a pagar
    private String metodo;     // Metodo de pago: tarjeta, efectivo, etc.
}