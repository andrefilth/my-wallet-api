select ord.uuid                                             as order_uuid
     , ord.type                                             as order_type
     , ord.status                                             as order_status
     , ord.title                                             as order_title
     , ord.description                                         as order_description
     , ord.total_amount_in_cents                             as order_total_amount_in_cents
     , ord.created_at                                         as order_created_at
     , ord.updated_at                                         as order_updated_at
     , ord.nsu                                              as order_nsu
     , trn.type                                             as transaction_type
     , trn.wallet_id                                         as wallet_id
     , trn.peer_wallet_id                                    as peer_wallet_id
     , ord.payment_methods                                     as payment_methods
     , ord.reference_order_uuid                             as reference_order_uuid
     , wallet.type                                             as peer_wallet_type
     , owner.name                                            as peer_owner_name
     , ord.secondary_id                                     as order_secondary_id
     , ctrn.gateway_cancellation_reference                     as gtw_cancellation_reference
      --
      from tb_order ord
      --
      join tb_transaction trn
        on ord.id = trn.order_id
        and trn.latest is true
      --
      join tb_wallet wallet
        on wallet.id = trn.peer_wallet_id
      --
      join tb_owner owner
        on owner.id = wallet.owner_id
      --
 left join tb_creditcard_transaction ctrn
        on ctrn.transaction_id = trn.id
     where ord.latest is true
       and ctrn.gateway_cancellation_reference = :gateway_cancellation_reference
  group by ord.uuid
     , ord.type
     , ord.status
     , ord.title
     , ord.description
     , ord.total_amount_in_cents
     , ord.created_at
     , ord.updated_at
     , ord.nsu
     , trn.type
     , trn.wallet_id
     , trn.peer_wallet_id
     , ord.payment_methods
     , ord.reference_order_uuid
     , wallet.type
     , owner.name
     , ord.secondary_id
     , ctrn.gateway_cancellation_reference
  order by ord.created_at desc
