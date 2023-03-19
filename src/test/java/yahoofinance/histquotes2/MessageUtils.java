package yahoofinance.histquotes2;

import yahoofinance.Utils;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MessageUtils {

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

    public static List<HistoricalQuote> loadHistQuotesCSV_old(String symbol, String filepath) throws IOException {

        List<HistoricalQuote> result = new ArrayList<>();

        InputStream message = MessageUtils.class.getResourceAsStream(filepath);
        InputStreamReader is = new InputStreamReader(message);
        BufferedReader br = new BufferedReader(is);
        String line = br.readLine();
        while ((line = br.readLine()) != null) {
            HistoricalQuote quote = MessageUtils.parseCSVLine_old(symbol, line);
            result.add(quote);
        }

        return result;
    }

    private static HistoricalQuote parseCSVLine_old(String symbol, String line) {

        String[] data = line.split(YahooFinance.QUOTES_CSV_DELIMITER);
        return new HistoricalQuote(symbol,
                                   Utils.parseHistDate(data[0]),
                                   Utils.getBigDecimal(data[1]),
                                   Utils.getBigDecimal(data[3]),
                                   Utils.getBigDecimal(data[2]),
                                   Utils.getBigDecimal(data[4]),
                                   Utils.getBigDecimal(data[6]),
                                   Utils.getLong(data[5])
        );
    }
}
