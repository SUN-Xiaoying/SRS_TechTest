package yahoofinance.histquotes2;

import com.xiao.yahoofinance.histquotes2.HistQuotes2Request;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static lombok.AccessLevel.PRIVATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@FieldDefaults(level = PRIVATE)
class HistQuotes2RequestTest {

    Calendar today;
    Calendar from;

    @BeforeEach
    void setup() {

        today = Calendar.getInstance();
        today.set(Calendar.YEAR, 2023);
        today.set(Calendar.MONTH, 3);
        today.set(Calendar.DATE, 19);

        from = (Calendar) today.clone();
        from.add(Calendar.YEAR, -1);
    }

    @ParameterizedTest
    @ValueSource(ints = { 3, 8 })
    void testDefaultRequest(int sample) throws IOException {
        // given:
        var data = MessageUtils.loadHistQuotesCSV("SHEL", "/historicalQuoteRequest/SHEL_1Y_D.csv");

        // when:
        var request = new HistQuotes2Request("SHEL", from, today);
        var result = request.getResult();

        assertNotNull(result);
        assertEquals(12, result.size());
        assertEquals(data.get(sample).getDate(), result.get(sample).getDate());
        assertEquals(data.get(sample).getOpen(), result.get(sample).getOpen());
        assertEquals(data.get(sample).getLow(), result.get(sample).getLow());
        assertEquals(data.get(sample).getHigh(), result.get(sample).getHigh());
        assertEquals(data.get(sample).getClose(), result.get(sample).getClose());
        assertEquals(data.get(sample).getAdjClose(), result.get(sample).getAdjClose());
        assertEquals(data.get(sample).getVolume(), result.get(sample).getVolume());
    }

    @ParameterizedTest
    @CsvSource({
            "DAILY, 230",
            "WEEKLY, 49",
            "MONTHLY, 12"

    })
    void testDifferentInterval(Interval interval, int size) throws IOException, InterruptedException {

        Stock stock = YahooFinance.get("SHEL", from, today, interval);
        TimeUnit.SECONDS.sleep(1);
        assertNotNull(stock.getHistory());
        assertEquals(size, stock.getHistory().size());
    }

    @SneakyThrows
    @Test
    void testImpossibleRequest() {

        Calendar test = Calendar.getInstance();
        test.add(Calendar.MONTH, 1);
        assertThrows(IOException.class, () -> YahooFinance.get("SHEL", test, today));
    }
}
