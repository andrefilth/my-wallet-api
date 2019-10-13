use db_wallet_v2;

DROP PROCEDURE IF EXISTS db_wallet_v2.pr_load_tb_order_cash_in;

DELIMITER $$

CREATE PROCEDURE db_wallet_v2.pr_load_tb_order_cash_in (IN p_wallet_id INT, IN p_wallet_uuid VARCHAR(255))

BEGIN

--    insert into tb_action (id, parent_action_id, type, order_id, order_uuid) values (1, null, 'MIGRATION', null, null)
--    on duplicate key update type = 'MIGRATION';


	-- Insere as ordens de cash in referente a umas carteira
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
	-- Seleciona todas as orderns de DEBITO do tipo CASH_IN
    select wt.operation_code 									as uuid
         , 'CASH_IN'		 									as type
         , case
				when wt.status = 'SUCCESS'  then 'CAPTURED'
                when wt.status = 'DENIED'   then 'DENIED'
                when wt.status = 'CANCELED' then 'CANCELLED'
                when wt.status = 'AUTHORIZED' then 'AUTHORIZED'
           end 													as status
         , wt.amount 											as total_amount_in_cents
         , wt.title 											as title
         , wt.description 										as description
         , wt.operation_code									as order_detail_uuid
         , 'QRCODE' 											as authorization_method
         , wt.wallet_id 										as created_by_wallet_id
         , 1 													as latest
         , null 												as reference_order_uuid
         , null 												as secondary_id
         , wt.operation_date 									as created_at
         , ifnull(updated_at, operation_date) 					as updated_at
         , 'CREDIT_CARD' as payment_methods
      from db_wallet.TB_WALLET_TRANSACTION wt
	 where wt.type = 'CASH_IN'
       and wt.cash_type = 'CARD'
       and wt.operation_type = 'CREDIT'
       and exists  ( select wtm.wallet_id from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = wt.wallet_id )
    ;

    commit;




    -- Insere as transaçoes de DEBITO referentes aos cash_ins realizados pelas carteira alvo.
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
    select uuid() 												as uuid
		 , ord.id 												as order_id
         , ord.uuid 											as order_uuid
         , 1 													as action_id
         , ord.created_by_wallet_id 							as wallet_id
         , case
				when ord.status = 'CAPTURED'    then 'CAPTURED'
                when ord.status = 'DENIED'	    then 'DENIED'
                when ord.status = 'CANCELLED'	 then 'CANCELLED'
                when ord.status = 'AUTHORIZED'	 then 'AUTHORIZED'
           end 													as status
         , 'DEBIT' 												as type
         , 'CREDIT_CARD' 										as payment_method
         , ord.created_by_wallet_id 							as peer_wallet_id
         , null 												as peer_transaction_uuid
         , 1500000												as manager_wallet_id
         , ord.total_amount_in_cents 							as amount_in_cents
         , 1 													as latest
         , ord.created_at	 									as created_at
         , ord.updated_at 										as updated_at
      from db_wallet_v2.tb_order ord
     where ord.type = 'CASH_IN'
	   -- and created_by_wallet_id  in (349)
       and exists  ( select wtm.wallet_id from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = created_by_wallet_id )
    ;

    commit;


    -- Popula a tb_creditcard_transaction referente aos cash_in da carteira alvo
    insert into db_wallet_v2.tb_creditcard_transaction (
		  transaction_id
		, credit_card_id
		, credit_card_status
		, gateway_order_reference
		, gateway_payment_reference
		, acquirer
		, tid
		, nsu
		, authorization_nsu
		, capture_nsu
		, cancel_nsu
		, number_of_installments
		, installment_type
		, authorization_code
		, authorization_tid
		, capture_tid
		, cancel_tid
		, holder_name
		, masked_number
		, brand
		, expiration_month
		, expiration_year
		, currency
		, authorization_date
		, capture_date
		, cancel_date
		, refund_date
		, gateway_cancellation_reference
		, gateway_response_message
		, gateway_response_code
		, created_at
		, updated_at
	) select tr.id 		  								    as transaction_id
           , ca.card_token 									as credit_card_id
           , case
				when ws.status = 'DENIED'   	then 'UNAUTHORIZED'
                when ws.status = 'SUCCESS'  	then 'CAPTURED'
                when ws.status = 'CANCELED' 	then 'CANCELLED'
                when ws.status = 'HOD' 	 		then 'PENDING'
                when ws.status = 'AUTHORIZED'	then 'AUTHORIZED'
                when ws.status = 'REFUNDED'		then 'REFUNDED'
                when ws.status = 'ERROR' 		then 'UNEXPECTED_ERROR' -- TODO: Verificar se esta com erro esta transacao
            end 		  									as credit_card_status
		   , ord.uuid 										as gateway_order_reference
           , ca.payment_id 									as gateway_payment_reference
		   , null 											as acquirer
		   , ca.tid 										as tid
           , ca.nsu 										as nsu
           , ca.nsu 										as authorization_nsu -- TODO Atualizar de acordo com status da transaçao
           , ca.nsu											as capture_nsu -- TODO Atualizar de acordo com status da transaçao
		   , ca.nsu											as cancel_nsu  -- TODO Atualizar de acordo com status da transaçao
           , ca.installments 								as number_of_installments
           , null 											as installment_type
           , authorization_code 							as authorization_code
           , ca.tid 										as authorization_tid -- TODO Atualizar de acordo com status da transaçao
		   , ca.tid 										as capture_tid -- TODO Atualizar de acordo com status da transaçao
		   , ca.tid 										as cancel_tid -- TODO Atualizar de acordo com status da transaçao
           , null 											as holder_name
		   , card_masked 									as masked_number
		   , ca.card_brand 									as brand
           , null 											as expiration_month
		   , null 											as expiration_year
		   , 'BRL' 											as currency
           , ws.date 										as authorization_date
		   , ws.date 										as capture_date
           , ws.date 										as cancel_date
		   , ws.date 										as refund_date
           , ca.cancel_id 									as gateway_cancellation_reference
		   , ca.response_message 							as gateway_response_message
		   , ca.response_code 								as gateway_response_code
		   , ws.date 										as created_at
		   , ws.date 										as updated_at
	  from db_wallet_v2.tb_order ord
	  --
	  join db_wallet_v2.tb_transaction tr
		on tr.order_id = ord.id
	  --
	  join db_wallet.TB_WALLET_TRANSACTION wt
		on 	   wt.operation_code = ord.uuid
--		   and wt.wallet_id = p_wallet_id
		   and wt.wallet_id = tr.wallet_id
		   and wt.type = 'CASH_IN'
		   and wt.cash_type = 'CARD'
		   and wt.operation_type = 'CREDIT'
	  --
	  join db_wallet.TB_WALLET_TRANSACTION_SPLIT ws
		on ws.transaction_id = wt.id
	  --
	  join db_wallet.TB_CARD_AUTHORIZATION ca
		on     ca.id = ws.card_authorization_id
		   and ws.cash_type = 'CARD'
	  --
	 where -- tr.wallet_id = p_wallet_id
	        tr.payment_method = 'CREDIT_CARD'
	       and exists  ( select wtm.wallet_id from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = tr.wallet_id )
	;
	commit;

    -- Insere as ordens de liquidaçao dos Cash In`s
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
    select uuid() 					as uuid
         , 'RELEASE' 				as type
         , 'RELEASED' 				as status
         , total_amount_in_cents 	as total_amount_in_cents
         , title 					as title
         , description				as description
         , null 					as order_detail_uuid
         , 'NONE' 					as authorization_method
         , 1500000 					as created_by_wallet_id
         , 1 						as latest
         , uuid 					as reference_order_uuid
         , uuid()					as secondary_id
         , created_at				as created_at
         , updated_at				as updated_at
         , payment_methods 			as payment_methods
	  from db_wallet_v2.tb_order ord
     where ord.status = 'CAPTURED'
       and ord.type = 'CASH_IN'
       -- and ord.created_by_wallet_id = 4
       and exists  ( select wtm.wallet_id from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = created_by_wallet_id )
    ;
    commit;


    -- Insere as transaçoes de credito referente a ordem de liquidaçao do Cash In
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
    select uuid() 															as uuid
		 , release_order.id 												as order_id
         , release_order.uuid 												as order_uuid
         , 1 																as action_id
         , ref.created_by_wallet_id			 								as wallet_id
         , 'CAPTURED'														as status
         , 'CREDIT' 														as type
         , 'CASH' 															as payment_method
         , ref.created_by_wallet_id 										as peer_wallet_id
         , tr.uuid 															as peer_transaction_uuid
         , 1500000															as manager_wallet_id
         , release_order.total_amount_in_cents 								as amount_in_cents
         , 1 																as latest
         , release_order.created_at	 										as created_at
         , release_order.updated_at 										as updated_at
      from db_wallet_v2.tb_order release_order
      --
      join db_wallet_v2.tb_order ref
        on ref.uuid = release_order.reference_order_uuid
      --
      join db_wallet_v2.tb_transaction tr
		on tr.order_id = ref.id
      --
     where release_order.type = 'RELEASE'
       and tr.status = 'CAPTURED'
	   and release_order.created_by_wallet_id = 1500000
       and ref.type = 'CASH_IN'
       -- and ref.created_by_wallet_id = p_wallet_id
       and exists  ( select wtm.wallet_id from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = ref.created_by_wallet_id )
    ;

    commit;

    -- Insere dados na Cash Transaction
    insert into db_wallet_v2.tb_cash_transaction (
		  wallet_id
		, transaction_id
		, amount_in_cents
		, status
		, transaction_type
		, created_at
		, updated_at
	)
	select	   tr.wallet_id 			as wallet_id
		 , tr.id 				as transaction_id
		 , tr.amount_in_cents	as amount_in_cents
		 , tr.status 			as status
		 , tr.type 				as transaction_type
		 , tr.created_at 		as created_at
		 , tr.updated_at 		as updated_at
		--
    from 	db_wallet_v2.tb_transaction tr
            --
            join 	db_wallet_v2.tb_order release_order
            on 	release_order.id = tr.order_id
            and 	release_order.type = 'RELEASE'
            and 	release_order.status = 'RELEASED'
            and 	release_order.created_by_wallet_id = 1500000
            --
            join 	db_wallet_v2.tb_order ref_order
            on 	release_order.reference_order_uuid = ref_order.uuid
            and 	ref_order.type = 'CASH_IN'
            and 	ref_order.status = 'CAPTURED'
            and 	ref_order.created_by_wallet_id = tr.wallet_id
		--
    where tr.status = 'CAPTURED'
    -- and tr.wallet_id = p_wallet_id
    and exists  ( select wtm.wallet_id from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = tr.wallet_id )
	;

    commit;


END$$

DELIMITER ;