package yahoofinance.query2v8;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import yahoofinance.histquotes2.QueryInterval;

import java.io.IOException;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HistQuotesQuery2V8RequestTest {

    private static final String SHELL_SYMBOL = "SHEL";

    @ParameterizedTest
    @CsvSource({
            "MONTHLY, 13",
            "WEEKLY, 53",
            "DAILY, 251"
    })
    void testQueryInterval(String queryInterval, int size) throws IOException {
        // given
        HistQuotesQuery2V8Request request = new HistQuotesQuery2V8Request(SHELL_SYMBOL, QueryInterval.valueOf(queryInterval));

        // when
        var result = request.getResult();

        assertNotNull(result);
        assertEquals(SHELL_SYMBOL, result.get(0).getSymbol());
        assertEquals(size, result.size());
    }

    int getWorkingDaysBetweenTwoDates() {

        Calendar startCal = Calendar.getInstance();
        startCal.add(Calendar.YEAR, -1);
        Calendar endCal = Calendar.getInstance();

        int workDays = 0;

        do {
            //excluding start date
            startCal.add(Calendar.DAY_OF_MONTH, 1);
            if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                ++workDays;
            }
        } while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date

        return workDays;
    }

}
