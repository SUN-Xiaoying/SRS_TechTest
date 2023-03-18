package yahoofinance.histquotes2;

import org.junit.jupiter.api.Test;
import yahoofinance.MessageUtils;
import yahoofinance.histquotes.HistoricalQuote;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HistQuotes2RequestTest {

    private static final SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    void testParseCSV() throws IOException {

        List<HistoricalQuote> result = MessageUtils.loadHistQuotesCSV("SHEL", "/messages/histquotes2/SHEL_1Y_D.csv");

        assertNotNull(result);
        assertEquals(251, result.size());

        var latestQuote = result.get(result.size() - 1);

        assertEquals("2023-03-17", simpleFormat.format(latestQuote.getDate().getTime()));
        assertEquals(BigDecimal.valueOf(54.549999), latestQuote.getOpen());
        assertEquals(BigDecimal.valueOf(54.705002), latestQuote.getHigh());
        assertEquals(BigDecimal.valueOf(53.580002), latestQuote.getLow());
        assertEquals(BigDecimal.valueOf(53.950001), latestQuote.getClose());
        assertEquals(BigDecimal.valueOf(53.950001), latestQuote.getAdjClose());
    }
}
