import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import yahoofinance.quotes.query1v7.StockQuotesQuery1V7Request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StockQuoteQuery1V7RequestTest {

    @SneakyThrows
    @Test
    void testParseJson() {

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
    }

    @SneakyThrows
    @Test
    void testMultipleSymbolsRequest() {

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

    @SneakyThrows
    @Test
    void testWrongSymbolRequest() {

        // given
        String symbols = "SHELL";

        StockQuotesQuery1V7Request request = new StockQuotesQuery1V7Request(symbols);

        // when
        var result = request.getResult();

        // then
        assertEquals(0, result.size());
    }
}
