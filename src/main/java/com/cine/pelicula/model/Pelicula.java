package com.cine.pelicula.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "peliculas")
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 200, message = "El título no puede superar los 200 caracteres")
    @Column(nullable = false, length = 200)
    private String titulo;

    @NotBlank(message = "El género no puede estar vacío")
    @Column(nullable = false, length = 50)
    private String genero;

    @NotBlank(message = "La clasificación no puede estar vacía")
    @Pattern(regexp = "^(G|PG|PG-13|R|NC-17)$",
            message = "La clasificación debe ser: G, PG, PG-13, R o NC-17")
    @Column(nullable = false, length = 10)
    private String clasificacion;

    @NotNull(message = "La duración es obligatoria")
    @Positive(message = "La duración debe ser un número positivo en minutos")
    @Column(name = "duracion_min", nullable = false)
    private Integer duracionMin;

    @Column(columnDefinition = "TEXT")
    private String sinopsis;

    @Column(nullable = false)
    private Boolean activo = true;
}