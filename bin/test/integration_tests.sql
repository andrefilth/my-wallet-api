CREATE TABLE IF NOT EXISTS tb_owner (
    id BIGINT (30) PRIMARY KEY IDENTITY,
    uuid VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    document VARCHAR(255) NOT NULL,
    document_type VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    external_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Indices
CREATE UNIQUE INDEX idx_tb_owner_uuid ON tb_owner (uuid);
CREATE UNIQUE INDEX idx_tb_owner_document ON tb_owner (document);
CREATE UNIQUE INDEX idx_tb_owner_email ON tb_owner (email);

CREATE TABLE IF NOT EXISTS tb_wallet (
    id BIGINT (30) PRIMARY KEY IDENTITY,
    uuid VARCHAR(255) NOT NULL,
    owner_id BIGINT (30) NOT NULL,
    type VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    main BOOLEAN NOT NULL DEFAULT TRUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES tb_owner (id)
);

-- Indices
CREATE UNIQUE INDEX idx_tb_wallet_uuid ON tb_wallet (uuid);
CREATE UNIQUE INDEX idx_tb_wallet_owner_id_main ON tb_wallet (owner_id, main);

-- Inset Angelina Owner
insert into tb_owner (uuid, name, email, document, document_type, external_id) VALUES ('b2e6d5e1-fe99-4ffe-9c7b-addf8c52d408','Angelina Jolie','angel.jolie@gmail.com','92421096812','CPF','740ebe7a-e266-4c82-8887-db6e8109bb42');
insert into tb_owner (uuid, name, email, document, document_type, external_id) VALUES ('54d16c35-73a2-4d55-96a7-33398e27cb65','Moranguinho Amigao','morango.amigao@gmail.com','56922496000','CPF','51b9aa7e-0228-4769-a57e-97a96b807ed2');

-- Inset Angelina Wallet
insert into tb_wallet(owner_id, uuid, type, name) VALUES (0, 'ed4f4c6c-fc5d-43f0-a878-5288613f8179', 'CUSTOMER', 'Carteira Principal');
insert into tb_wallet(owner_id, uuid, type, name) VALUES (1, 'cdeb3497-71a9-4db5-b60d-1e60098b106d', 'CUSTOMER', 'Carteira Principal');