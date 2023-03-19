package jsonserver;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonServersTest {

    private static final Logger log = LoggerFactory.getLogger(JsonServersTest.class);
    private static boolean started = false;

    public static MockWebServer quotes1v7Server;
    public static MockWebServer histQuotes2v8Server;

    @BeforeEach
    public void start() {

        if (started) {
            return;
        }
        started = true;
        quotes1v7Server = new MockWebServer();
        histQuotes2v8Server = new MockWebServer();
        try {
            quotes1v7Server.start();
            histQuotes2v8Server.start();
        } catch (IOException e) {
            log.error("Unable to start mock web server", e);
        }

        String quotesBaseUrl = "http://localhost:" + quotes1v7Server.getPort();
        String histQuotesBaseUrl = "http://localhost:" + histQuotes2v8Server.getPort();

        System.setProperty("yahoofinance.baseurl.quotes", quotesBaseUrl);
        System.setProperty("yahoofinance.baseurl.histquotesquery2v8", histQuotesBaseUrl);
        System.setProperty("yahoofinance.histquotes2.enabled", "true");
        System.setProperty("yahoofinance.quotesquery1v7.enabled", "true");
    }
}
