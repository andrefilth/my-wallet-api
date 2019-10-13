CREATE TABLE IF NOT EXISTS tb_wallet (
    id BIGINT (30) PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(255) NOT NULL,
    owner_id BIGINT (30) NOT NULL,
    type ENUM('MERCHANT','CUSTOMER', 'MANAGER') NOT NULL,
    name VARCHAR(255) NOT NULL,
    main BOOLEAN NOT NULL DEFAULT TRUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



-- Forein Keys
ALTER TABLE tb_wallet ADD FOREIGN KEY fk_owner_id (owner_id) REFERENCES tb_owner (id);

-- Indices
CREATE UNIQUE INDEX idx_tb_wallet_uuid ON tb_wallet (uuid);
CREATE UNIQUE INDEX idx_tb_wallet_owner_id_main ON tb_wallet (owner_id, main);



insert into tb_owner (id, uuid, name, email, document, document_type, external_id) VALUES (1500000, '8eabeba1-87a7-4314-bd93-9f6f98820b8e','Ame Digital','ame@amedigital.com','82742140000108','CNPJ', '8eabeba1-87a7-4314-bd93-9f6f98820b8e')
    on duplicate key update
        uuid = '8eabeba1-87a7-4314-bd93-9f6f98820b8e',
        name = 'Ame Digital',
        email = 'ame@amedigital.com',
        document = '82742140000108',
        document_type = 'CNPJ',
        external_id = '8eabeba1-87a7-4314-bd93-9f6f98820b8e'
    ;


    insert into tb_wallet(id, owner_id, uuid, type, name) VALUES (1500000, 1500000, 'd52a20cb-7329-4239-805c-eae1c485be30', 'MANAGER', 'Carteira Principal')
	on duplicate key update
		owner_id = 1500000,
        uuid = 'd52a20cb-7329-4239-805c-eae1c485be30',
        type = 'MANAGER',
        name = 'Carteira Principal'
    ;