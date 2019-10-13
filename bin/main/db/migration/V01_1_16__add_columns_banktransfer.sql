ALTER TABLE tb_banktransfer_transaction ADD COLUMN bank_transfer_type ENUM('BANK_CASH_OUT','BANK_CASH_IN') NOT NULL DEFAULT 'BANK_CASH_OUT';
ALTER TABLE tb_banktransfer_transaction ALTER COLUMN bank_transfer_type DROP DEFAULT;

ALTER TABLE tb_banktransfer_transaction ADD COLUMN destination_agency VARCHAR(255);
ALTER TABLE tb_banktransfer_transaction ADD COLUMN destination_account VARCHAR(255);
ALTER TABLE tb_banktransfer_transaction ADD COLUMN destination_account_holder VARCHAR(255);
ALTER TABLE tb_banktransfer_transaction ADD COLUMN destination_account_holder_document VARCHAR(255);

ALTER TABLE tb_order MODIFY COLUMN type enum('PURCHASE', 'TRANSFER_BETWEEN_WALLETS', 'CASH_IN', 'RELEASE', 'REFUND', 'CASH_BACK', 'STORE_PURCHASE', 'GIFT_CASH_IN', 'CASH_OUT', 'STORE_CASH_IN', 'BANK_CASH_IN');