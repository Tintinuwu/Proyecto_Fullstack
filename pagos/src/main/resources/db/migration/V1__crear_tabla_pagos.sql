CREATE TABLE pagos (
                       id                   BIGINT       NOT NULL AUTO_INCREMENT,
                       reserva_id           BIGINT       NOT NULL,
                       monto                DOUBLE       NOT NULL,
                       metodo               VARCHAR(50)  NOT NULL,
                       estado               VARCHAR(20)  NOT NULL,
                       codigo_transaccion   VARCHAR(100) NOT NULL UNIQUE,
                       fecha_pago           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       mensaje_error        VARCHAR(255) NULL,
                       PRIMARY KEY (id)
);

CREATE INDEX idx_pagos_reserva ON pagos(reserva_id);