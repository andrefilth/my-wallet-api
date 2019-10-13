insert into tb_creditcard (
    uuid,
    token,
    hash,
    holder,
    masked_number,
    brand,
    exp_date,
    main,
    wallet_id,
    verified_by_ame,
    active) values (
	:uuid,
    :token,
    :hash,
    :holder,
    :maskedNumber,
    :brand,
    :expDate,
    :main,
    :walletId,
    :verifiedByAme,
    true
 );