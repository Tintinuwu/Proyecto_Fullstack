package com.cine.reservas.repository;

import com.cine.reservas.model.Resrvas_model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface Reserva_repository extends JpaRepository<Resrvas_model, Long> {

    // Buscar reservas por usuario_id
    List<Resrvas_model> findByUsuarioId(Long usuarioId);

    // Buscar reservas por funcion_id
    List<Resrvas_model> findByFuncionId(Long funcionId);

    // Buscar reservas por estado
    List<Resrvas_model> findByEstado(String estado);
}