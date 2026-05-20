package com.cine.pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {

    private Long id;
    private Long reservaId;
    private Double monto;
    private String metodo;
    private String estado;
    private String codigoTransaccion;
    private LocalDateTime fechaPago;
    private String mensajeError;
}