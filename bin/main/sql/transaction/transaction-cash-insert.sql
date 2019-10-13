insert into tb_cash_transaction (
	  wallet_id
    , transaction_id
    , amount_in_cents
    , status
    , transaction_type
    , created_at
    , updated_at
) values (
	  :wallet_id
    , :transaction_id
    , :amount_in_cents
    , :status
    , :transaction_type
    , :created_at
    , :updated_at
);