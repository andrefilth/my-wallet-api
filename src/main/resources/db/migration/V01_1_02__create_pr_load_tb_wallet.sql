USE db_wallet_v2;

DROP PROCEDURE IF EXISTS db_wallet_v2.pr_load_tb_wallet;

DELIMITER $$

CREATE PROCEDURE db_wallet_v2.pr_load_tb_wallet()
BEGIN
	-- Insere todos os ownwer de wallets que sao do tipo CUSTOMER que ainda nao foram importadas.
    insert into db_wallet_v2.tb_wallet (
		     id
		   , uuid
		   , owner_id
		   , type
		   , name
		   , main
		   , active
		   , created_at
		   , updated_at
	) select w.id 				 as id
	       , w.code 			 as uuid
		   , owner_v2.id 		 as owner_id
		   , w.type 			 as type
		   , w.name 			 as name
		   , 1 					 as main
		   , 1 				     as active
		   , current_timestamp() as created_at
		   , current_timestamp() as updated_at
	   from db_wallet.TB_WALLET w
	   join db_wallet_v2.tb_owner owner_v2
         on w.document = owner_v2.document
      where not exists (
		select 1
          from db_wallet_v2.tb_wallet current_wallet
         where current_wallet.uuid = w.code
      )
	;
    
    commit;

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

    commit;
    
END$$

DELIMITER ;