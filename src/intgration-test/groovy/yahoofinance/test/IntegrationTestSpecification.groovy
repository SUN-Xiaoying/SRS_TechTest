package yahoofinance.test

import groovy.util.logging.Slf4j
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import spock.lang.Specification
import yahoofinance.mockserver.MockServerTestUtils

@Slf4j
class IntegrationTestSpecification extends Specification {

    MockWebServer mockWebServer = new MockWebServer()

    @BeforeEach
    def setup() {
        mockWebServer.start(1080)
    }

    /**
     * Executes cleanup logic after every test.
     * @return VOID
     */
    @AfterEach
    def cleanup() {
        MockServerTestUtils.resetMockServer()
    }
}
