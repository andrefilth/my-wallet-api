USE db_wallet_v2;

DROP PROCEDURE IF EXISTS db_wallet_v2.pr_load_tb_owner;

DELIMITER $$

CREATE PROCEDURE db_wallet_v2.pr_load_tb_owner()
BEGIN
	-- Insere todos os ownwer de wallets que sao do tipo CUSTOMER que ainda nao foram importadas.
    insert into db_wallet_v2.tb_owner (
		  uuid
		, name
		, email
		, document
		, document_type
		, active
		, external_id
		, created_at
		, updated_at
	) select -- id 				   as id
		   code 			   as uuid
		 , name 			   as name
		 , code 	   		   as email
		 , document 		   as document
		 , case when type = 'CUSTOMER' then 'CPF' else 'CNPJ' end as document_type
		 , true 			   as active
		 , owner_id 		   as external_id
		 , current_timestamp() as created_at
		 , current_timestamp() as updated_at
	from db_wallet.TB_WALLET w
    where not exists (
		select 1 from db_wallet_v2.tb_owner ow2 where ow2.document = w.document
    );
    
    commit;
    
END$$

DELIMITER ;