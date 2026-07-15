package com.pers.util.constant;

import com.pers.enums.Currency;

import java.util.Map;

public class Constants {

    public static final String ACCOUNT_NAME = "Счет-";
    public static final String DEFAULT_ACCOUNT_NAME = "Основной счет";
    public static final Map<Currency, String> CURRENCY_PREFIXES = Map.of(
            Currency.RUB, "220011",
            Currency.USD, "440055",
            Currency.CNY, "620099"
    );
    public static final String DEFAULT_CARD_NUMBER_PREFIX = "400000";
    public static final int CARD_NUMBER_LENGTH = 16;
    public static final long YEARS_TO_EXPIRED = 5L;
    public static final int CASHBACK_DEFAULT_VALUE = 0;

    /**
     * Keycloak constants
     */
    public static final String KEYCLOAK_NAME = "name";
    public static final String KEYCLOAK_FAMILY_NAME = "family_name";
    public static final String KEYCLOAK_PHONE = "phone";
    public static final String KEYCLOAK_SUB = "sub";

}
