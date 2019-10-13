use db_wallet_v2;

DROP PROCEDURE IF EXISTS db_wallet_v2.pr_load_tb_order_gift_cash_in;

DELIMITER $$

CREATE  PROCEDURE db_wallet_v2.pr_load_tb_order_gift_cash_in (IN p_wallet_id INT, IN p_wallet_uuid VARCHAR(255))
BEGIN

/*
    Insere as ordens do cash que veio do limbo geralmente em promoções da AME, e insere como uma ordem de GIFT_CASH_IN
    que nada mais é que uma ordem que irá gerar um debito no carteira manager (AME) e crédito na carteira do cliente.
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
select wt.operation_code 										as uuid
	 , 'GIFT_CASH_IN'		 									as type
	 , case
			when wt.status = 'SUCCESS' then 'CAPTURED'
			when wt.status = 'DENIED' then 'DENIED'
			when wt.status = 'HOLD' then 'AUTHORIZED'
	   end 													as status
	 , wt.amount 											as total_amount_in_cents
	 , wt.title 											as title
	 , wt.description 										as description
	 , wt.operation_code									as order_detail_uuid
	 , 'QRCODE' 											as authorization_method
	 , 1500000		 										as created_by_wallet_id
	 , 1 													as latest
	 , null 												as reference_order_uuid
	 , null 												as secondary_id
	 , wt.operation_date 									as created_at
	 , ifnull(updated_at, operation_date) 					as updated_at
	 , 'CASH' 												as payment_methods
  from db_wallet.TB_WALLET_TRANSACTION wt
 where wt.type = 'CASH_IN'
   and wt.cash_type = 'CASH'
   and wt.operation_type = 'CREDIT'
   -- and wt.wallet_id in (p_wallet_id)
   and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = wt.wallet_id)
;

commit;

/*
	Insere o DEBITO na carteira da AME (Manager) referente as ordens de GIFT_CASH_IN que foram geradas
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
select uuid() 		as uuid
	, ord.id 		as order_id
	, ord.uuid 		as order_uuid
	, 1 			as action_id
	, 1500000 		as wallet_id
	, ord.status 	as status
	, 'DEBIT' 		as type
	, 'CASH' 		as payment_method
	, wt.wallet_peer as peer_wallet_id
	, null 			as peer_transaction_uuid
	, 1500000 		as manager_wallet_id
	, ord.total_amount_in_cents as amount_in_cents
	, 1 as latest
	, ord.created_at as created_at
	, ord.updated_at as updated_at
  from db_wallet_v2.tb_order ord
  join db_wallet.TB_WALLET_TRANSACTION wt
    on wt.operation_code = ord.uuid
 where ord.type = 'GIFT_CASH_IN'
   -- and wt.wallet_id in (p_wallet_id)
   and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = wt.wallet_id)
;

commit;

/*
	Insere o CREDITO na carteira do cliente referente as ordens de GIFT_CASH_IN que foram geradas.
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
select uuid() 		as uuid
	, ord.id 		as order_id
	, ord.uuid 		as order_uuid
	, 1 			as action_id
	, wt.wallet_peer 		as wallet_id
	, ord.status 	as status
	, 'CREDIT' 		as type
	, 'CASH' 		as payment_method
	, 1500000 		as peer_wallet_id
	, null 			as peer_transaction_uuid
	, wt.wallet_peer 		as manager_wallet_id
	, ord.total_amount_in_cents as amount_in_cents
	, 1 as latest
	, ord.created_at as created_at
	, ord.updated_at as updated_at
  from db_wallet_v2.tb_order ord
  join db_wallet.TB_WALLET_TRANSACTION wt
    on wt.operation_code = ord.uuid
 where ord.type = 'GIFT_CASH_IN'
   -- and wt.wallet_id in (p_wallet_id)
   and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = wt.wallet_id)
;

commit;

/*
	Atualiza o saldo do cliente referente as ordens de GIFT_CASH_IN
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
select tr.wallet_id as  wallet_id
     , tr.id as transaction_id
     , tr.amount_in_cents as amount_in_cents
     , tr.status as status
     , tr.type as transaction_type
     , tr.created_at as created_at
     , tr.updated_at as updated_at
--
from db_wallet_v2.tb_transaction tr
--
join db_wallet_v2.tb_order ord
  on ord.id = tr.order_id
--
where ord.type = 'GIFT_CASH_IN'
  and ord.latest is true
  and tr.latest is true
  -- and tr.wallet_id in (p_wallet_id)
  and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = tr.wallet_id)
  and tr.peer_wallet_id = 1500000
;

commit;

/*
	Atualiza o saldo da AME referente as ordens de GIFT_CASH_IN
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
select tr.wallet_id 			as wallet_id
     , tr.id 					as transaction_id
     , tr.amount_in_cents 		as amount_in_cents
     , tr.status 				as status
     , tr.type 					as transaction_type
     , tr.created_at 			as created_at
     , tr.updated_at 			as updated_at
--
from db_wallet_v2.tb_transaction tr
--
join db_wallet_v2.tb_order ord
  on ord.id = tr.order_id
--
where ord.type = 'GIFT_CASH_IN'
  and ord.latest is true
  and tr.latest is true
  and tr.wallet_id = 1500000
  -- and tr.peer_wallet_id in (p_wallet_id)
  and exists (select 1 from db_wallet_v2.tb_wallet_to_migrate wtm where wtm.wallet_id = tr.peer_wallet_id)
;


commit;

END$$

DELIMITER ;