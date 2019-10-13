select
       ord.uuid 				as order_uuid
     , ord.type 				as order_type
     , ord.status 				as order_status
     , ord.reference_order_uuid as order_reference_order_uuid
     , ord.secondary_id			as order_secondary_id
     , trn.uuid 				as transaction_uuid
     , trn.release_date 		as transaction_release_date
  from tb_order ord
   left join tb_action act
	on act.order_id = ord.id and act.order_id is not null
  --
   join tb_transaction trn
    on ord.id = trn.order_id and trn.latest is true
							 and trn.release_date is not null
							 and trn.release_date >=  :begin_date and  trn.release_date < :end_date
--
	 where ord.latest is true--
       and ord.status in (<order_status>)
       and ord.type in (<orderTypes>)
