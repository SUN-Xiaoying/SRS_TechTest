package com.xiao.yahoofinance.query2v8;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.xiao.yahoofinance.Utils;
import com.xiao.yahoofinance.YahooFinance;
import com.xiao.yahoofinance.histquotes.HistoricalQuote;
import com.xiao.yahoofinance.histquotes2.QueryInterval;
import com.xiao.yahoofinance.util.RedirectableRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HistQuotesQuery2V8Request {

    private static final Logger log = LoggerFactory.getLogger(HistQuotesQuery2V8Request.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String symbol;
    private final Calendar from;
    private final Calendar to;
    private final QueryInterval interval;

    public static final Calendar DEFAULT_FROM = Calendar.getInstance();

    static {
        DEFAULT_FROM.add(Calendar.YEAR, -1);
    }

    public static final Calendar DEFAULT_TO = Calendar.getInstance();
    public static final QueryInterval DEFAULT_INTERVAL = QueryInterval.MONTHLY;

    public HistQuotesQuery2V8Request(String symbol) {

        this(symbol, DEFAULT_INTERVAL);
    }

    public HistQuotesQuery2V8Request(String symbol, QueryInterval interval) {

        this(symbol, DEFAULT_FROM, DEFAULT_TO, interval);
    }


    public HistQuotesQuery2V8Request(String symbol, Calendar from, Calendar to) {

        this(symbol, from, to, DEFAULT_INTERVAL);
    }

    public HistQuotesQuery2V8Request(String symbol, Calendar from, Calendar to, QueryInterval interval) {

        this.symbol = symbol;
        this.from = this.cleanHistCalendar(from);
        this.to = this.cleanHistCalendar(to);
        this.interval = interval;
    }

    public HistQuotesQuery2V8Request(String symbol, Date from, Date to) {

        this(symbol, from, to, DEFAULT_INTERVAL);
    }

    public HistQuotesQuery2V8Request(String symbol, Date from, Date to, QueryInterval interval) {

        this(symbol, interval);
        this.from.setTime(from);
        this.to.setTime(to);
        this.from.setTimeInMillis(this.cleanHistCalendar(this.from).getTimeInMillis());
        this.to.setTimeInMillis(this.cleanHistCalendar(this.to).getTimeInMillis());
    }

    /**
     * Put everything smaller than days at 0
     *
     * @param cal calendar to be cleaned
     */
    private Calendar cleanHistCalendar(Calendar cal) {

        cal = (Calendar) cal.clone();

        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal;
    }

    public List<HistoricalQuote> getResult() throws IOException {

        if (from.after(to)) {
            return new ArrayList<>();
        }
        String json = getJson();
        JsonNode root = objectMapper.readTree(json);
        JsonNode results = root == null ? null : root.path("chart").path("result");
        if (results == null || !results.isArray() || results.isEmpty()) {
            throw new IOException("No historical data for " + symbol);
        }
        JsonNode resultNode = results.get(0);
        if (!resultNode.path("timestamp").isArray() || resultNode.path("timestamp").isEmpty()
                || !resultNode.path("indicators").path("quote").isArray()
                || resultNode.path("indicators").path("quote").isEmpty()
                || !resultNode.path("indicators").path("adjclose").isArray()
                || resultNode.path("indicators").path("adjclose").isEmpty()) {
            throw new IOException("Incomplete historical data for " + symbol);
        }
        JsonNode timestamps = resultNode.get("timestamp");
        JsonNode indicators = resultNode.get("indicators");
        JsonNode quotes = indicators.get("quote").get(0);
        JsonNode closes = quotes.get("close");
        JsonNode volumes = quotes.get("volume");
        JsonNode opens = quotes.get("open");
        JsonNode highs = quotes.get("high");
        JsonNode lows = quotes.get("low");
        JsonNode adjCloses = indicators.get("adjclose").get(0).get("adjclose");

        List<HistoricalQuote> result = new ArrayList<HistoricalQuote>();
        for (int i = 0; i < timestamps.size(); i++) {
            long timestamp = timestamps.get(i).asLong();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp * 1000);
            BigDecimal adjClose = adjCloses.get(i).decimalValue();
            long volume = volumes.get(i).asLong();
            BigDecimal open = opens.get(i).decimalValue();
            BigDecimal high = highs.get(i).decimalValue();
            BigDecimal low = lows.get(i).decimalValue();
            BigDecimal close = closes.get(i).decimalValue();

            HistoricalQuote quote = new HistoricalQuote(
                    symbol,
                    calendar,
                    open,
                    low,
                    high,
                    close,
                    adjClose,
                    volume);
            result.add(quote);
        }

        return result;
    }

    public String getJson() throws IOException {

        if (this.from.after(this.to)) {
            log.warn("Unable to retrieve historical quotes. "
                     + "From-date should not be after to-date. From: "
                     + this.from.getTime() + ", to: " + this.to.getTime());
            return "";
        }

        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("period1", String.valueOf(this.from.getTimeInMillis() / 1000));
        params.put("period2", String.valueOf(this.to.getTimeInMillis() / 1000));
        params.put("interval", this.interval.getTag());
        params.put("events", "div|split");

        String url = YahooFinance.HISTQUOTES_QUERY2V8_BASE_URL + URLEncoder.encode(this.symbol, "UTF-8") + "?" + Utils.getURLParameters(params);

        // Get CSV from Yahoo
        log.info("Sending request: " + url);

        URL request = new URL(url);
        RedirectableRequest redirectableRequest = new RedirectableRequest(request, 5);
        redirectableRequest.setConnectTimeout(YahooFinance.CONNECTION_TIMEOUT);
        redirectableRequest.setReadTimeout(YahooFinance.CONNECTION_TIMEOUT);
        URLConnection connection = redirectableRequest.openConnection();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if (builder.length() > 0) {
                    builder.append("\n");
                }
                builder.append(line);
            }
            return builder.toString();
        }
    }

}
