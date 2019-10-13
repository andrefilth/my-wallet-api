use db_wallet_v2;

DROP PROCEDURE IF EXISTS db_wallet_v2.pr_load_tb_order_transfer;

DELIMITER $$
CREATE PROCEDURE db_wallet_v2.pr_load_tb_order_transfer (IN p_wallet_id INT, IN p_wallet_uuid VARCHAR(255))
BEGIN

    /*
        Caso a carteira alvo tenha realizado um transferencia para outra carteira,
        deve-se inserir a ordem de tranferencia que agrupará as transaçoes
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
    select	  wt.operation_code 											as uuid
		, 'TRANSFER_BETWEEN_WALLETS' 									as type
		, case
			when wt.status = 'DENIED' 		then 'DENIED'
			when wt.status = 'SUCCESS' 		then 'CAPTURED'
			when wt.status = 'CANCELED' 	then 'CANCELLED'
			when wt.status = 'AUTHORIZED' 	then 'AUTHORIZED'
		end														as status
		, wt.amount												as total_amount_in_cents
		, wt.title												as title
		, wt.description			 			                as description
		, wt.operation_code										as order_detail_uuid
		, 'QRCODE'												as authorization_method
		, case
			when wt.operation_type = 'DEBIT' then wt.wallet_id
			else wt.wallet_peer
		  end													as created_by_wallet_id
		, 1														as latest
		, null													as reference_order_uuid
		, null													as secondary_id
		, wt.operation_date										as created_at
		, ifnull( wt.updated_at, wt.operation_date)				as updated_at
		, case
			when wt.cash_type = 'CASH'	then 'CASH'
			when wt.cash_type = 'CARD'	then 'CREDIT_CARD'
			else 'CASH,CREDIT_CARD'
		  end     		                                  			   	as payment_methods
		--

    from 	db_wallet.TB_WALLET_TRANSACTION wt -- USE INDEX (idx_wallet_id)
            --
    where 	wt.type = 'TRANSFER'
    and 	(wt.wallet_id in (p_wallet_id) or wt.wallet_peer in (p_wallet_id))
    and     wt.operation_type = 'DEBIT'
    and 	not exists (
               select 1
               from db_wallet_v2.tb_order ord
               where ord.uuid = wt.operation_code
            );

     commit;


    /*
        Para as ordens de tranferencia geradas é inserido as transações, sejam elas, cartão, saldo ou saldo + cartão.
        No caso de saldo + cartão serão duas transacões cada uma em seu respectivo valor
    */
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
         , tranf_ord.id 											as order_id
         , tranf_ord.uuid 											as order_uuid
         , 1 														as action_id
         , wt.wallet_id 											as wallet_id
         , case
                when wt.status = 'DENIED' 		then 'DENIED'
                when wt.status = 'SUCCESS' 		then 'CAPTURED'
                when wt.status = 'CANCELED' 		then 'CANCELLED'
                when wt.status = 'REFUNDED' 		then 'CAPTURED'
                when wt.status = 'AUTHORIZED' 	then 'AUTHORIZED'
            end 													as status
         , wt.operation_type 										as type
         , case
                when ws.cash_type = 'CARD' then 'CREDIT_CARD'
                when ws.cash_type = 'CASH' then 'CASH'
            end														as payment_mehtod
         , wt.wallet_peer 										as peer_wallet_id
         , null 													as peer_transaction_uuid
         , 1500000													as manager_wallet_id
         , ws.amount 												as amount_in_cents
         , 1 														as latest
         , tranf_ord.created_at 									as created_at
         , tranf_ord.updated_at 									as updated_at
      from db_wallet_v2.tb_order tranf_ord
      join db_wallet.TB_WALLET_TRANSACTION wt
        on wt.operation_code = tranf_ord.uuid
	  join db_wallet.TB_WALLET_TRANSACTION_SPLIT ws
        on ws.transaction_id = wt.id
     where tranf_ord.type = 'TRANSFER_BETWEEN_WALLETS'
       and tranf_ord.latest is true
      -- and tranf_ord.status = 'CAPTURED'
       and (wt.wallet_id in (p_wallet_id) or wt.wallet_peer in (p_wallet_id))
       and wt.operation_type = 'DEBIT'
       and not exists (
               select 1
               from db_wallet_v2.tb_transaction tr
               where tr.order_uuid = tranf_ord.uuid
            )
    ;

    commit;

    /*
		Caso a carteira alvo tenha utilizado cartao de credito para realizar a transferencia deve-se armazenar os dados do cartao de credito
        e associa-lo a respectiva transacao
    */
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
	)  select tr.id 		  								    as transaction_id
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
		on wt.operation_code = ord.uuid
	  --
	  join db_wallet.TB_WALLET_TRANSACTION_SPLIT ws
		on ws.transaction_id = wt.id
	  --
	  join db_wallet.TB_CARD_AUTHORIZATION ca
		on ca.id = ws.card_authorization_id
	  --
	 where (tr.wallet_id in (p_wallet_id) or tr.peer_wallet_id in (p_wallet_id))
       and tr.payment_method = 'CREDIT_CARD'
       and tr.type = 'DEBIT'
       and ord.type = 'TRANSFER_BETWEEN_WALLETS'
       and ws.cash_type = 'CARD'
       and not exists (
               select 1
               from db_wallet_v2.tb_creditcard_transaction ctr
               where ctr.transaction_id = tr.id
            )
	;

	commit;



    /*
	O criador da transferencia, caso tenha feito utilizando somente o CASH, deve-se inserir os devidos debito na
	tb_cash_transaction
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
         , tr.amount_in_cents 	as amount_in_cents
         , tr.status 			as status
         , tr.type   			as transaction_type
         , tr.created_at 		as created_at
         , tr.updated_at 		as updated_at
      from db_wallet_v2.tb_transaction tr
      --
      join db_wallet_v2.tb_order transfer_order
        on transfer_order.id = tr.order_id
      --
     where tr.type = 'DEBIT'
       and tr.payment_method = 'CASH'
       and transfer_order.type = 'TRANSFER_BETWEEN_WALLETS'
       and (tr.wallet_id in (p_wallet_id) or tr.peer_wallet_id in (p_wallet_id))
       and not exists (
               select 1
               from db_wallet_v2.tb_cash_transaction ctr
               where ctr.transaction_id = tr.id
            )
     --  and transfer_order.status = 'CAPTURED'
     --  and transfer_order.created_by_wallet_id in (p_wallet_id)
    ;

    commit;

    /*
        Para as ordens de transferencia inseridas é gerado a ordem de liquidacão, para que o crédito seja depositado
        na carteira do peer original da transferencia.
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
    -- Insere as liquidaçoes referentes as transferencias da carteira alvo
    select uuid() 							as uuid
        , 'RELEASE' 						as type
        , 'RELEASED' 						as status
        , sum(tr.amount_in_cents)           as total_amount_in_cents
        , ord.title 						as title
        , ord.description 					as description
        , null 								as order_detail_uuid
        , 'NONE' 							as authorization_method
        , 1500000 							as created_by_wallet_id
        , 1 								as latest
        , ord.uuid 							as reference_order_uuid
        , uuid() 							as secondary_id
        , ord.created_at 					as created_at
        , ord.updated_at 					as updated_at
        , ord.payment_methods 				as payment_methods
      --
      from  db_wallet_v2.tb_order ord
      --
	  join  db_wallet_v2.tb_transaction tr
		on  ord.id = tr.order_id
      --
      where ord.type = 'TRANSFER_BETWEEN_WALLETS'
        and ord.status = 'CAPTURED'
        and (tr.wallet_id in (p_wallet_id) or tr.peer_wallet_id in (p_wallet_id))
        and not exists (
               select 1
               from db_wallet_v2.tb_order ord_x
               where ord_x.reference_order_uuid = ord.uuid
            )
       group    by uuid
                , type
                , status
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
    ;

    commit;

    /*
        Para o recebedor da transferencia é gerado as transacoes de credito baseado nas liquidaçoes que foram geradas
        para as transferencias
    */
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
	select
		   uuid() 									as uuid
		 , release_order.id 							as order_id
		 , release_order.uuid 						as order_uuid
		 , 1 										as action_id
		 , transfer_transaction.peer_wallet_id 		as wallet_id
		 , 'CAPTURED' 								as status
		 , 'CREDIT' 								as type
		 , 'CASH' 									as payment_method
		 , transfer_order.created_by_wallet_id 		as peer_wallet_id
		 , transfer_transaction.uuid 				as peer_transaction_uuid
		 , 1500000 									as manager_wallet_id
		 , transfer_transaction.amount_in_cents		as amount_in_cents
		 , 1 										as latest
		 , release_order.created_at 					as created_at
		 , release_order.updated_at 					as updated_at
	  from db_wallet_v2.tb_order release_order
	  --
	  join db_wallet_v2.tb_order transfer_order
		on transfer_order.uuid = release_order.reference_order_uuid
		and transfer_order.latest is true
	  --
	  join db_wallet_v2.tb_transaction transfer_transaction
		on transfer_transaction.order_id = transfer_order.id
		and transfer_transaction.latest is true
	  --
	 where release_order.type = 'RELEASE'
       and release_order.latest is true
	   and release_order.status = 'RELEASED'
       and (transfer_transaction.wallet_id in (p_wallet_id) or transfer_transaction.peer_wallet_id in (p_wallet_id))
       and transfer_order.type = 'TRANSFER_BETWEEN_WALLETS'
	   and transfer_order.status = 'CAPTURED'
	   and not exists (
               select 1
               from db_wallet_v2.tb_transaction tr
               where tr.peer_transaction_uuid = transfer_transaction.uuid
            )
	;

    commit;


    /*
		A carteira alvo reializou tranferencia para outras carteiras logo deve-se inserir o credito nessas carteiras de destino
        que sao originados apartir da liquidaçao das respctivas liquidacao das ordens de transferencias
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
	select rel_tr.wallet_id 		as wallet_id
		 , rel_tr.id 				as transaction_id
		 , rel_tr.amount_in_cents 	as amount_in_cents
		 , rel_tr.status 			as status
		 , 'CREDIT' 				as transaction_type
		 , rel_tr.created_at 		as created_at
		 , rel_tr.updated_at 		as updated_at
	  from db_wallet_v2.tb_transaction rel_tr
      --
	  join db_wallet_v2.tb_order release_order
		on release_order.id = rel_tr.order_id
		and release_order.latest is true
		and release_order.type = 'RELEASE'
	   and release_order.status = 'RELEASED'
      --
	  join db_wallet_v2.tb_order transfer_order
		on transfer_order.uuid = release_order.reference_order_uuid
		and transfer_order.latest is true
		and transfer_order.type = 'TRANSFER_BETWEEN_WALLETS'
	    and transfer_order.status = 'CAPTURED'
      --
	 where rel_tr.type = 'CREDIT'
	   and rel_tr.payment_method = 'CASH'
       and (rel_tr.wallet_id in (p_wallet_id) or rel_tr.peer_wallet_id in (p_wallet_id))
       and rel_tr.latest is true
       and not exists (
               select 1
               from db_wallet_v2.tb_cash_transaction ctr
               where ctr.transaction_id = rel_tr.id
            )
	;


    commit;

END$$
DELIMITER ; 