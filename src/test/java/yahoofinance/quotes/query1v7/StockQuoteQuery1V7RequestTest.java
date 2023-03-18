package yahoofinance.quotes.query1v7;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import yahoofinance.MessageUtils;
import yahoofinance.Utils;

import java.io.IOException;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StockQuoteQuery1V7RequestTest {

    @Mock
    StockQuotesQuery1V7Request request = new StockQuotesQuery1V7Request("test");

    @Test
    void testParseJson() throws IOException {

        // given
        var message = MessageUtils.loadMessage("/messages/query1v7/one-quote-ok.json");
        var result = message.get("quoteResponse").get("result").get(0);

        // when
        var stock = request.parseJson(result);

        // then
        assertNotNull(stock);
        assertEquals(result.get("symbol").asText(), stock.getSymbol());
        assertEquals(result.get("longName").asText(), stock.getName());
        assertEquals(result.get("fullExchangeName").asText(), stock.getStockExchange());
        assertEquals(result.get("currency").asText(), stock.getCurrency());

        var quote = stock.getQuote();
        assertNotNull(quote);
        assertEquals(Utils.getBigDecimal(getStringValue(result, "regularMarketPrice")), quote.getPrice());
        assertEquals(Utils.getBigDecimal(getStringValue(result, "ask")), quote.getAsk());
        assertEquals(Utils.getLong(getStringValue(result, "askSize")), quote.getAskSize());
        assertEquals(Utils.getBigDecimal(result.get("bid").asText()), quote.getBid());
        assertEquals(Utils.getLong(result.get("bidSize").asText()), quote.getBidSize());
        assertEquals(Utils.getBigDecimal(getStringValue(result, "regularMarketOpen")), quote.getOpen());
        assertEquals(Utils.getBigDecimal(getStringValue(result, "regularMarketPreviousClose")), quote.getPreviousClose());
        assertEquals(Utils.getBigDecimal(getStringValue(result, "regularMarketDayHigh")), quote.getDayHigh());
        assertEquals(Utils.getBigDecimal(getStringValue(result, "regularMarketDayLow")), quote.getDayLow());

        assertEquals(TimeZone.getTimeZone(result.get("exchangeTimezoneName").asText()), quote.getTimeZone());
        assertEquals(Utils.unixToCalendar(result.get("regularMarketTime").asLong()), quote.getLastTradeTime());
        assertEquals(Utils.getBigDecimal(getStringValue(result, "fiftyTwoWeekHigh")), quote.getYearHigh());
        assertEquals(Utils.getBigDecimal(getStringValue(result, "fiftyTwoWeekLow")), quote.getYearLow());
        assertEquals(Utils.getBigDecimal(getStringValue(result, "fiftyDayAverage")), quote.getPriceAvg50());
        assertEquals(Utils.getBigDecimal(getStringValue(result, "twoHundredDayAverage")), quote.getPriceAvg200());

        assertEquals(Utils.getLong(getStringValue(result, "regularMarketVolume")), quote.getVolume());
        assertEquals(Utils.getLong(getStringValue(result, "averageDailyVolume3Month")), quote.getAvgVolume());

        var status = stock.getStats();
        assertNotNull(status);
        assertEquals(Utils.getBigDecimal(getStringValue(result, "marketCap")), status.getMarketCap());
        assertEquals(Utils.getLong(getStringValue(result, "sharesOutstanding")), status.getSharesOutstanding());
        assertEquals(Utils.getBigDecimal(getStringValue(result, "epsTrailingTwelveMonths")), status.getEps());
        assertEquals(Utils.getBigDecimal(getStringValue(result, "trailingPE")), status.getPe());
        assertEquals(Utils.getBigDecimal(getStringValue(result, "epsForward")), status.getEpsEstimateCurrentYear());
        assertEquals(Utils.getBigDecimal(getStringValue(result, "priceToBook")), status.getPriceBook());
        assertEquals(Utils.getBigDecimal(getStringValue(result, "bookValue")), status.getBookValuePerShare());

    }

    @Test
    void testMultipleSymbolsRequest() throws IOException {

        // given
        String symbols = "TSLA,AMZN,SHEL";

        StockQuotesQuery1V7Request request = new StockQuotesQuery1V7Request(symbols);

        // when
        var results = request.getResult();

        // then
        assertEquals(3, results.size());
        assertEquals("TSLA", results.get(0).getSymbol());
        assertEquals("AMZN", results.get(1).getSymbol());
        assertEquals("SHEL", results.get(2).getSymbol());
    }

    @Test
    void testWrongSymbolRequest() throws IOException {

        // given
        String symbols = "SHELL";

        StockQuotesQuery1V7Request request = new StockQuotesQuery1V7Request(symbols);

        // when
        var result = request.getResult();

        // then
        assertEquals(0, result.size());
    }

    private String getStringValue(JsonNode node, String field) {

        if (node.has(field)) {
            return node.get(field).asText();
        }
        return null;
    }
}
