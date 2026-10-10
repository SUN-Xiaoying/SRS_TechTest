package com.xiao.yahoofinance.histquotes2;


public enum QueryInterval {

    DAILY("1d"),
    WEEKLY("1wk"),
    MONTHLY("1mo");

    private final String tag;

    QueryInterval(String tag) {

        this.tag = tag;
    }

    public String getTag() {

        return this.tag;
    }

}
