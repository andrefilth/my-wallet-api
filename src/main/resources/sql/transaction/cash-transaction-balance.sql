select
	ifnull(sum(
		CASE
			WHEN trc.transaction_type = 'DEBIT'  AND (trc.status in ('CAPTURED', 'AUTHORIZED', 'PENDING')) THEN trc.amount_in_cents * -1
			WHEN trc.transaction_type = 'CREDIT' AND trc.status in ('CAPTURED') THEN trc.amount_in_cents
            ELSE 0
		END
    ),0) available_amount,

    ifnull(sum( CASE WHEN trc.transaction_type = 'CREDIT' AND trc.status in ('AUTHORIZED', 'PENDING') THEN trc.amount_in_cents ELSE 0 END ),0)  future_credit,
    ifnull(sum( CASE WHEN trc.transaction_type = 'DEBIT'  AND trc.status in ('AUTHORIZED', 'PENDING') THEN trc.amount_in_cents * -1 END ),0)   future_debit

from tb_cash_transaction trc
join tb_transaction tr on tr.id = trc.transaction_id
where trc.wallet_id = :wallet_id
  and trc.wallet_id not in (1, 71416, 71432, 445209, 445214, 920519)
  and tr.latest is true
;
