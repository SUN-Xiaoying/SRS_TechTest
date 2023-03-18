package yahoofinance.mockserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockserver.client.MockServerClient;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class MockServerTestUtils {

    private static final long FIVE_SECONDS = 5_000L;
    private static final MockServerClient mockServerClient = new MockServerClient("localhost", 1080);

    private static final Logger log = Logger.getLogger(MockServerTestUtils.class.getName());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void setUpQuoteQuery1v7Response(String symbol, String body) {

        log.info("Registering mock QUOTES_QUERY1V7_BASE_URL response");

        mockServerClient
                .when(
                        request()
                                .withMethod(RequestMethod.GET.name())
                                .withPath("/query1.finance.yahoo.com/v7/finance/quote?symbols:" + symbol),
                        Times.unlimited(),
                        TimeToLive.exactly(TimeUnit.MILLISECONDS, FIVE_SECONDS)
                     )
                .respond(
                        response()
                                .withStatusCode(HttpStatus.OK.value())
                                .withHeaders(new Header(HttpHeaders.CONTENT_TYPE, "application/json"))
                                .withBody(body)
                        );
    }

    public static void resetMockServer() {

        mockServerClient.reset();
    }
}
