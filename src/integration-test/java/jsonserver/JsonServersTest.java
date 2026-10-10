package jsonserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/** Fixture-backed endpoints. Each test class runs in its own JVM. */
public class JsonServersTest {
    public static MockWebServer quotes1v7Server;
    public static MockWebServer histQuotes2v8Server;
    public static MockWebServer histCsvServer;

    public static String fixture(String path) throws IOException {
        try (var stream = Objects.requireNonNull(JsonServersTest.class.getResourceAsStream(path), path)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @BeforeAll
    public static void start() throws IOException {
        quotes1v7Server = new MockWebServer();
        histQuotes2v8Server = new MockWebServer();
        histCsvServer = new MockWebServer();
        quotes1v7Server.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                try {
                    String symbols = request.getRequestUrl().queryParameter("symbols");
                    if (symbols == null) return new MockResponse().setResponseCode(400);
                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode response = mapper.createObjectNode();
                    ArrayNode results = response.putObject("quoteResponse").putArray("result");
                    JsonNode available = mapper.readTree(fixture("/responses/query1v7/regional-quotes.json"))
                            .path("quoteResponse").path("result");
                    for (String symbol : symbols.split(",")) {
                        for (JsonNode quote : available) {
                            if (quote.path("symbol").asText().equals(symbol)) results.add(quote);
                        }
                    }
                    return new MockResponse().setBody(response.toString());
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
            }
        });
        histQuotes2v8Server.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                String symbol = request.getRequestUrl().pathSegments().get(0);
                String file = switch (symbol) {
                    case "SHEL" -> "shel-ok.json";
                    case "SHELL" -> "shell-file-not-found.json";
                    case "AWS" -> "aws-null-pointer.json";
                    default -> null;
                };
                if (file == null) return new MockResponse().setResponseCode(404);
                try {
                    return new MockResponse().setResponseCode(symbol.equals("SHELL") ? 404 : 200)
                            .setBody(fixture("/responses/query2v8/" + file));
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
            }
        });
        histCsvServer.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                String symbol = request.getRequestUrl().pathSegments().get(0);
                String interval = request.getRequestUrl().queryParameter("interval");
                String file = switch (symbol + ":" + interval) {
                    case "SHEL:1mo" -> "SHEL_1Y_D.csv";
                    case "TSLA:1d" -> "TSLA_1Y_D.csv";
                    case "SCTY:1wk" -> "SCTY_1Y_W.csv";
                    default -> null;
                };
                if (file == null) return new MockResponse().setResponseCode(404);
                try {
                    String csv = fixture("/historicalQuoteRequest/" + file);
                    if (!symbol.equals("SHEL")) {
                        StringBuilder converted = new StringBuilder("Date,Open,High,Low,Close,Adj Close,Volume\n");
                        for (String line : csv.split("\\R")) {
                            if (line.startsWith("Date") || line.isBlank()) continue;
                            String[] fields = line.split(",");
                            converted.append(String.join(",", Arrays.copyOf(fields, 5)))
                                    .append(',').append(fields[6]).append(',').append(fields[5]).append('\n');
                        }
                        csv = converted.toString();
                    }
                    return new MockResponse().setBody(csv);
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
            }
        });
        quotes1v7Server.start();
        histQuotes2v8Server.start();
        histCsvServer.start();
        System.setProperty("yahoofinance.baseurl.quotesquery1v7", quotes1v7Server.url("/").toString());
        System.setProperty("yahoofinance.baseurl.histquotesquery2v8", histQuotes2v8Server.url("/").toString());
        System.setProperty("yahoofinance.baseurl.histquotes2", histCsvServer.url("/").toString());
        System.setProperty("yahoofinance.quotesquery1v7.enabled", "true");
        System.setProperty("yahoofinance.histquotes2.enabled", "true");
        System.setProperty("yahoofinance.histquotesquery2v8.enabled", "true");
        System.setProperty("yahoofinance.crumb", "test-crumb");
        System.setProperty("yahoofinance.cookie", "test-cookie");
    }

    @AfterAll
    public static void stop() throws IOException {
        quotes1v7Server.shutdown();
        histQuotes2v8Server.shutdown();
        histCsvServer.shutdown();
    }
}
