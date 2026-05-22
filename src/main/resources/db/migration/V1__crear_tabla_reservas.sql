CREATE TABLE reservas (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    usuario_id           BIGINT       NOT NULL,
    funcion_id           BIGINT       NOT NULL,
    cantidad_de_asientos INT          NOT NULL,
    total                DOUBLE       NOT NULL,
    estado               VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
    fecha_reserva TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);