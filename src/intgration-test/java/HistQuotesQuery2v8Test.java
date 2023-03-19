import com.xiao.yahoofinance.YahooFinance;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import yahoofinance.histquotes.HistoricalQuote;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@FieldDefaults(level = PRIVATE)
class HistQuotesQuery2v8Test {

    Calendar today;
    Calendar from;
    final String SHEL_SYMBOL = "SHEL";
    final static SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");

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
    @ParameterizedTest
    @ValueSource(ints = { 5, 11 })
    void test_Shel_HistQuotes_OneYear_Daily(int sample) {

        // given:
        val message = MessageUtils.loadMessage("/responses/query2v8/shel-ok.json");
        val resource = message.get("chart").get("result").get(0).get("indicators").get("quote").get(0);

        // when:
        List<HistoricalQuote> histquotes = YahooFinance.getHistQuotes(SHEL_SYMBOL, from, today);

        assertNotNull(histquotes);
        assertEquals(12, histquotes.size());

        val histquote = histquotes.get(sample);
        assertEquals(SHEL_SYMBOL, histquote.getSymbol());
        assertEquals(transDate(message.get("chart").get("result").get(0).get("timestamp").get(sample).asLong()), histquote.getDate());
        assertEquals(resource.get("open").get(sample).decimalValue(), histquote.getOpen());
        assertEquals(resource.get("high").get(sample).decimalValue(), histquote.getHigh());
        assertEquals(resource.get("low").get(sample).decimalValue(), histquote.getLow());
        assertEquals(resource.get("close").get(sample).decimalValue(), histquote.getClose());
    }

    @SneakyThrows
    @Test
    void testThrowFileNotFoundException() {

        assertThrows(FileNotFoundException.class, () -> YahooFinance.getHistQuotes("SHELL", from, today));
    }

    @SneakyThrows
    @Test
    void testThrowNullPointerException() {

        assertThrows(NullPointerException.class, () -> YahooFinance.getHistQuotes("AWS", from, today));
    }


    Calendar transDate(long timestamp) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp * 1000);
        return calendar;
    }
}
