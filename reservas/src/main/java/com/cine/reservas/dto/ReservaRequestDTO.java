package com.cine.reservas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {

    @NotNull(message = "El usuario_id es obligatorio")
    private Long usuario_id;

    @NotNull(message = "El funcion_id es obligatorio")
    private Long funcion_id;

    @NotNull(message = "La cantidad de asientos es obligatoria")
    @Positive(message = "La cantidad de asientos debe ser mayor a 0")
    private Integer cantidad_de_asientos;

    @NotNull(message = "El total es obligatorio")
    @Positive(message = "El total debe ser mayor a 0")
    private Double total;
}