CREATE TABLE IF NOT EXISTS tb_migrated_wallet (
    wallet_id BIGINT (30) PRIMARY KEY,
    wallet_uuid VARCHAR(255),
    purchase_migrated BOOLEAN NOT NULL DEFAULT false,
    transfer_migrated BOOLEAN NOT NULL DEFAULT false,
    cash_in_migrated BOOLEAN NOT NULL DEFAULT false,
    refund_migrated BOOLEAN NOT NULL DEFAULT false,
    cashback_migrated BOOLEAN NOT NULL DEFAULT false,
    gift_migrated BOOLEAN NOT NULL DEFAULT false,
    creditcards_migrated BOOLEAN NOT NULL DEFAULT false,
    checked BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_migrated_wallet_uuid ON tb_migrated_wallet (wallet_uuid);
ALTER TABLE tb_migrated_wallet ADD FOREIGN KEY fk_migrated_wallet (wallet_id) REFERENCES tb_wallet(id);
