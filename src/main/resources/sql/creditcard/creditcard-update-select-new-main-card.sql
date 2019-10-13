UPDATE tb_creditcard
SET
    main = TRUE
WHERE
    id IN (SELECT
            id
        FROM
            (SELECT
                cc.id
            FROM
                tb_creditcard cc
            JOIN tb_wallet w on cc.wallet_id = w.id
            WHERE
                w.id = :walletId
                    AND cc.active
                    AND NOT cc.main
            ORDER BY cc.id
            LIMIT 1) tmp)
        AND NOT EXISTS( SELECT
            id
        FROM
            (SELECT
                cc.id
            FROM
                tb_creditcard cc
            JOIN tb_wallet w on cc.wallet_id = w.id
            WHERE
                w.id = :walletId
                    AND cc.active
                    AND cc.main) tmp2);