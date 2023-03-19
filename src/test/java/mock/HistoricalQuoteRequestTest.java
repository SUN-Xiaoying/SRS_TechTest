package mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.histquotes2.MessageUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoricalQuoteRequestTest extends MockedServersTest {

    private Calendar today;
    private Calendar from;

    @BeforeEach
    void setup() {

        today = Calendar.getInstance();
        today.set(Calendar.YEAR, 2016);
        today.set(Calendar.MONTH, 8);
        today.set(Calendar.DATE, 11);

        from = (Calendar) today.clone();
        from.add(Calendar.YEAR, -1);
    }

    @ParameterizedTest
    @ValueSource(ints = { 2, 6 })
    void historicalQuoteTest(int sample) throws IOException {
        // given:
        var data = MessageUtils.loadHistQuotesCSV_old("GOOG", "/historicalQuoteRequest/GOOG_1Y_M.csv");

        // when:
        Stock goog = YahooFinance.get("GOOG", from, today);

        // then:
        assertNotNull(goog.getHistory());
        assertEquals(13, goog.getHistory().size());

        for (HistoricalQuote histQuote : goog.getHistory()) {
            assertEquals("GOOG", histQuote.getSymbol());
            assertTrue(histQuote.getAdjClose().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(histQuote.getClose().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(histQuote.getHigh().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(histQuote.getLow().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(histQuote.getOpen().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(histQuote.getVolume() > 0);
            assertNotNull(histQuote.getDate());
        }

        HistoricalQuote histQuote = goog.getHistory().get(sample);
        var sampleData = data.get(sample);

        assertEquals(sampleData.getAdjClose(), histQuote.getAdjClose());
        assertEquals(sampleData.getClose(), histQuote.getClose());
        assertEquals(sampleData.getHigh(), histQuote.getHigh());
        assertEquals(sampleData.getLow(), histQuote.getLow());
        assertEquals(sampleData.getOpen(), histQuote.getOpen());
        assertEquals(sampleData.getVolume(), histQuote.getVolume());
        assertEquals(sampleData.getDate().get(Calendar.MONTH), histQuote.getDate().get(Calendar.MONTH));
        assertEquals(sampleData.getDate().get(Calendar.DATE), histQuote.getDate().get(Calendar.DATE));
        assertEquals(sampleData.getDate().get(Calendar.YEAR), histQuote.getDate().get(Calendar.YEAR));

    }

    @Test
    void multiYearTest() throws IOException {

        Calendar from = (Calendar) today.clone();
        Calendar to = (Calendar) today.clone();
        from.add(Calendar.YEAR, -5); // from 5 years ago

        Stock goog = YahooFinance.get("GOOG", from, to, Interval.WEEKLY);

        assertEquals(261, goog.getHistory().size());

        HistoricalQuote histQuote = goog.getHistory().get(0);
        assertEquals(8, histQuote.getDate().get(Calendar.MONTH));
        assertEquals(6, histQuote.getDate().get(Calendar.DATE));
        assertEquals(2016, histQuote.getDate().get(Calendar.YEAR));

        histQuote = goog.getHistory().get(260);
        assertEquals(8, histQuote.getDate().get(Calendar.MONTH));
        assertEquals(12, histQuote.getDate().get(Calendar.DATE));
        assertEquals(2011, histQuote.getDate().get(Calendar.YEAR));

    }

    @Test
    void historicalFlowTest() throws IOException {

        Stock goog = YahooFinance.get("GOOG");
        int requestCount = MockedServersTest.histQuotesServer.getRequestCount();
        assertNotNull(goog.getHistory(from, today));
        requestCount += 1;
        assertEquals(requestCount, MockedServersTest.histQuotesServer.getRequestCount());
        assertEquals(13, goog.getHistory().size());
        assertEquals(requestCount, MockedServersTest.histQuotesServer.getRequestCount());

        Calendar from = (Calendar) today.clone();
        Calendar to = (Calendar) today.clone();
        from.add(Calendar.YEAR, -5); // from 5 years ago
        assertNotNull(goog.getHistory(from, to, Interval.WEEKLY));
        requestCount += 1;
        assertEquals(requestCount, MockedServersTest.histQuotesServer.getRequestCount());
        assertEquals(261, goog.getHistory().size());
    }

    @Test
    void impossibleRequestTest() throws IOException {

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.DATE, 2); // from > to
        Exception reqEx = null;

        Stock goog = YahooFinance.get("GOOG");
        List<HistoricalQuote> histQuotes = null;
        int requestCount = MockedServersTest.histQuotesServer.getRequestCount();
        try {
            histQuotes = goog.getHistory(from, to);
        } catch (IOException ex) {
            reqEx = ex;
        }
        // Didn't send any requests since the problem was detected
        assertEquals(requestCount, MockedServersTest.histQuotesServer.getRequestCount());
        assertNull(reqEx);
        assertEquals(0, histQuotes.size());
    }

}
