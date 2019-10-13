insert into tb_action (
          parent_action_id
        , type
        , order_id
        , order_uuid
        , created_at
) values (
          :parent_action_id
        , :type
        , :order_id
        , :order_uuid
        , :created_at
)