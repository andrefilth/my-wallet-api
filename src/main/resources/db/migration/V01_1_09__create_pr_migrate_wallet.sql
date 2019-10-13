USE db_wallet_v2;

DROP PROCEDURE IF EXISTS db_wallet_v2.pr_run_migrate_wallet;

DELIMITER $$

CREATE PROCEDURE db_wallet_v2.pr_run_migrate_wallet()
BEGIN

    DECLARE v_wallet_id BIGINT(30) DEFAULT 0;
    DECLARE v_wallet_uuid varchar(255) DEFAULT 0;
    DECLARE v_finished INTEGER DEFAULT 0;

    DECLARE c_wallets_with_transfer CURSOR FOR
    SELECT wtm.wallet_id
         , wtm.wallet_uuid
        --
      FROM db_wallet_v2.tb_wallet_to_migrate wtm
        --
      JOIN db_wallet.TB_WALLET_TRANSACTION wt
        ON wtm.wallet_id = wt.wallet_id
        --
     WHERE wt.type = 'TRANSFER'
    ;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_finished = 1;


    OPEN c_wallets_with_transfer;
    get_wallet: LOOP
        FETCH c_wallets_with_transfer INTO v_wallet_id, v_wallet_uuid;

        IF v_finished = 1 THEN
            LEAVE get_wallet;
        END IF;

        call db_wallet_v2.pr_load_tb_order_transfer(v_wallet_id, v_wallet_uuid);
        commit;

    END LOOP get_wallet;
    CLOSE c_wallets_with_transfer;

    commit;

END$$

DELIMITER ;