CREATE TABLE IF NOT EXISTS tb_order (
    id BIGINT (30) PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(255) NOT NULL,
    type ENUM('PURCHASE', 'TRANSFER_BETWEEN_WALLETS', 'CASH_IN', 'RELEASE', 'REFUND', 'CASH_BACK', 'STORE_PURCHASE', 'GIFT_CASH_IN', 'STORE_CASH_IN') NOT NULL,
    status VARCHAR(255) NOT NULL,
    total_amount_in_cents BIGINT (30) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    order_detail_uuid VARCHAR(255),
    authorization_method ENUM('NONE', 'QRCODE', 'BARCODE') NOT NULL,
    payment_methods VARCHAR(255),
    created_by_wallet_id BIGINT (30) NOT NULL,
    nsu VARCHAR (255),
    latest BOOLEAN NOT NULL DEFAULT TRUE,
    reference_order_uuid VARCHAR(255),
    secondary_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (total_amount_in_cents > 0)
);

CREATE INDEX idx_order_uuid ON tb_order (uuid);
CREATE INDEX idx_order_type ON tb_order (type);
CREATE INDEX idx_reference_order_uuid_and_compose_id ON tb_order (reference_order_uuid, secondary_id);

ALTER TABLE tb_order ADD FOREIGN KEY fk_wallet_id (created_by_wallet_id) REFERENCES tb_wallet (id);