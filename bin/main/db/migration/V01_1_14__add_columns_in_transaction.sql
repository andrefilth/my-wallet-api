ALTER TABLE db_wallet_v2.tb_transaction ADD take_rate_unit VARCHAR(255) AFTER take_rate;

ALTER TABLE db_wallet_v2.tb_transaction ADD take_rate_amount_in_cents BIGINT (30) AFTER take_rate_unit;
ALTER TABLE db_wallet_v2.tb_transaction ADD gross_amount_in_cents BIGINT (30) AFTER take_rate_unit;
ALTER TABLE db_wallet_v2.tb_transaction ADD net_amount_in_cents BIGINT (30) AFTER gross_amount_in_cents;

ALTER TABLE db_wallet_v2.tb_transaction ADD release_time BIGINT (30) AFTER net_amount_in_cents;
ALTER TABLE db_wallet_v2.tb_transaction ADD release_time_unit VARCHAR (255) AFTER release_time;