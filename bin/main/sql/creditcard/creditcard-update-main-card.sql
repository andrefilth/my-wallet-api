UPDATE tb_creditcard cc join tb_wallet w on cc.wallet_id = w.id
SET
    cc.main = FALSE
WHERE
    cc.id <> :cardId AND w.id = :walletId