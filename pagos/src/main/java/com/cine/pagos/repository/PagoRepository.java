package com.cine.pagos.repository;

import com.cine.pagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Buscar pago por el id de reserva
    Optional<Pago> findByReservaId(Long reservaId);
}