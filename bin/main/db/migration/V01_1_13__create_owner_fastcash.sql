
insert into tb_owner (id, uuid, name, email, document, document_type, external_id) VALUES 
(1499999, 'dace6210-ebdf-4f4d-8621-53eca4024395','Fast Cash','fastcash@fastcash.com.br','11173146000155','CNPJ', 'dace6210-ebdf-4f4d-8621-53eca4024395')
    on duplicate key update
        uuid = 'dace6210-ebdf-4f4d-8621-53eca4024395',
        name = 'Fast Cash',
        email = 'fastcash@fastcash.com.br',
        document = '11173146000155',
        document_type = 'CNPJ',
        external_id = 'dace6210-ebdf-4f4d-8621-53eca4024395'
;


insert into tb_wallet(id, owner_id, uuid, type, name) VALUES (1499999, 1499999, '73e2209a-cf4b-44cb-9775-f0cbfc292fc3', 'MANAGER', 'Carteira FastCash')
on duplicate key update
	owner_id = 1499999,
    uuid = '73e2209a-cf4b-44cb-9775-f0cbfc292fc3',
    type = 'MANAGER',
    name = 'Carteira FastCash'
;


ALTER TABLE tb_order MODIFY COLUMN type enum('PURCHASE', 'TRANSFER_BETWEEN_WALLETS', 'CASH_IN', 'RELEASE', 'REFUND', 'CASH_BACK', 'STORE_PURCHASE', 'GIFT_CASH_IN', 'CASH_OUT', 'STORE_CASH_IN');