package com.cine.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// dto para recibir la respuesta de ms-pagos
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {
    private String estado;        // APROBADO, RECHAZADO, TIMEOUT
    private String mensajeError;  // null si está aprobado, causa del error si no
}