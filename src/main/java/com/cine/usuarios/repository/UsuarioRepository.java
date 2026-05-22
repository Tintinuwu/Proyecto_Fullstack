package com.cine.usuarios.repository;

import com.cine.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Métodos personalizados usados en tu UsuarioService
    boolean existsByEmail(String email);
    List<Usuario> findByActivoTrue();
}