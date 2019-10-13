use db_wallet_v2;

DROP PROCEDURE IF EXISTS db_wallet_v2.pr_load_tb_order_purchase;

DELIMITER $$

CREATE  PROCEDURE db_wallet_v2.pr_load_tb_order_purchase (IN p_wallet_id INT, IN p_wallet_uuid VARCHAR(255))
BEGIN

    -- Cria as ordens de compra referente a carteira alvo.
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
         , nsu
    )
    select
        --
            wt.operation_code 										as uuid
         , 'PURCHASE' 												as type
         , case
                when wt.status = 'DENIED'		then 'DENIED'
                when wt.status = 'SUCCESS'		then 'CAPTURED'
                when wt.status = 'CANCELED'		then 'CANCELLED'
                when wt.status = 'AUTHORIZED'	then 'AUTHORIZED'
                when wt.status = 'REFUNDED' 	then 'CAPTURED'
           end 														as status
         , wt.amount												as total_amount_in_cents
         , wt.title													as title
         , wt.description											as description
         , wt.operation_code										as order_detail_uuid
         , 'QRCODE'													as authorization_method
         , wt. wallet_peer 											as created_by_wallet_id
         , '1'														as latest
         , null 													as reference_order_uuid
         , null 													as secondary_id
         , wt.operation_date 		 			   					as created_at
         , ifnull(wt.updated_at, wt.operation_date) 				as updated_at
         , case
                when wt.cash_type = 'CASH' then 'CASH'
                when wt.cash_type = 'CARD' then 'CREDIT_CARD'
                else 'CASH,CREDIT_CARD'
            end 													as payment_methods
         , wt.id
      from db_wallet.TB_WALLET_TRANSACTION wt
     where wt.type = 'PAYMENT'
       and wt.operation_type = 'DEBIT'
       and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = wt.wallet_id)
    ;

    commit;

    -- Insere as transaçoes referentes as ordens de compra
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
	select ws.code 													as uuid
		 , ord.id 													as order_id
		 , ord.uuid 												as order_uuid
		 , 1 														as action_id
		 , wt.wallet_id 											as wallet_id
		 , case
		        when wt.status = 'CANCELED' then 'CANCELLED'
		        else
                    case
                        when ws.status = 'DENIED' 		then 'DENIED'
                        when ws.status = 'SUCCESS' 		then 'CAPTURED'
                        when ws.status = 'CANCELED' 	then 'CANCELLED'
                        when ws.status = 'REFUNDED' 	then 'CAPTURED'
                        when ws.status = 'AUTHORIZED' 	then 'AUTHORIZED'
			        end
	     end                                                        as status
		  , 'DEBIT' 												as type
		  , case
				when ws.cash_type = 'CARD' then 'CREDIT_CARD'
				when ws.cash_type = 'CASH' then 'CASH'
			end														as payment_mehtod
		  , wt.wallet_peer 		                                    as peer_wallet_id
		  , null 													as peer_transaction_uuid
		  , 1500000 												as manager_wallet_id
		  , ws.amount 			                                    as amount_in_cents
		  , '1'                                   				    as latest
		  , ord.created_at		                                    as created_at
		  , ord.updated_at		                                    as updated_at
	  from db_wallet_v2.tb_order ord
	  --
	  join db_wallet.TB_WALLET_TRANSACTION wt
		on wt.operation_code = ord.uuid
	  --
	  join db_wallet.TB_WALLET_TRANSACTION_SPLIT ws
		on wt.id = ws.transaction_id
	  --
	 where ord.type = 'PURCHASE'
	   and wt.type = 'PAYMENT'
	   and exists  ( select wtm.wallet_id from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = wt.wallet_id )
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
           , ifnull(ca.card_token, uuid()) 					as credit_card_id
           , case
                when wt.status = 'CANCELED' then 'CANCELLED'
                else
                    case
                        when ws.status = 'DENIED'   	then 'UNAUTHORIZED'
                        when ws.status = 'SUCCESS'  	then 'CAPTURED'
                        when ws.status = 'CANCELED' 	then 'CANCELLED'
                        when ws.status = 'HOLD' 	 	then 'PENDING'
                        when ws.status = 'AUTHORIZED'	then 'AUTHORIZED'
                        when ws.status = 'REFUNDED'		then 'REFUNDED'
                        when ws.status = 'ERROR' 		then 'UNEXPECTED_ERROR' -- TODO: Verificar se esta com erro esta transacao
                    end
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
		   and wt.wallet_id = tr.wallet_id
		   and wt.type = 'PAYMENT'
		   and wt.operation_type = 'DEBIT'
	  --
	  join db_wallet.TB_WALLET_TRANSACTION_SPLIT ws
		on ws.transaction_id = wt.id
	  --
	  join db_wallet.TB_CARD_AUTHORIZATION ca
		on     ca.id = ws.card_authorization_id
		   and ws.cash_type = 'CARD'
	  --
	 where exists  ( select wtm.wallet_id from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = tr.wallet_id )
       and ord.type = 'PURCHASE'
       and wt.type = 'PAYMENT'
       and tr.payment_method = 'CREDIT_CARD'

	;

	commit;

	insert into db_wallet_v2.tb_cash_transaction (
		   wallet_id
		 , transaction_id
		 , amount_in_cents
		 , status
		 , transaction_type
		 , created_at
		 , updated_at
	)
	select	   tr.wallet_id			as wallet_id
             , tr.id 				as transaction_id
             , tr.amount_in_cents	as amount_in_cents
             , tr.status 			as status
             , tr.type 				as transaction_type
             , tr.created_at 		as created_at
             , tr.updated_at 		as updated_at
            --
      from 	db_wallet_v2.tb_transaction tr
            --
            join db_wallet_v2.tb_order ord
            on ord.id = tr.order_id
            and ord.type = 'PURCHASE'
            --
    where 	tr.payment_method = 'CASH'
    and		tr.status != 'RELEASED'
    and exists  ( select wtm.wallet_id from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = tr.wallet_id )

    ;

    commit;



    -- Insere as ordens de liquidaçao referente as ordens de compra
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
	select distinct uuid()									as uuid
          , 'RELEASE'								as type
          , 'RELEASED'								as status
          , purchase_order.total_amount_in_cents	as total_amount_in_cents
          , purchase_order.title 					as title
          , purchase_order.description				as description
          , null 									as order_detail_uuid
          , 'NONE' 									as authorization_method
          , 1500000 								as created_by_wallet_id
          , 1 										as latest
          , purchase_order.uuid 					as reference_order_uuid
          , uuid() 									as secondary_id
          , purchase_order.created_at 				as created_at
          , purchase_order.updated_at 				as updated_at
          , purchase_order.payment_methods 			as payment_methods
      from db_wallet_v2.tb_order purchase_order
      --
      join db_wallet_v2.tb_transaction tr
        on purchase_order.id = tr.order_id
      --
     where purchase_order.type = 'PURCHASE'
       and purchase_order.status = 'CAPTURED'
       and exists  ( select wtm.wallet_id from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = tr.wallet_id )
     order by purchase_order.uuid
    ;


    -- TODO: Inserir as transações de liquidação referente ao numero de parcelas que foi pago no caso de cartão e saldo.
    -- TODO: saldo sempre é 1x (a vista)

    commit;
END$$

DELIMITER ;