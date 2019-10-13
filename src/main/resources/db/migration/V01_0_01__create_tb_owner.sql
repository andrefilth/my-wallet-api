CREATE TABLE IF NOT EXISTS tb_owner (
    id BIGINT (30) PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    document VARCHAR(255) NOT NULL,
    document_type ENUM('CNPJ', 'CPF') NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    external_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Indices
CREATE UNIQUE INDEX idx_tb_owner_uuid ON tb_owner (uuid);
CREATE UNIQUE INDEX idx_tb_owner_document ON tb_owner (document);
CREATE UNIQUE INDEX idx_tb_owner_email ON tb_owner (email);
