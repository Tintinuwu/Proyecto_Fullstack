package com.cine.pelicula.repository;

import com.cine.pelicula.model.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {

    // Estos métodos permiten a Spring Data JPA generar la consulta SQL automáticamente
    boolean existsByTituloIgnoreCase(String titulo);

    List<Pelicula> findByActivoTrue();

    List<Pelicula> findByGeneroAndActivoTrue(String genero);
}