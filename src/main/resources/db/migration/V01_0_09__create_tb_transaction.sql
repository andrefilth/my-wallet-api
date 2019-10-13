CREATE TABLE IF NOT EXISTS tb_transaction (
    id BIGINT (30) PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(255) NOT NULL,
    order_id BIGINT (30) NOT NULL,
    order_uuid VARCHAR(255) NOT NULL,
    action_id BIGINT (30) NOT NULL,
    wallet_id BIGINT (30) NOT NULL,
    status ENUM('CREATED', 'AUTHORIZED', 'DENIED', 'CAPTURED', 'ERROR', 'CANCELLED', 'RELEASED', 'REFUNDED', 'PENDING') NOT NULL,
    type ENUM('CREDIT', 'DEBIT') NOT NULL,
    payment_method ENUM('CREDIT_CARD', 'CASH') NOT NULL,
    peer_wallet_id BIGINT (30) NOT NULL,
    peer_transaction_uuid VARCHAR(255),
    manager_wallet_id BIGINT (30) NOT NULL,
    amount_in_cents BIGINT (30) NOT NULL,
    take_rate DECIMAL (7,2),
    release_date TIMESTAMP,
    latest BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (amountInCents > 0)
);

CREATE INDEX idx_transaction_uuid ON tb_transaction (uuid, latest);
CREATE INDEX idx_transaction_wallet_id ON tb_transaction (wallet_id);
CREATE INDEX idx_transaction_payment_method_status ON tb_transaction (payment_method, status);
CREATE INDEX idx_transaction_order_uuid ON tb_transaction (order_uuid);
CREATE INDEX idx_transaction_order_id_latest ON tb_transaction (order_id, latest);
CREATE INDEX idx_transaction_peer_transfer_uuid ON tb_transaction (peer_transaction_uuid);

ALTER TABLE tb_transaction ADD FOREIGN KEY fk_transaction_action (action_id) REFERENCES tb_action (id);
ALTER TABLE tb_transaction ADD FOREIGN KEY fk_transaction_order  (order_id) REFERENCES tb_order (id);
ALTER TABLE tb_transaction ADD FOREIGN KEY fk_transaction_wallet (wallet_id) REFERENCES tb_wallet (id);
ALTER TABLE tb_transaction ADD FOREIGN KEY fk_transaction_peer_wallet_id (peer_wallet_id) REFERENCES tb_wallet (id);
ALTER TABLE tb_transaction ADD FOREIGN KEY fk_transaction_manager_wallet (manager_wallet_id) REFERENCES tb_wallet (id);