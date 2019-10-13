package com.amedigital.wallet.constants;

import java.time.format.DateTimeFormatter;

public class Constants {

    // path
    public static final String WALLET_CONTEXT_PATH = "/wallet/v2";

    public static final String WALLET_CONTEXT_PATH_V3 = "/wallet/v3";

    //manager wallet id
    public static final Long DEFAULT_MANAGER_WALLET_ID = 1500000L;
    public static final Long FAST_CASH_MANAGER_WALLET_ID = 1499999L;

    //order type
    public static final String PURCHASE_ORDER_TYPE = "compra";
    public static final String CASH_IN_ORDER_TYPE = "cash in";
    public static final String TRANSFER_BETWEEN_WALLETS_ORDER_TYPE = "transferência entre carteiras";
    public static final String RELEASE_ORDER_TYPE = "liquidação";
    public static final String GIFT_CASH_IN_ORDER_TYPE = "Cash in de presente";
    public static final String CASH_OUT_ORDER_TYPE = "Cash Out Order";
    public static final String STORE_CASH_IN_ORDER_TYPE = "Cash in em loja física";
    public static final String STORE_CASH_OUT_ORDER_TYPE = "Cash Out em loja física";
    public static final String BANK_CASH_IN_ORDER_TYPE = "Cash in transferencia bancaria";

    // Formatação de datas
    public static final DateTimeFormatter INTERNATIONAL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
