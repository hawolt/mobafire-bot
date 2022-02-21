package com.hawolt.http;

public enum ProxyGateway {
    AUSTRALIA("au", 2001, 9999),
    BELGIUM("be", 22001, 9999),
    CANADA("ca", 2001, 9999),
    FRANCE("fr", 32001, 9999),
    GREECE("gr", 32001, 9999),
    NETHERLANDS("nl", 12001, 9999),
    NORWAY("no", 42001, 999),
    POLAND("pl", 12001, 9999),
    RUSSIA("ru", 32001, 9999),
    SWEDEN("se", 22001, 9999),
    SWITZERLAND("ch", 44001, 999),
    TURKEY("tr", 32001, 9999),
    UKRAINE("ua", 2001, 9999),
    UNITED_KINGDOM("gb", 2001, 9999),
    UNITED_STATES("us", 2001, 9999);

    private final int start, amount;
    private final String code;

    ProxyGateway(String code, int start, int amount) {
        this.code = code;
        this.start = start;
        this.amount = amount;
    }

    public String getCode() {
        return code;
    }

    public int getStart() {
        return start;
    }

    public int getAmount() {
        return amount;
    }
}
