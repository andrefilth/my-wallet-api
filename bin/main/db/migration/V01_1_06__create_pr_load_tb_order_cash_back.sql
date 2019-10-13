use db_wallet_v2;

DROP PROCEDURE IF EXISTS db_wallet_v2.pr_load_tb_order_cash_back;

DELIMITER $$

CREATE  PROCEDURE db_wallet_v2.pr_load_tb_order_cash_back (IN p_wallet_id INT, IN p_wallet_uuid VARCHAR(255))
BEGIN
    /*
        Insere as ordens de cashback que foram geradas para a wallet alvo
    */
	insert into db_wallet_v2.tb_order (
			  uuid
			, type
			, status
			, total_amount_in_cents
			, title
			, description
			, order_detail_uuid
			, authorization_method
			, created_by_wallet_id
			, latest
			, reference_order_uuid
			, secondary_id
			, created_at
			, updated_at
			, payment_methods
	)
	select wt.operation_code 							as uuid
		 , 'CASH_BACK' 									as type
		 , case
				when wt.status = 'SUCCESS'      then 'CAPTURED'
				when wt.status = 'HOLD'	        then 'AUTHORIZED'
				when wt.status = 'CANCELED'	   	then 'CANCELLED'
				when wt.status = 'DENIED'	   	then 'CANCELLED'
		   end 											as status
		 , wt.amount 									as total_amount_in_cents
		 , wt.title 									as title
		 , wt.description 								as description
		 , wt.operation_code 							as order_detail_uuid
		 , 'NONE' 										as authorization_method
		 , wt.wallet_peer 								as created_by_wallet_id
		 , 1 											as latest
		 , wt.operation_reference 						as reference_order_uuid
		 , uuid() 										as secondary_id
		 , wt.operation_date 		 			     	as created_at
		 , ifnull(wt.updated_at, wt.operation_date)		as updated_at
		 , 'CASH' as payment_methods
	  from db_wallet.TB_WALLET_TRANSACTION wt
	 where type = 'CASH_BACK'
       -- and wt.wallet_id in (359)
       and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = wt.wallet_id)
	;

	commit;

	-- Insere as transacoes de debito na carteira do lojista referente a ordem de cashback
    insert into db_wallet_v2.tb_transaction (
          uuid
        , order_id
        , order_uuid
        , action_id
        , wallet_id
        , status
        , type
        , payment_method
        , peer_wallet_id
        , peer_transaction_uuid
        , manager_wallet_id
        , amount_in_cents
        , latest
        , created_at
        , updated_at
    )
    select uuid() 							as uuid
         , ord.id 							as order_id
         , ord.uuid 						as order_uuid
         , 1 								as action_id
         , ord.created_by_wallet_id 		as wallet_id
         , ord.status 						as status
         , 'DEBIT' 							as type
         , 'CASH' 							as payment_method
         , wt.wallet_id						as peer_wallet_id -- Eh a propria carteira alvo
         , null 							as peer_transaction_uuid
         , 1500000 							as manager_wallet_id
         , ord.total_amount_in_cents 		as amount_in_cents
         , 1 								as latest
         , ord.created_at 					as created_at
         , ord.updated_at 					as updated_at
      from db_wallet_v2.tb_order ord
      --
      join db_wallet.TB_WALLET_TRANSACTION wt
        on wt.operation_code = ord.uuid
     where ord.type = 'CASH_BACK'
       and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = wt.wallet_id)
    ;

    commit;


    -- Insere as transacoes de credito na carteira do recebedor do cashback
    insert into db_wallet_v2.tb_transaction (
          uuid
        , order_id
        , order_uuid
        , action_id
        , wallet_id
        , status
        , type
        , payment_method
        , peer_wallet_id
        , peer_transaction_uuid
        , manager_wallet_id
        , amount_in_cents
        , latest
        , created_at
        , updated_at
    )
    select uuid() 							as uuid
         , ord.id 							as order_id
         , ord.uuid 						as order_uuid
         , 1 								as action_id
         , wt.wallet_id						as wallet_id -- Eh a propria carteira alvo
         , ord.status 						as status
         , 'CREDIT' 						as type
         , 'CASH' 							as payment_method
         , ord.created_by_wallet_id 		as peer_wallet_id
         , null 							as peer_transaction_uuid
         , 1500000 							as manager_wallet_id
         , ord.total_amount_in_cents 		as amount_in_cents
         , 1 								as latest
         , ord.created_at 					as created_at
         , ord.updated_at 					as updated_at
      from db_wallet_v2.tb_order ord
      --
      join db_wallet.TB_WALLET_TRANSACTION wt
        on wt.operation_code = ord.uuid
      --
     where ord.type = 'CASH_BACK'
       -- and wt.wallet_id in (p_wallet_id)
       and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = wt.wallet_id)
    ;

    commit;

    /*
		Popula a tabel cash_transaction baseado nas transacoes que foram inseridas referentes aos cashbacks
	*/
	 insert into db_wallet_v2.tb_cash_transaction (
		  wallet_id
		, transaction_id
		, amount_in_cents
		, status
		, transaction_type
		, created_at
		, updated_at
	)
	select tr.wallet_id 		as wallet_id
		 , tr.id 				as transaction_id
		 , tr.amount_in_cents	as amount_in_cents
		 , tr.status 			as status
		 , tr.type 				as transaction_type
		 , tr.created_at 		as created_at
		 , tr.updated_at 		as updated_at
	  from db_wallet_v2.tb_transaction tr
	  --
	  join db_wallet_v2.tb_order ord
		on ord.id = tr.order_id and ord.latest is true
	  --
	 where tr.latest is true
	   and tr.payment_method = 'CASH'
	   and ord.type = 'CASH_BACK'
       and (
			exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = tr.wallet_id)
            or
            exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = tr.peer_wallet_id)
		)
	;

	commit;

END$$

DELIMITER ;