select ord.id 					as order_id
	 , ord.uuid 				as order_uuid
     , ord.type 				as order_type
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
     , owner.id                 as owner_id
     , owner.uuid               as owner_uuid
     , owner.name               as owner_name
     , owner.email              as owner_email
     , owner.document           as owner_document
     , owner.document_type      as owner_document_type
     , owner.active             as owner_active
     , owner.external_id        as owner_external_id
     , owner.created_at         as owner_created_at
     , owner.updated_at         as owner_updated_at
  from tb_order ord
  --
  join tb_wallet wal
    on ord.created_by_wallet_id = wal.id
  --
  join tb_owner owner
    on owner.id = wal.owner_id
  --
   left join tb_action act
	on act.order_id = ord.id and act.order_id is not null
  --
 where ord.reference_order_uuid = :order_uuid
   and ord.latest is true