import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import yahoofinance.Utils;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class MessageUtils {

    public static JsonNode loadMessage(String filepath) throws IOException {

        var mapper = new ObjectMapper();
        InputStream message = MessageUtils.class.getResourceAsStream(filepath);
        InputStreamReader is = new InputStreamReader(Objects.requireNonNull(message));

        return mapper.readTree(is);
    }

    public static List<HistoricalQuote> loadHistQuotesCSV(String symbol, String filepath) throws IOException {

        List<HistoricalQuote> result = new ArrayList<>();


        InputStream message = MessageUtils.class.getResourceAsStream(filepath);
        InputStreamReader is = new InputStreamReader(message);
        BufferedReader br = new BufferedReader(is);
        String line = br.readLine();
        while ((line = br.readLine()) != null) {
            HistoricalQuote quote = MessageUtils.parseCSVLine(symbol, line);
            result.add(quote);
        }

        return result;
    }

    private static HistoricalQuote parseCSVLine(String symbol, String line) {

        String[] data = line.split(YahooFinance.QUOTES_CSV_DELIMITER);
        return new HistoricalQuote(symbol,
                                   Utils.parseHistDate(data[0]),
                                   Utils.getBigDecimal(data[1]),
                                   Utils.getBigDecimal(data[3]),
                                   Utils.getBigDecimal(data[2]),
                                   Utils.getBigDecimal(data[4]),
                                   Utils.getBigDecimal(data[5]),
                                   Utils.getLong(data[6])
        );
    }
}
