select ord.uuid 					AS order_uuid
     , ord.type 					AS order_type
     , ord.status 					AS order_status
     , ord.title 					AS order_title
     , ord.description 				AS order_description
     , ord.total_amount_in_cents 	AS order_total_amount_in_cents
     , ord.created_at 				AS order_created_at
     , ord.updated_at 				AS order_updated_at
     , ord.nsu 						AS order_nsu
     , ord.payment_methods 			AS payment_methods
     , ord.reference_order_uuid 	AS reference_order_uuid
     , ord.secondary_id 			AS order_secondary_id
  from tb_order ord
 where ord.reference_order_uuid = :reference_order_uuid
   and ord.latest is true
   and ord.type in (<orderTypes>)