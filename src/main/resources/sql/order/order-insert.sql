insert into tb_order (
        uuid
      , type
      , status
      , total_amount_in_cents
      , title
      , description
      , order_detail_uuid
      , authorization_method
      , created_by_wallet_id
      , reference_order_uuid
      , payment_methods
      , secondary_id
      , nsu
      , created_at
      , updated_at
) values (
        :uuid
      , :type
      , :status
      , :total_amount_in_cents
      , :title
      , :description
      , :order_detail_uuid
      , :authorization_method
      , :created_by_wallet_id
      , :reference_order_uuid
      , :payment_methods
      , :secondary_id
      , :nsu
      , :created_at
      , :updated_at
)