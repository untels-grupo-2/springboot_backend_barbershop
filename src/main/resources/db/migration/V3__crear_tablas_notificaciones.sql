-- ============================================================================
-- PB-41 — Push notifications al administrador via Firebase Cloud Messaging.
-- Crea las tablas que el BC `notificaciones` necesita para:
--   1. Persistir los tokens FCM de los dispositivos de los admins.
--   2. Llevar el historial de notificaciones para que la app móvil pueda
--      listarlas y marcarlas como leídas.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Tokens FCM de dispositivos
--   - Un mismo usuario puede tener varios tokens (varios dispositivos).
--   - El token es único globalmente (lo genera Firebase).
--   - Si el usuario se elimina, sus tokens se borran (CASCADE).
-- ----------------------------------------------------------------------------
CREATE TABLE device_tokens_fcm (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    usuario_id  BIGINT       NOT NULL,
    token       VARCHAR(500) NOT NULL,
    plataforma  VARCHAR(20)  NOT NULL,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_device_tokens_token UNIQUE (token),
    CONSTRAINT fk_device_tokens_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(usuario_id) ON DELETE CASCADE,
    INDEX idx_device_tokens_usuario (usuario_id)
);

-- ----------------------------------------------------------------------------
-- Historial de notificaciones que el admin recibe.
-- Se persiste SIEMPRE, aunque el envío del push a FCM falle — así el admin
-- puede verla en la app cuando la abra, incluso si su dispositivo no recibió
-- la notificación en el momento.
-- ----------------------------------------------------------------------------
CREATE TABLE notificaciones_admin (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    titulo      VARCHAR(255) NOT NULL,
    cuerpo      TEXT         NOT NULL,
    tipo        VARCHAR(50)  NOT NULL,
    leida       BOOLEAN      DEFAULT FALSE,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_notificaciones_admin_leida (leida),
    INDEX idx_notificaciones_admin_created_at (created_at)
);
