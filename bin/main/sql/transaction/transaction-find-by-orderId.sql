select tr.id                                as transaction_id
     , tr.uuid                              as transaction_uuid
     , tr.peer_wallet_id                    as peer_wallet_id
     , tr.manager_wallet_id                 as manager_wallet_id
     , tr.order_id                          as transaction_order_id
     , tr.order_uuid                        as transaction_order_uuid
     , tr.action_id                         as transaction_action_id
     , tr.wallet_id                         as transaction_wallet_id
     , wal.uuid                             as transaction_wallet_uuid
     , peer_wal.uuid                        as transaction_peer_wallet_uuid
     , tr.status                            as transaction_status
     , tr.type                              as transaction_type
     , tr.payment_method                    as transaction_payment_method
     , tr.amount_in_cents                   as transaction_amount_in_cents
     , tr.take_rate                         as transaction_take_rate
     , tr.release_date                      as transaction_release_date
     , tr.latest                            as transaction_latest
     , tr.created_at                        as transaction_created_at
     , tr.updated_at                        as transaction_updated_at
     , tr.peer_transaction_uuid             as peer_transaction_uuid
     , cc.id                                as creditcard_id
     , cc.credit_card_id                    as credit_card_id
     , cc.credit_card_status                as creditcard_status
     , cc.gateway_order_reference           as cc_gateway_order_reference
     , cc.gateway_payment_reference         as cc_gateway_payment_reference
     , cc.acquirer                          as cc_acquirer
     , cc.tid                               as cc_tid
     , cc.nsu                               as cc_nsu
     , cc.authorization_nsu                 as cc_authorization_nsu
     , cc.capture_nsu                       as cc_capture_nsu
     , cc.cancel_nsu                        as cc_cancel_nsu
     , cc.number_of_installments            as cc_number_of_installments
     , cc.installment_type                  as cc_installment_type
     , cc.authorization_code                as cc_authorization_code
     , cc.authorization_tid                 as cc_authorization_tid
     , cc.capture_tid                       as cc_capture_tid
     , cc.cancel_tid                        as cc_cancel_tid
     , cc.holder_name                       as cc_holder_name
     , cc.masked_number                     as cc_masked_number
     , cc.brand                             as cc_brand
     , cc.expiration_month                  as cc_expiration_month
     , cc.expiration_year                   as cc_expiration_year
     , cc.currency                          as cc_currency
     , cc.authorization_date                as cc_authorization_date
     , cc.capture_date                      as cc_capture_date
     , cc.cancel_date                       as cc_cancel_date
     , cc.refund_date                       as cc_refund_date
     , cc.gateway_cancellation_reference    as cc_gateway_cancellation_reference
     , cc.gateway_response_message          as cc_gateway_response_message
     , cc.gateway_response_code             as cc_gateway_response_code
     , cc.created_at                        as cc_created_at
     , cc.updated_at                        as cc_updated_at
     , cash.status							as cash_status
     , cash.id								as cash_id
     , cash.created_at						as cash_created_at
     , cash.updated_at						as cash_updated_at
     , bank_transfer.id						as banktransfer_id
     , cashback.status						as cash_back_status
     , cashback.id							as cash_back_id
     , cashback.created_at					as cash_back_created_at
     , cashback.updated_at					as cash_back_updated_at
     , bank_transfer.client_name			as banktransfer_client_name
     , bank_transfer.client_email			as banktransfer_client_email
     , bank_transfer.client_phone			as banktransfer_client_phone
     , bank_transfer.client_cpf				as banktransfer_client_cpf
     , bank_transfer.bank					as banktransfer_bank
     , bank_transfer.agency					as banktransfer_agency
     , bank_transfer.account_number			as banktransfer_account_number
     , bank_transfer.bank_transfer_status	as banktransfer_status
     , bank_transfer.tax_applied			as banktransfer_tax_applied
     , bank_transfer.bank_transfer_type		as banktransfer_type
     , bank_transfer.destination_agency		as banktransfer_destination_agency
     , bank_transfer.destination_account	as banktransfer_destination_account
     , bank_transfer.destination_account_holder	as banktransfer_destination_account_holder
     , bank_transfer.destination_account_holder_document as banktransfer_destination_account_holder_document
     , tr.take_rate_unit                    as transaction_take_rate_unit
     , tr.gross_amount_in_cents				as transaction_gross_amount_in_cents
     , tr.net_amount_in_cents				as transaction_net_amount_in_cents
     , tr.take_rate_amount_in_cents			as transaction_take_rate_amount_in_cents
     , tr.release_time						as transaction_release_time
     , tr.release_time_unit					as transaction_release_time_unit
  from tb_transaction tr
  join tb_wallet wal
    on wal.id = tr.wallet_id
left join tb_wallet peer_wal
       on peer_wal.id = tr.peer_wallet_id
left join tb_creditcard_transaction cc
	   on cc.transaction_id = tr.id
left join tb_cash_transaction cash
	   on cash.transaction_id = tr.id
left join tb_cash_back_transaction cashback
     on cashback.transaction_id = tr.id
left join tb_banktransfer_transaction bank_transfer
	   on bank_transfer.transaction_id = tr.id
 where tr.order_id = :order_id
   and tr.latest is true