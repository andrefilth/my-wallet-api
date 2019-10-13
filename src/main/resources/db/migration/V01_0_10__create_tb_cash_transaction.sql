CREATE TABLE IF NOT EXISTS tb_cash_transaction (
    id BIGINT (30) PRIMARY KEY AUTO_INCREMENT,
    wallet_id BIGINT (30) NOT NULL,
    transaction_id BIGINT (30) NOT NULL,
    amount_in_cents BIGINT (30) NOT NULL,
    status ENUM('AUTHORIZED', 'CREATED', 'CAPTURED', 'ERROR', 'CANCELLED', 'RELEASED', 'REFUNDED', 'PENDING', 'DENIED') NOT NULL,
    transaction_type ENUM('DEBIT','CREDIT'),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (amount > 0)
);


ALTER TABLE tb_cash_transaction ADD FOREIGN KEY fk_cash_transaction (transaction_id) REFERENCES tb_transaction (id);
ALTER TABLE tb_cash_transaction ADD FOREIGN KEY fk_cash_transaction_wallet (wallet_id) REFERENCES tb_wallet (id);