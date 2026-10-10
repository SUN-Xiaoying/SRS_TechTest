package mock;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class MockedServersTest {

    private static final Logger log = LoggerFactory.getLogger(MockedServersTest.class);

    private static boolean started = false;

    public static MockWebServer quotesServer;
    public static MockWebServer histQuotesServer;

    @BeforeAll
    public static void startServers() throws IOException {

        if (started) {
            return;
        }
        quotesServer = new MockWebServer();
        histQuotesServer = new MockWebServer();
        quotesServer.start();
        histQuotesServer.start();
        String quotesBaseUrl = "http://localhost:" + quotesServer.getPort() + "/d/quotes.csv";
        String histQuotesBaseUrl = "http://localhost:" + histQuotesServer.getPort() + "/table.csv";

        System.setProperty("yahoofinance.baseurl.quotes", quotesBaseUrl);
        System.setProperty("yahoofinance.baseurl.histquotes", histQuotesBaseUrl);
        System.setProperty("yahoofinance.histquotes2.enabled", "false");
        System.setProperty("yahoofinance.quotesquery1v7.enabled", "false");

        final Dispatcher dispatcher = new YahooFinanceDispatcher();
        quotesServer.setDispatcher(dispatcher);
        histQuotesServer.setDispatcher(dispatcher);
        started = true;
    }

    @AfterAll
    public static void stopServers() throws IOException {
        quotesServer.shutdown();
        histQuotesServer.shutdown();
        started = false;
    }
}
