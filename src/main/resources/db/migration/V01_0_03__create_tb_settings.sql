CREATE TABLE IF NOT EXISTS tb_settings (
    name VARCHAR(255) PRIMARY KEY,
    default_value VARCHAR(255)
);


INSERT into tb_settings (name, default_value) VALUES ('wallet.balance.maxAvailableAmount', '9999999999999');
INSERT into tb_settings (name, default_value) VALUES ('merchant.purchase.callbackUrl', null);

commit;
