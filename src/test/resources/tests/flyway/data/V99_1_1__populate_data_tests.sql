
insert into tb_owner (id, uuid, name, email, document, document_type, external_id) VALUES 
(1500001, '2926a5d1-e6f5-441a-888e-f75d3b755039','Teste Integrado Owner','teste@teste.com.br','22809993823','CPF', '2926a5d1-e6f5-441a-888e-f75d3b755039')
    on duplicate key update
        uuid = '2926a5d1-e6f5-441a-888e-f75d3b755039',
        name = 'Teste Integrado Owner',
        email = 'teste@teste.com.br',
        document = '22809993823',
        document_type = 'CPF',
        external_id = '2926a5d1-e6f5-441a-888e-f75d3b755039'
;


insert into tb_wallet(id, owner_id, uuid, type, name) VALUES (1500001, 1500001, '915918fc-0b28-48ed-88a2-5372413e19b5', 'CUSTOMER', 'Carteira Teste Wallet')
on duplicate key update
	owner_id = 1500001,
    uuid = '915918fc-0b28-48ed-88a2-5372413e19b5',
    type = 'CUSTOMER',
    name = 'Carteira Teste Wallet'
;