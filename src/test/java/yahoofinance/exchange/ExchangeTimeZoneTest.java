package yahoofinance.exchange;


import lombok.experimental.FieldDefaults;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import yahoofinance.exchanges.ExchangeTimeZone;

import java.util.TimeZone;

import static lombok.AccessLevel.PRIVATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@FieldDefaults(level = PRIVATE)
class ExchangeTimeZoneTest {

    @ParameterizedTest
    @CsvSource({
            "SHEL, America/New_York",
            "AIR.PA, Europe/Paris",
            "9988.HK, Asia/Hong_Kong",
            "C6L.SI, Asia/Singapore"
    })
    void testGetTimeZone(String symbol, TimeZone timeZone) {

        assertEquals(timeZone, ExchangeTimeZone.getStockTimeZone(symbol));
    }
}
