package com.cine.pelicula.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeliculaRequestDTO {

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 200, message = "El título no puede superar los 200 caracteres")
    private String titulo;

    @NotBlank(message = "El género no puede estar vacío")
    private String genero;

    @NotBlank(message = "La clasificación no puede estar vacía")
    @Pattern(regexp = "^(G|PG|PG-13|R|NC-17)$",
            message = "La clasificación debe ser: G, PG, PG-13, R o NC-17")
    private String clasificacion;

    @NotNull(message = "La duración es obligatoria")
    @Positive(message = "La duración debe ser un número positivo en minutos")
    private Integer duracionMin;

    private String sinopsis;
}