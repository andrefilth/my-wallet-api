use db_wallet_v2;

DROP PROCEDURE IF EXISTS db_wallet_v2.pr_load_tb_order_refund;

DELIMITER $$

CREATE  PROCEDURE db_wallet_v2.pr_load_tb_order_refund (IN p_wallet_id INT, IN p_wallet_uuid VARCHAR(255))
BEGIN

-- ####################################################################################################
-- Popula a tb_order
-- ####################################################################################################

/*
    Insere as ordens de estorno para as acarteiras alvo,
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
select 	 wt.operation_code 										as w2_uuid
		, 'REFUND' 												as w2_type
		, case
				when wt.status = 'REFUNDED' then 'REFUNDED'
				when wt.status = 'HOLD'	  then 'AUTHORIZED'
				when wt.status = 'DENIED' then 'DENIED'
		  end 													as w2_status
		, wt.amount 							    			as w2_total_amount_in_cents
		, wt.title 												as w2_title
		, wt.description 										as w2_description
		, null 													as w2_order_detail_uuid
		, 'NONE' 												as w2_authorization_method
		, wt.wallet_peer 				 						as w2_created_by_wallet_id
		, 1 													as w2_latest
		, wt.operation_reference 								as w2_reference_order_uuid
		, ifnull(wt.refund_id, uuid()) 							as w2_secondary_id
		, wt.operation_date     								as w2_created_at
		, ifnull(wt.updated_at, wt.operation_date)       		as w2_updated_at
		, case
                when wt.cash_type = 'CASH' then 'CASH'
                when wt.cash_type = 'CARD' then 'CREDIT_CARD'
                else 'CASH,CREDIT_CARD'
		  end 													as w2_payment_methods
	 from db_wallet.TB_WALLET_TRANSACTION wt
	where wt.type = 'REFUND'
      and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = wt.wallet_id)
;



-- ####################################################################################################
-- Popula a tabela tb_transaction
-- ####################################################################################################

	-- Insere as transacoes de credito para o cliente em seguida o debito para o lojista.
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
	select ws.code 								as uuid
		, refund_order.id 						as order_id
		, refund_order.uuid 					as order_uuid
		, 1 									as action_id
		, wt.wallet_id 							as wallet_id
		, case
				when ws.status = 'REFUNDED' 	then 'CAPTURED'
				when ws.status = 'HOLD' 		then 'PENDING'
				when ws.status = 'DENIED' 		then 'DENIED'
                when ws.status = 'ERROR' 		then 'ERROR'
		   end                                                          as status
		, 'CREDIT' 														as type
		, case
				when ws.cash_type = 'CASH' then 'CASH'
				when ws.cash_type = 'CARD' then 'CREDIT_CARD'
		   end 															as payment_method
		, wt.wallet_peer                     							as peer_wallet_id
		, null 															as peer_transaction_uuid
		, 1500000 														as manager_wallet_id
		, ws.amount 													as amount_in_cents
		, 1 															as latest
		, refund_order.created_at 										as created_at
		, refund_order.updated_at 										as updated_at
	from tb_order refund_order
	--
	join db_wallet.TB_WALLET_TRANSACTION wt
	  on     refund_order.reference_order_uuid = wt.operation_reference
	     and refund_order.uuid = wt.operation_code
	--
	join db_wallet.TB_WALLET_TRANSACTION_SPLIT ws
	  on ws.transaction_id = wt.id
	--
	where refund_order.type = 'REFUND'
	  and refund_order.latest is true
	  and wt.type = 'REFUND'
	  -- and wt.wallet_id in (p_wallet_id)
      and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = wt.wallet_id)
	;

	commit;

	-- insere cash transaction do cliente
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
         , tr.amount_in_cents 	as amount_in_cents
         , tr.status 			as status
         , tr.type			 	as transaction_type
         , tr.created_at 		as created_at
         , tr.updated_at 		as updated_at
      from db_wallet_v2.tb_order ord
      join db_wallet_v2.tb_transaction tr
        on tr.order_id = ord.id and tr.latest is true
     where ord.type = 'REFUND'
       and ord.latest is true
       and tr.payment_method = 'CASH'
       and tr.type = 'CREDIT'
       -- and tr.wallet_id in (p_wallet_id)
       and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = tr.wallet_id)
    ;

commit;

    #insere credit_card_transaction
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
    )
    select tr.id 															    as transaction_id
     , ifnull(ca.card_token, 'invalid') 													    as credit_card_id
     , case
			when ws.status = 'DENIED'   	then 'UNAUTHORIZED'
			when ws.status = 'REFUNDED'  	then 'REFUNDED'
			when ws.status = 'HOLD' 	 	then 'CANCELLATION_PENDING'
			when ws.status = 'ERROR' 		then 'UNEXPECTED_ERROR'
		end 																as credit_card_status
      , ord.uuid 															as gateway_order_reference
	  , ca.payment_id 													as gateway_payment_reference
	  , null 											                    as acquirer
	  , ca.tid 										                    as tid
	   , ca.nsu 										                    as nsu
	   , ca.nsu 										                    as authorization_nsu
	   , ca.nsu											                as capture_nsu
	   , ca.nsu											                as cancel_nsu
	   , ca.installments 								                    as number_of_installments
	   , null 											                    as installment_type
	   , ca.authorization_code 							                as authorization_code
	   , ca.tid 										                    as authorization_tid
	   , ca.tid 										                    as capture_tid
	   , ca.tid 										                    as cancel_tid
	   , null 											                    as holder_name
	   , ca.card_masked 									                as masked_number
	   , ca.card_brand 									                as brand
	   , null 											                    as expiration_month
	   , null 											                    as expiration_year
	   , 'BRL' 											                    as currency
	   , ws.date 										                    as authorization_date
	   , ws.date 										                    as capture_date
	   , ws.date 										                    as cancel_date
	   , ws.date 										                    as refund_date
	   , ca.cancel_id 									                as gateway_cancellation_reference
	   , ca.response_message 							        as gateway_response_message
	   , ca.response_code 								                as gateway_response_code
	   , ws.date 										                    as created_at
	   , ws.date 										                    as updated_at
      from db_wallet_v2.tb_order ord
      --
      join db_wallet_v2.tb_transaction tr
        on tr.order_id = ord.id and tr.latest is true

      join db_wallet.TB_WALLET_TRANSACTION wt
        on wt.operation_code = ord.uuid
       and wt.wallet_id = tr.wallet_id
       and wt.type = 'REFUND'
       and wt.operation_type = 'CREDIT'
      --
      join db_wallet.TB_WALLET_TRANSACTION_SPLIT ws
        on ws.transaction_id = wt.id
        and ws.cash_type = 'CARD'
      --
      join db_wallet.TB_CARD_AUTHORIZATION ca
        on     ca.id = ws.card_authorization_id
           and ws.cash_type = 'CARD'

     where ord.type = 'REFUND'
       and ord.latest is true
       and tr.payment_method = 'CREDIT_CARD'
       -- and tr.wallet_id in (p_wallet_id)
       and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = tr.wallet_id)
;
commit;

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
	select uuid() 								as uuid
		, refund_order.id 						as order_id
		, refund_order.uuid 					as order_uuid
		, 1 									as action_id
		, wt.wallet_peer 							as wallet_id
		, case
				when ws.status = 'REFUNDED' 	then 'CAPTURED'
				when ws.status = 'HOLD' 		then 'PENDING'
				when ws.status = 'DENIED' 		then 'DENIED'
                when ws.status = 'ERROR' 		then 'ERROR'
		   end                                                          as status
        , 'DEBIT' 														as type
		, 'CASH'														as payment_method
		, wt.wallet_id                       							as peer_wallet_id
		, null 															as peer_transaction_uuid
		, 1500000 														as manager_wallet_id
		, ws.amount 													as amount_in_cents
		, 1 															as latest
		, refund_order.created_at 										as created_at
		, refund_order.updated_at 										as updated_at
	from tb_order refund_order
	--
	join db_wallet.TB_WALLET_TRANSACTION wt
	  on refund_order.uuid = wt.operation_code
	--
	join db_wallet.TB_WALLET_TRANSACTION_SPLIT ws
	  on ws.transaction_id = wt.id
	--
	where refund_order.type = 'REFUND'
	  and refund_order.latest is true
	  and wt.type = 'REFUND'
	  -- and wt.wallet_id in (p_wallet_id)
      and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = wt.wallet_id)
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
    select tr.wallet_id      	as wallet_id
         , tr.id 				as transaction_id
         , tr.amount_in_cents 	as amount_in_cents
         , tr.status 			as status
         , tr.type			 	as transaction_type
         , tr.created_at 		as created_at
         , tr.updated_at 		as updated_at
      from db_wallet_v2.tb_order ord
      join db_wallet_v2.tb_transaction tr
        on tr.order_id = ord.id and tr.latest is true
     where ord.type = 'REFUND'
       and ord.latest is true
       and tr.type = 'DEBIT'
       and tr.payment_method = 'CASH'
       -- and tr.peer_wallet_id in (p_wallet_id)
       and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = tr.peer_wallet_id)
    ;

    commit;

    -- Importa os estornos que para os pagamentos que não foram registrados estornos porém possuem valores no amount_refunded
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
    select uuid() 													as w2_uuid
	 , 'REFUND' 												as w2_type
	 , 'REFUNDED' 												as w2_status
	 , wt.amount_refunded 										as w2_total_amount_in_cents
	 , wt.title 												as w2_title
	 , wt.description 											as w2_description
	 , null 													as w2_order_detail_uuid
	 , 'NONE' 													as w2_authorization_method
	 , wt.wallet_peer	 				 							as w2_created_by_wallet_id
	 , 1 														as w2_latest
	 , wt.operation_code 										as w2_reference_order_uuid
	 , uuid()                       							as w2_secondary_id
	 , ifnull(wt.updated_at, wt.operation_date) 				as w2_created_at
	 , ifnull(wt.updated_at, wt.operation_date) 				as w2_updated_at
	 , case
			when wt.cash_type = 'CASH' then 'CASH'
			when wt.cash_type = 'CARD' then 'CREDIT_CARD'
			else 'CASH,CREDIT_CARD'
	   end 														as w2_payment_methods
  from db_wallet.TB_WALLET_TRANSACTION wt
  where wt.type = 'PAYMENT'
	and wt.amount_refunded != 0
	-- and wt.wallet_id in (p_wallet_id)
	and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = wt.wallet_id)
	and not exists (
		select 1
		  from db_wallet.TB_WALLET_TRANSACTION tr_r
		 where tr_r.operation_reference = wt.operation_code
		   and tr_r.type = 'REFUND'
           and tr_r.wallet_id = wt.wallet_id
	)
   ;

    commit;


    -- Insere a transacao de credito na carteira do cliente
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
    select uuid() as uuid
         , ord.id as order_id
         , ord.uuid as order_uuid
         , 1 as action_id
         , wt.wallet_id as wallet_id
         , 'CAPTURED' as status
         , 'CREDIT' as type
         , ord.payment_methods as payment_method
         , wt.wallet_peer as peer_wallet_id
         , null as peer_transaction_uuid
         , 1500000 as manager_wallet_id
         , ord.total_amount_in_cents as amount_in_cents
         , 1 as latest
         , ord.created_at as created_at
         , ord.updated_at as updated_at
      from db_wallet_v2.tb_order ord
      join db_wallet.TB_WALLET_TRANSACTION wt
        on 		wt.operation_code = ord.reference_order_uuid
           and	wt.type = 'PAYMENT'
           and  wt.amount_refunded != 0
           -- and 	wt.wallet_id in (p_wallet_id)
           and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = wt.wallet_id)
     where ord.type = 'REFUND'
       and not exists (
         select 1
           from db_wallet_v2.tb_transaction itr
          where itr.order_id = ord.id
            -- and itr.wallet_id in (p_wallet_id)
            and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = itr.wallet_id)
      )
    ;

    commit;

    -- Insere a transacao de debi na carteira do lojista
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
    select uuid() 						as uuid
         , ord.id 						as order_id
         , ord.uuid 					as order_uuid
         , 1 							as action_id
         , wt.wallet_peer 				as wallet_id
         , 'CAPTURED' 					as status
         , 'DEBIT' 						as type
         , 'CASH' 						as payment_method
         , wt.wallet_id 				as peer_wallet_id
         , null 						as peer_transaction_uuid
         , 1500000 						as manager_wallet_id
         , ord.total_amount_in_cents 	as amount_in_cents
         , 1 							as latest
         , ord.created_at 				as created_at
         , ord.updated_at 				as updated_at
      from db_wallet_v2.tb_order ord
      join db_wallet.TB_WALLET_TRANSACTION wt
        on 		wt.operation_code = ord.reference_order_uuid
           and	wt.type = 'PAYMENT'
           and  wt.amount_refunded != 0
           -- and 	wt.wallet_id in (p_wallet_id)
           and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = wt.wallet_id)
     where ord.type = 'REFUND'
       and not exists (
         select 1
           from db_wallet_v2.tb_transaction itr
          where itr.order_id = ord.id
			and itr.type = 'DEBIT'
            -- and itr.peer_wallet_id in (p_wallet_id)
            and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = itr.peer_wallet_id)

      )
    ;

    commit;

    -- Insere o debito no saldo do lojista
    insert into db_wallet_v2.tb_cash_transaction (
              wallet_id
            , transaction_id
            , amount_in_cents
            , status
            , transaction_type
            , created_at
            , updated_at
         )
    select tr.wallet_id         as wallet_id
         , tr.id                as transaction_id
         , tr.amount_in_cents   as amount_in_cents
         , tr.status            as status
         , tr.type              as transaction_type
         , tr.created_at        as created_at
         , tr.updated_at        as updated_at
    from db_wallet_v2.tb_transaction tr
     where tr.payment_method = 'CASH'
        and tr.type = 'DEBIT'
        -- and tr.peer_wallet_id in (p_wallet_id)
        and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = tr.peer_wallet_id)
        and tr.latest is true
        and not exists (
         select 1
           from db_wallet_v2.tb_cash_transaction icstr
          where tr.id = icstr.transaction_id
            and icstr.wallet_id = tr.wallet_id
      )
    ;

    commit;


    -- Insere o credito no saldo do cliente se houver
    insert into db_wallet_v2.tb_cash_transaction (
              wallet_id
            , transaction_id
            , amount_in_cents
            , status
            , transaction_type
            , created_at
            , updated_at
    )
    select tr.wallet_id         as wallet_id
         , tr.id                as transaction_id
         , tr.amount_in_cents   as amount_in_cents
         , tr.status            as status
         , tr.type              as transaction_type
         , tr.created_at        as created_at
         , tr.updated_at        as updated_at
    from db_wallet_v2.tb_transaction tr
     where tr.payment_method = 'CASH'
        and tr.type = 'CREDIT'
        -- and tr.wallet_id in (p_wallet_id)
        and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = tr.wallet_id)
        and tr.latest is true
        and not exists (
         select 1
           from db_wallet_v2.tb_cash_transaction icstr
          where tr.id = icstr.transaction_id
            and icstr.wallet_id = tr.wallet_id
      )
    ;


    commit;



END$$

DELIMITER ;