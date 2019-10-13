select ord.id 					as order_id
	 , case
		when ord.type = 'RELEASE' then ref_order.type
		else ord.type
       end                      as order_type
	 , ord.uuid 				as order_uuid
     , ord.status 				as order_status
     , ord.total_amount_in_cents as order_total_amount_in_cents
     , ord.title 				as order_title
     , ord.description 			as order_description
     , ord.order_detail_uuid	as order_detail_uuid
     , ord.authorization_method as order_authorization_method
     , ord.created_by_wallet_id as order_created_by_wallet_id
     , ord.latest 				as order_latest
     , ord.created_at			as order_created_at
     , ord.updated_at 			as order_updated_at
     , ord.reference_order_uuid as order_reference_order_uuid
     , ord.payment_methods      as order_payment_methods
     , ord.secondary_id         as order_secondary_id
     , ord.nsu                  as order_nsu
     , case when act.id is null then 1 else act.id end as action_id
     , case when act.type is null then 'MIGRATION' else act.type end					as action_type
     , act.parent_action_id     as action_parent_action_id
     , act.created_at			as action_created_at
     , ow.id                 	as owner_id
	 , ow.uuid               	as owner_uuid
     , ow.name               	as owner_name
     , ow.email              	as owner_email
     , ow.document           	as owner_document
     , ow.document_type      	as owner_document_type
     , ow.active             	as owner_active
     , ow.external_id        	as owner_external_id
     , ow.created_at         	as owner_created_at
     , ow.updated_at         	as owner_updated_at
     , case
			when ord.type = 'RELEASE' then  ref_order.reference_order_uuid
            else ord.reference_order_uuid
		end as reference_order_uuid
	 , wal.id 					as wallet_id
     , wal.uuid 				as wallet_uuid
     , wal.main 				as wallet_main
     , wal.name 				as wallet_name
     , wal.type 				as wallet_type
     , wal.created_at 			as wallet_created_at
     , wal.updated_at 			as wallet_updated_at
  from tb_order ord
  --
  join tb_wallet wal
    on ord.created_by_wallet_id = wal.id
  --
  join tb_owner ow
    on ow.id = wal.owner_id
  --
  left join tb_action act
	on act.order_id = ord.id and act.order_id is not null
  --
   left join tb_order ref_order
          on ref_order.uuid = ord.reference_order_uuid and ref_order.latest is true
  --
 where ord.uuid = :order_uuid
   and ord.latest is true