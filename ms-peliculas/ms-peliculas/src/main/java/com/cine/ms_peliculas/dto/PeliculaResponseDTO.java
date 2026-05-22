package com.cine.ms_peliculas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeliculaResponseDTO {
    private Long id;
    private String titulo;
    private String genero;
    private String clasificacion;
    private Integer duracionMin;
    private String sinopsis;
    private Boolean activo;
}