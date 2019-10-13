CREATE TABLE IF NOT EXISTS tb_wallet_to_migrate (
    wallet_id BIGINT (30) PRIMARY KEY,
    wallet_uuid VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_tb_wallet_uuid ON tb_wallet_to_migrate (wallet_uuid);
ALTER TABLE tb_wallet_to_migrate ADD FOREIGN KEY fk_to_migrate (wallet_id) REFERENCES tb_wallet(id);
