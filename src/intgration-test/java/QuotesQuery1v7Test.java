import com.fasterxml.jackson.databind.JsonNode;
import com.xiao.yahoofinance.YahooFinance;
import jsonserver.JsonServersTest;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import yahoofinance.Stock;
import yahoofinance.Utils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import static lombok.AccessLevel.PRIVATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@FieldDefaults(level = PRIVATE)
class QuotesQuery1v7Test extends JsonServersTest {

    Calendar today;
    Calendar from;
    final String SHEL_SYMBOL = "SHEL";
    final String GOOG_SYMBOL = "GOOG";
    final String AMZN_SYMBOL = "AMZN";
    final String WRONG_SYMBOL = "SHELL";

    @BeforeEach
    void setup() {

        today = Calendar.getInstance();
        today.set(Calendar.YEAR, 2023);
        today.set(Calendar.MONTH, 3);
        today.set(Calendar.DATE, 19);

        from = (Calendar) today.clone();
        from.add(Calendar.YEAR, -1);
    }

    @SneakyThrows
    @Test
    void testOneSymbol() {

        // given:
        val message = MessageUtils.loadMessage("/responses/query1v7/one-quote-ok.json");
        val result = message.get("quoteResponse").get("result").get(0);

        // when:
        Stock stock = YahooFinance.get(SHEL_SYMBOL, from, today);

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
        var message = MessageUtils.loadMessage("/responses/query1v7/multi-quotes-all-ok.json");
        var results = message.get("quoteResponse").get("result");

        // when
        Map<String, Stock> stocks = YahooFinance.get(new String[]{ GOOG_SYMBOL, AMZN_SYMBOL, SHEL_SYMBOL }, from, today);

        // then
        assertEquals(3, stocks.size());
        val google = stocks.get(GOOG_SYMBOL);
        assertEquals(results.get(0).get("symbol").asText(), google.getSymbol());
        assertEquals(results.get(0).get("longName").asText(), google.getName());
        assertEquals(results.get(0).get("fullExchangeName").asText(), google.getStockExchange());
        assertEquals(results.get(0).get("currency").asText(), google.getCurrency());

        val amazon = stocks.get(AMZN_SYMBOL);
        assertEquals(results.get(1).get("symbol").asText(), amazon.getSymbol());
        assertEquals(results.get(1).get("longName").asText(), amazon.getName());
        assertEquals(results.get(1).get("fullExchangeName").asText(), amazon.getStockExchange());
        assertEquals(results.get(1).get("currency").asText(), amazon.getCurrency());

        val shell = stocks.get(SHEL_SYMBOL);
        assertEquals(results.get(2).get("symbol").asText(), shell.getSymbol());
        assertEquals(results.get(2).get("longName").asText(), shell.getName());
        assertEquals(results.get(2).get("fullExchangeName").asText(), shell.getStockExchange());
        assertEquals(results.get(2).get("currency").asText(), shell.getCurrency());
    }

    @Test
    void testWrongSymbolRequest() throws IOException {

        Stock stock = YahooFinance.get(WRONG_SYMBOL, from, today);

        assertNull(stock);
    }

    private String getStringValue(JsonNode node, String field) {

        if (node.has(field)) {
            return node.get(field).asText();
        }
        return null;
    }

}
