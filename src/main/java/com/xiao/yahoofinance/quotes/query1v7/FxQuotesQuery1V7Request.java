package com.xiao.yahoofinance.quotes.query1v7;

import com.fasterxml.jackson.databind.JsonNode;
import com.xiao.yahoofinance.Utils;
import com.xiao.yahoofinance.quotes.fx.FxQuote;

import java.math.BigDecimal;


public class FxQuotesQuery1V7Request extends QuotesRequest<FxQuote> {

    public FxQuotesQuery1V7Request(String symbols) {

        super(symbols);
    }

    @Override
    protected FxQuote parseJson(JsonNode node) {

        String symbol = node.get("symbol").asText();
        BigDecimal price = Utils.getBigDecimal(node.get("regularMarketPrice").asText());

        return new FxQuote(symbol, price);
    }
}
