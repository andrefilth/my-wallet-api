insert into tb_banktransfer_transaction (
	  wallet_id
    , transaction_id
    , amount_in_cents
    , bank_transfer_status
    , transaction_type
    , created_at
    , updated_at
    , client_name
    , client_email
    , client_phone
    , client_cpf
    , bank
    , agency
    , account_number
    , tax_applied
    , bank_transfer_type
    , destination_agency
    , destination_account
    , destination_account_holder
    , destination_account_holder_document
) values (
	  :wallet_id
    , :transaction_id
    , :amount_in_cents
    , :bank_transfer_status
    , :transaction_type
    , :created_at
    , :updated_at
    , :client_name
    , :client_email
    , :client_phone
    , :client_cpf
    , :bank
    , :agency
    , :account_number
    , :tax_applied
    , :bank_transfer_type
    , :destination_agency
    , :destination_account
    , :destination_account_holder
    , :destination_account_holder_document
);