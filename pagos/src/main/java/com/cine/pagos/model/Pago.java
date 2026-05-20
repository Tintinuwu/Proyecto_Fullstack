package com.cine.pagos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reserva_id", nullable = false)
    private Long reservaId;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false, length = 50)
    private String metodo;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "codigo_transaccion", nullable = false, unique = true, length = 100)
    private String codigoTransaccion;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "mensaje_error", length = 255)
    private String mensajeError;
}