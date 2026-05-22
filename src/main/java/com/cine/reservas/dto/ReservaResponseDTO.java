package com.cine.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {

    private Long id;
    private Long usuario_id;
    private Long funcion_id;
    private Integer cantidad_de_asientos;
    private Double total;
    private String estado;
    private LocalDateTime fechaReserva;
}