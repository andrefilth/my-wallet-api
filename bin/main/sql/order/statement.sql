select ord.uuid 										    as order_uuid
     , case
            when ord.type = 'RELEASE' then ref_order.type
            else ord.type
       end 													as order_type
     , ord.created_at 										as order_created_at
     , ord.status 											as order_status
     , ord.title 											as order_title
     , ord.description 										as order_description
     , ord.total_amount_in_cents 							as order_total_amount_in_cents
     , ord.created_at 										as order_created_at
     , ord.updated_at 										as order_updated_at
     , ord.nsu                                              as order_nsu
     , trn.type 											as transaction_type
     , trn.wallet_id 										as wallet_id
     , trn.peer_wallet_id									as peer_wallet_id
     , ord.payment_methods 									as payment_methods
     , case
			when ord.type = 'RELEASE' then  ref_order.reference_order_uuid
            else ord.reference_order_uuid
		end as reference_order_uuid
     , wallet.type 											as peer_wallet_type
     , owner.name											as peer_owner_name
     , trn.take_rate_unit                    				as transaction_take_rate_unit
     , trn.gross_amount_in_cents							as transaction_gross_amount_in_cents
     , case
     		when (trn.net_amount_in_cents > 0 and trn.wallet_id =  :wallet_id) then sum(trn.net_amount_in_cents)	
     		else ord.total_amount_in_cents 
     	end													as transaction_net_amount_in_cents
     , trn.take_rate_amount_in_cents						as transaction_take_rate_amount_in_cents
     , trn.release_time										as transaction_release_time
     , trn.release_time_unit								as transaction_release_time_unit
	  from tb_order ord
      --
	  join tb_transaction trn
        on ord.id = trn.order_id and trn.latest is true
      --
	  join tb_wallet wallet
        on wallet.id = trn.peer_wallet_id
      --
	  join tb_owner owner
        on owner.id = wallet.owner_id
      --
 left join tb_order ref_order
        on ref_order.uuid = ord.reference_order_uuid and ref_order.latest is true
       --
	 where ord.latest is true
	   and trn.wallet_id =  :wallet_id
	   and trn.type in (<transactionType>)
	   and ord.status != 'DENIED'
	   and ord.status in (<orderStatus>)
	   and trn.wallet_id not in (1, 71416, 71432, 445209, 445214, 920519)
  group by ord.uuid
  order by ord.created_at desc
    limit :size
	offset :offset