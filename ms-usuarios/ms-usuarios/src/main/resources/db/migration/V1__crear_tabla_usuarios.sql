CREATE TABLE usuarios (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(100)    NOT NULL,
    apellido        VARCHAR(100)    NOT NULL,
    email           VARCHAR(150)    NOT NULL UNIQUE,
    password        VARCHAR(255)    NOT NULL,
    rol             VARCHAR(20)     NOT NULL,
    activo          BOOLEAN         NOT NULL DEFAULT TRUE,
    fecha_registro  DATETIME        NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT chk_rol CHECK (rol IN ('CLIENTE', 'ADMIN'))
);
