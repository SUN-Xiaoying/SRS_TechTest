package yahoofinance.test

import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import lombok.AccessLevel
import lombok.experimental.FieldDefaults
import spock.lang.Shared
import yahoofinance.util.MockUtils

import java.util.logging.Logger

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class SimpleQuoteRequestSpecification extends IntegrationTestSpecification {

    Logger log = Logger.getLogger("SimpleQuoteRequestSpecification")

    @Shared
    RESTClient client

    def setup() {
        log.info('^^^^^^^^^^ Running: ' + specificationContext.currentFeature.name)
    }

    def 'Calling US stock quote should return stock'() {
        given:
        def symbol = "SHEL"
        MockUtils.setUpSuccessfulOneQuoteQuery1v7Response("SHEL")

        when:
        def response = client.get(path: "/query1.finance.yahoo.com/v7/finance/quote?".concat(symbol), contentType: ContentType.JSON)

        then:
        response != null
    }
}