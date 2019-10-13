CREATE TABLE IF NOT EXISTS tb_wallet_settings (
	wallet_id BIGINT(30) NOT NULL,
	setting_name VARCHAR(255) NOT NULL,
	value VARCHAR(255) NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (wallet_id, setting_name)
);


ALTER TABLE tb_wallet_settings ADD FOREIGN KEY fk_wallet (wallet_id) REFERENCES tb_wallet (id);
ALTER TABLE tb_wallet_settings ADD FOREIGN KEY fk_setting (setting_name) REFERENCES tb_settings (name);
