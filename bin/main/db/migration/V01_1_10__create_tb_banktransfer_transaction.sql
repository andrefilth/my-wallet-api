CREATE TABLE IF NOT EXISTS tb_banktransfer_transaction (
    id BIGINT (30) PRIMARY KEY AUTO_INCREMENT,
    wallet_id BIGINT (30) NOT NULL,
    transaction_id BIGINT (30) NOT NULL,
    amount_in_cents BIGINT (30) NOT NULL,
    bank_transfer_status ENUM('AUTHORIZED', 'CREATED', 'CAPTURED', 'ERROR', 'CANCELLED', 'RELEASED', 'REFUNDED', 'PENDING', 'DENIED') NOT NULL,
    transaction_type ENUM('DEBIT','CREDIT'),
    created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    client_name VARCHAR(255),
    client_email VARCHAR(255),
    client_phone VARCHAR(255),
    client_cpf VARCHAR(255),
    bank INT NOT NULL,
    agency VARCHAR(255) NOT NULL,
    account_number VARCHAR(255) NOT NULL,
    tax_applied BIGINT (30),
    FOREIGN KEY fk_banktransfer_transaction (transaction_id) REFERENCES tb_transaction (id),
    FOREIGN KEY fk_banktransfer_wallet (wallet_id) REFERENCES tb_wallet (id)
);

ALTER TABLE tb_order MODIFY COLUMN type enum('PURCHASE', 'TRANSFER_BETWEEN_WALLETS', 'CASH_IN', 'RELEASE', 'REFUND', 'CASH_BACK', 'STORE_PURCHASE', 'GIFT_CASH_IN', 'CASH_OUT');

ALTER TABLE tb_transaction MODIFY COLUMN payment_method ENUM('CREDIT_CARD', 'CASH', 'BANK_TRANSFER');