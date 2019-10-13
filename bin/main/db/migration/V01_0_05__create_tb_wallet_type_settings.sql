CREATE TABLE IF NOT EXISTS tb_wallet_type_settings (
	wallet_type VARCHAR(255) NOT NULL,
	setting_name VARCHAR(255) NOT NULL,
	value VARCHAR(255) NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (wallet_type, setting_name)
);

ALTER TABLE tb_wallet_type_settings ADD FOREIGN KEY fk_wallet_type_setting (setting_name) REFERENCES tb_settings (name);

