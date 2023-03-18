package yahoofinance.util

import groovy.util.logging.Slf4j
import yahoofinance.mockserver.MockServerTestUtils

@Slf4j
class MockUtils {

    static void setUpSuccessfulOneQuoteQuery1v7Response(String symbol) {
        def responseBody = MessageUtils.class.loadResponse("quote-query1v7-one-ok")
        MockServerTestUtils.setUpQuoteQuery1v7Response(symbol, responseBody)
    }
}
