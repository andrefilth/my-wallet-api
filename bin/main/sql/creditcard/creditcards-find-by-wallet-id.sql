select cc.id as id,
       cc.uuid as uuid,
       cc.token as token,
       cc.hash as hash,
       cc.holder as holder,
       cc.masked_number as masked_number,
       cc.brand as brand,
       cc.exp_date as exp_date,
       cc.main as main,
       cc.wallet_id as wallet_id,
       cc.created_at as created_at,
       cc.updated_at as updated_at,
       cc.active as active,
       cc.verified_by_ame as verified_by_ame
 from tb_creditcard cc
 inner join tb_wallet w on w.id = cc.wallet_id
where cc.active and w.id = :wallet_id
order by main desc;