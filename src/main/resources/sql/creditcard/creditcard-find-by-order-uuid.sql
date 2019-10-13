select wal.uuid 			as wallet_uuid
     , ctrn.credit_card_id 	as credit_card_id
    , ctrn.number_of_installments as number_of_installments
  --
  from tb_order ord
  --
  join tb_transaction trn
    on ord.id = trn.order_id
  --
  join tb_creditcard_transaction ctrn
    on trn.id = ctrn.transaction_id
  --
  join tb_wallet wal
    on wal.id = trn.wallet_id
  --
 where ord.uuid = :order_uuid
   and trn.payment_method = 'CREDIT_CARD'
   and ord.latest is true
   and trn.latest is true
;