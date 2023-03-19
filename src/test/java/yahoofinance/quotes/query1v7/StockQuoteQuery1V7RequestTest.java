package yahoofinance.quotes.query1v7;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StockQuoteQuery1V7RequestTest {

    @Test
    void testSingleSymbolRequest() throws IOException {

        // given
        String symbol = "SHEL";
        StockQuotesQuery1V7Request request = new StockQuotesQuery1V7Request(symbol);

        // when
        var stock = request.getResult().get(0);

        // then
        assertNotNull(stock);
        assertEquals(symbol, stock.getSymbol());
        assertEquals("Shell plc", stock.getName());
        assertEquals("NYSE", stock.getStockExchange());
        assertEquals("USD", stock.getCurrency());
        assertNotNull(stock.getQuote());
        assertNotNull(stock.getStats());
        assertNotNull(stock.getDividend());
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

    @ParameterizedTest
    @CsvSource({
            "SHEL, NYSE, USD",
            "AIR.PA, Paris, EUR",
            "9988.HK, HKSE, HKD",
            "C6L.SI, SES, SGD"
    })
    void testDifferentRegions(String symbol, String stockExchange, String currency) throws IOException {
        // when:
        Stock stock = YahooFinance.get(symbol);

        // then:
        assertNotNull(stock);
        assertEquals(stockExchange, stock.getStockExchange());
        assertEquals(currency, stock.getCurrency());
        assertNotNull(stock.getQuote());
        assertNotNull(stock.getStats());
        assertNotNull(stock.getDividend());
    }
}
