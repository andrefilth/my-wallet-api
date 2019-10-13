SELECT
    t.uuid AS transaction_id,
    ord.uuid AS order_id,
    t.amount_in_cents,
    t.created_at
FROM
    tb_transaction t
        JOIN
    tb_order ord ON t.order_id = ord.id
        JOIN
    tb_transaction trn ON ord.id = trn.order_id
        AND trn.latest IS TRUE
        JOIN
    tb_wallet wallet ON wallet.id = trn.peer_wallet_id
        JOIN
    tb_owner owner ON owner.id = wallet.owner_id
        LEFT JOIN
    tb_order ref_order ON ref_order.uuid = ord.reference_order_uuid
        AND ref_order.latest IS TRUE
WHERE
    trn.latest = TRUE
        AND ord.status = :order_status
        AND ref_order.type = :order_type
        AND wallet.uuid = :wallet_id
        AND ord.created_at BETWEEN :date_start AND :date_end
ORDER BY ord.created_at DESC;