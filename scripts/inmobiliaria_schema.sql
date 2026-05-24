-- ============================================================
--  INMOBILIARIA MICROSERVICIOS - Schema completo
--  Ejecutar como root antes de levantar los servicios
--  mysql -u root -p < inmobiliaria_schema.sql
-- ============================================================

-- ============================================================
--  1. AUTH SERVICE  (puerto 8081)
-- ============================================================
CREATE DATABASE IF NOT EXISTS auth_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE auth_db;

CREATE TABLE IF NOT EXISTS auth_usuarios (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    username   VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    nombre     VARCHAR(150),
    role       VARCHAR(20)  NOT NULL DEFAULT 'USER',
    created_at DATETIME,
    PRIMARY KEY (id),
    UNIQUE KEY uk_auth_username (username),
    UNIQUE KEY uk_auth_email    (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
--  2. USUARIO SERVICE  (puerto 8082)
-- ============================================================
CREATE DATABASE IF NOT EXISTS usuario_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE usuario_db;

CREATE TABLE IF NOT EXISTS usuarios (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    nombre     VARCHAR(150) NOT NULL,
    apellido   VARCHAR(150),
    email      VARCHAR(150) NOT NULL,
    telefono   VARCHAR(30),
    direccion  VARCHAR(255),
    ciudad     VARCHAR(100),
    activo     TINYINT(1)   NOT NULL DEFAULT 1,
    created_at DATETIME,
    updated_at DATETIME,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
--  3. PROPIEDAD SERVICE  (puerto 8086)
-- ============================================================
CREATE DATABASE IF NOT EXISTS propiedad_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE propiedad_db;

CREATE TABLE IF NOT EXISTS propiedades (
    id                BIGINT          NOT NULL AUTO_INCREMENT,
    titulo            VARCHAR(255)    NOT NULL,
    descripcion       TEXT,
    precio            DECIMAL(15, 2)  NOT NULL,
    direccion         VARCHAR(255),
    ciudad            VARCHAR(100)    NOT NULL,
    pais              VARCHAR(100),
    habitaciones      INT,
    banos             INT,
    metros_cuadrados  DOUBLE,
    tipo              VARCHAR(20)     NOT NULL COMMENT 'VENTA | ALQUILER',
    estado            VARCHAR(20)     NOT NULL DEFAULT 'DISPONIBLE' COMMENT 'DISPONIBLE | VENDIDA | ALQUILADA | INACTIVA',
    propietario_id    BIGINT,
    created_at        DATETIME,
    updated_at        DATETIME,
    PRIMARY KEY (id),
    KEY idx_propiedad_ciudad  (ciudad),
    KEY idx_propiedad_estado  (estado),
    KEY idx_propiedad_tipo    (tipo),
    KEY idx_propiedad_propietario (propietario_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
--  4. RESERVA SERVICE  (puerto 8083)
-- ============================================================
CREATE DATABASE IF NOT EXISTS reserva_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE reserva_db;

CREATE TABLE IF NOT EXISTS reservas (
    id            BIGINT         NOT NULL AUTO_INCREMENT,
    propiedad_id  BIGINT         NOT NULL,
    usuario_id    BIGINT         NOT NULL,
    fecha_inicio  DATE           NOT NULL,
    fecha_fin     DATE           NOT NULL,
    estado        VARCHAR(20)    NOT NULL DEFAULT 'PENDIENTE' COMMENT 'PENDIENTE | CONFIRMADA | CANCELADA | COMPLETADA',
    monto         DECIMAL(15, 2),
    comentario    TEXT,
    created_at    DATETIME,
    updated_at    DATETIME,
    PRIMARY KEY (id),
    KEY idx_reserva_usuario    (usuario_id),
    KEY idx_reserva_propiedad  (propiedad_id),
    KEY idx_reserva_estado     (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
--  5. RESENA SERVICE  (puerto 8084)
-- ============================================================
CREATE DATABASE IF NOT EXISTS resena_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE resena_db;

CREATE TABLE IF NOT EXISTS resenas (
    id             BIGINT   NOT NULL AUTO_INCREMENT,
    propiedad_id   BIGINT   NOT NULL,
    usuario_id     BIGINT   NOT NULL,
    nombre_usuario VARCHAR(150),
    calificacion   INT      NOT NULL COMMENT '1 a 5',
    comentario     TEXT,
    created_at     DATETIME,
    PRIMARY KEY (id),
    KEY idx_resena_propiedad (propiedad_id),
    KEY idx_resena_usuario   (usuario_id),
    CONSTRAINT chk_calificacion CHECK (calificacion BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
--  6. BUSQUEDA SERVICE  (puerto 8089)
--  No tiene entidades propias, solo necesita la base de datos
-- ============================================================
CREATE DATABASE IF NOT EXISTS busqueda_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


-- ============================================================
--  7. NOTIFICACION SERVICE  (puerto 8087)
-- ============================================================
CREATE DATABASE IF NOT EXISTS notificacion_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE notificacion_db;

CREATE TABLE IF NOT EXISTS notificaciones (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    usuario_id  BIGINT       NOT NULL,
    titulo      VARCHAR(255) NOT NULL,
    mensaje     TEXT         NOT NULL,
    tipo        VARCHAR(20)  NOT NULL DEFAULT 'SISTEMA' COMMENT 'RESERVA | RESENA | SISTEMA | PAGO | BIENVENIDA',
    leida       TINYINT(1)   NOT NULL DEFAULT 0,
    created_at  DATETIME,
    leida_at    DATETIME,
    PRIMARY KEY (id),
    KEY idx_notif_usuario       (usuario_id),
    KEY idx_notif_usuario_leida (usuario_id, leida)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
--  8. IMAGEN SERVICE  (puerto 8088)
-- ============================================================
CREATE DATABASE IF NOT EXISTS imagen_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE imagen_db;

CREATE TABLE IF NOT EXISTS imagenes (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    propiedad_id   BIGINT       NOT NULL,
    url            VARCHAR(500) NOT NULL,
    nombre         VARCHAR(255),
    tipo_mime      VARCHAR(100),
    tamanio_bytes  BIGINT,
    principal      TINYINT(1)   NOT NULL DEFAULT 0,
    descripcion    TEXT,
    created_at     DATETIME,
    PRIMARY KEY (id),
    KEY idx_imagen_propiedad          (propiedad_id),
    KEY idx_imagen_propiedad_principal (propiedad_id, principal)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
--  9. REPORTE SERVICE  (puerto 8085)
-- ============================================================
CREATE DATABASE IF NOT EXISTS reporte_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE reporte_db;

CREATE TABLE IF NOT EXISTS reportes (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    titulo       VARCHAR(255) NOT NULL,
    tipo         VARCHAR(20)  NOT NULL COMMENT 'PROPIEDADES | RESERVAS | INGRESOS | GENERAL',
    descripcion  VARCHAR(500),
    datos        TEXT,
    generado_por BIGINT,
    generado_en  DATETIME,
    PRIMARY KEY (id),
    KEY idx_reporte_tipo (tipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
--  Verificar bases de datos creadas
-- ============================================================
SHOW DATABASES LIKE '%_db';