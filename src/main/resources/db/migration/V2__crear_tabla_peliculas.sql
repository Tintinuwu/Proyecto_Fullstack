CREATE TABLE peliculas (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    titulo          VARCHAR(200)    NOT NULL,
    genero          VARCHAR(50)     NOT NULL,
    clasificacion   VARCHAR(10)     NOT NULL,
    duracion_min    INT             NOT NULL,
    sinopsis        TEXT,
    activo          BOOLEAN         NOT NULL DEFAULT TRUE,

    PRIMARY KEY (id),
    CONSTRAINT chk_clasificacion CHECK (clasificacion IN ('G', 'PG', 'PG-13', 'R', 'NC-17')),
    CONSTRAINT chk_duracion CHECK (duracion_min > 0)
);
