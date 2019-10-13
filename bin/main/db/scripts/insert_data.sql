-- Insert Angelina Owner
insert into tb_owner (uuid, name, email, document, document_type, external_id) VALUES ('b2e6d5e1-fe99-4ffe-9c7b-addf8c52d408','Angelina Jolie','angel.jolie@gmail.com','92421096812','CPF','740ebe7a-e266-4c82-8887-db6e8109bb42');

-- Insert Angelina Wallet
insert into tb_wallet(owner_id, uuid, type, name) VALUES (800001, 'b2e6d5e1-fe99-4ffe-9c7b-addf8c52d408', 'CUSTOMER', 'Carteira Principal');

-- ##########################
-- Insert ACOM Wallet
insert into tb_owner (uuid, name, email, document, document_type, external_id) VALUES ('b137365c-1e8f-4108-98c3-eb32f847de1c','Americanas.com','suporte@americanas.com','76259402000177','CNPJ','58fb3bfa-8424-4307-babe-c7beb5436626');

-- Insert ACOM Wallet
insert into tb_wallet(owner_id, uuid, type, name) VALUES (800002, 'ede9d9f4-e09d-44e2-b108-79c262856064', 'MERCHANT', 'Carteira Principal');

-- Insert Emma Stone Owner
insert into tb_owner (uuid, name, email, document, document_type, external_id) VALUES ('3062c811-3d79-4166-b639-3110916757c7','Emma Stone','emma.stone@gmail.com','94868534289','CPF','740ebe7a-e266-4c82-8887-db6e8109bb43');

-- Insert Emma Stone Wallet
insert into tb_wallet(owner_id, uuid, type, name) VALUES (800003, '551a1080-d9b7-46bc-9f3e-dbda4f953fa9', 'CUSTOMER', 'Carteira Principal');

-- Insert
insert into tb_creditcard(id, uuid, token, hash, holder, masked_number, brand, exp_date, main, wallet_id, created_at, updated_at, active) VALUES (800001, '7c075fd3-2779-4698-9f8c-e2b47717423f', null , null, 'BILL GATEWAY', "12############12", 'VISA', '11/2020', true, 800001,  '2018-11-01 13:00:00', '2008-11-01 13:00:00', true);

commit;

-- ACOM gera ordem de compra
insert into tb_order (
	uuid,
    type,
    external_state,
    internal_state,
    total_amount,
    title,
    description,
    order_detail_uid,
    created_by_wallet_id
) VALUES (
	'2b3e7156-fcd3-44cc-9e51-d32964ac91b8',
    'PURCHASE',
    'CAPTURED',
    'CAPTURED',
    32500,
    'Compra Lojas Americanas',
    'Good Of War PS4',
    'ada0e918-e166-49de-9854-c517df307d75',
    4
);

insert into tb_statement (
	wallet_id,
    order_id,
    transaction_type,
    amount
) VALUES (
	1, -- Wallet Angelina
    1, -- Order Acom
    'DEBIT',
    32500
);

insert into tb_statement (
	wallet_id,
    order_id,
    transaction_type,
    amount
) VALUES (
	4, -- Wallet ACOM
    1, -- Order Acom
    'CREDIT',
    32500
);

commit;
