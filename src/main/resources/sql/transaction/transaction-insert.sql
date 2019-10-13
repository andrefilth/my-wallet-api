insert into tb_transaction (
          uuid
        , peer_wallet_id
        , manager_wallet_id
        , order_id
        , order_uuid
        , peer_transaction_uuid
        , action_id
        , wallet_id
        , status
        , type
        , payment_method

        , amount_in_cents

        , take_rate
        , take_rate_unit
        , take_rate_amount_in_cents

        , gross_amount_in_cents
        , net_amount_in_cents

        , release_time
        , release_time_unit
        , release_date

        , latest
        , created_at
        , updated_at
) values (
          :uuid
        , :peer_wallet_id
        , :manager_wallet_id
        , :order_id
        , :order_uuid
        , :peer_transaction_uuid
        , :action_id
        , :wallet_id
        , :status
        , :type
        , :payment_method

        , :amount_in_cents

        , :take_rate
        , :take_rate_unit
        , :take_rate_amount_in_cents

        , :gross_amount_in_cents
        , :net_amount_in_cents

        , :release_time
        , :release_time_unit
        , :release_date

        , :latest
        , :created_at
        , :updated_at
)