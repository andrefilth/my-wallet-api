CREATE TABLE IF NOT EXISTS tb_creditcard (
    id BIGINT (30) PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(255) NOT NULL,
    token VARCHAR(255),
    hash VARCHAR(255),
    holder VARCHAR(255) NOT NULL,
    masked_number VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    exp_date VARCHAR(7) NOT NULL,
    main BOOLEAN DEFAULT FALSE NOT NULL,
    wallet_id BIGINT (30) NOT NULL,
    active BOOLEAN DEFAULT true NOT NULL,
    verified_by_ame BOOLEAN DEFAULT false NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_creditcard_token ON tb_creditcard (token);

CREATE UNIQUE INDEX idx_creditcard_uudi ON tb_creditcard (uuid);

CREATE INDEX idx_creditcard_wallet_id ON tb_creditcard (wallet_id);

ALTER TABLE tb_creditcard ADD FOREIGN KEY fk_creditcard_wallet (wallet_id) REFERENCES tb_wallet(id);
