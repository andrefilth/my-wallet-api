update tb_creditcard
set token = :token,
    hash = :hash,
    holder = :holder,
    masked_number = :maskedNumber,
    brand = :brand,
    exp_date = :expDate,
    main = :main,
    active = :active,
    wallet_id = :walletId,
    verified_by_ame = :verifiedByAme
where uuid = :uuid;