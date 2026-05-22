package com.cine.usuarios.dto;

import com.cine.usuarios.model.Usuario.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private Rol rol;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
}