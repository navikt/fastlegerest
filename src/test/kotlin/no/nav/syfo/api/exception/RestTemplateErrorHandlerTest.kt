package no.nav.syfo.api.exception

import no.nav.syfo.LocalApplication
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate
import javax.inject.Inject

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
class RestTemplateErrorHandlerTest {
    @Inject
    @Qualifier("Oidc")
    private lateinit var restTemplate: RestTemplate

    private lateinit var mockRestServiceServer: MockRestServiceServer

    @BeforeEach
    fun setUp() {
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build()
    }

    @AfterEach
    fun tearDown() {
        mockRestServiceServer.reset()
    }

    @Test
    fun no_exception_if_OK_from_server() {
        val url = "http://test.no"
        val method = HttpMethod.GET
        mockRestRequest(url, method, HttpStatus.OK)
        doRestTemplateExchange(url, method)
    }

    @Test
    fun server_error_from_resttemplate_gives_runtimeexception() {
        val url = "http://test.no"
        val method = HttpMethod.GET
        mockRestRequest(url, method, HttpStatus.INTERNAL_SERVER_ERROR)
        try {
            doRestTemplateExchange(url, method)
        } catch (e: RuntimeException) {
            Assertions.assertThat(e.message).isEqualTo("Fikk en server-error ved $method-kall til $url")
        } catch (e: Exception) {
            Assertions.fail("Got the wrong type of exception")
        }
    }

    @Test
    fun client_error_from_resttemplate_thats_not_forbidden_gives_runtimeexception() {
        val url = "http://test.no"
        val method = HttpMethod.GET
        mockRestRequest(url, method, HttpStatus.NOT_FOUND)
        try {
            doRestTemplateExchange(url, method)
        } catch (e: RuntimeException) {
            Assertions.assertThat(e.message).isEqualTo("Noe gikk galt ved $method-kall til $url")
        } catch (e: Exception) {
            Assertions.fail("Got the wrong type of exception")
        }
    }

    @Test
    fun client_error_from_resttemplate_thats_forbidden_does_not_give_exception() {
        val url = "http://test.no"
        val method = HttpMethod.GET
        mockRestRequest(url, method, HttpStatus.FORBIDDEN)
        doRestTemplateExchange(url, method)
    }

    @Test
    fun error_from_restTemplateErrorHandler_gives_url_without_fnr_in_thrown_exception() {
        val url = "http://test.no/?fnr=12345678901"
        val urlWithHiddenFnr = "http://test.no/?fnr=***********"
        val method = HttpMethod.GET
        mockRestRequest(url, method, HttpStatus.INTERNAL_SERVER_ERROR)
        try {
            doRestTemplateExchange(url, method)
        } catch (e: RuntimeException) {
            Assertions.assertThat(e.message).isEqualTo("Fikk en server-error ved $method-kall til $urlWithHiddenFnr")
        } catch (e: Exception) {
            Assertions.fail("Got the wrong type of exception")
        }
    }

    @Test
    fun error_from_restTemplateErrorHandler_gives_correct_method_in_thrown_exception() {
        val url = "http://test.no"
        val urlWithHiddenFnr = "http://test.no"
        val method = HttpMethod.POST
        mockRestRequest(url, method, HttpStatus.INTERNAL_SERVER_ERROR)
        try {
            doRestTemplateExchange(url, method)
        } catch (e: RuntimeException) {
            Assertions.assertThat(e.message).isEqualTo("Fikk en server-error ved $method-kall til $urlWithHiddenFnr")
        } catch (e: Exception) {
            Assertions.fail("Got the wrong type of exception")
        }
    }

    private fun mockRestRequest(url: String, httpMethod: HttpMethod, httpStatus: HttpStatus) {
        mockRestServiceServer.expect(ExpectedCount.once(), MockRestRequestMatchers.requestTo(url))
            .andExpect(MockRestRequestMatchers.method(httpMethod))
            .andRespond(MockRestResponseCreators.withStatus(httpStatus))
    }

    private fun doRestTemplateExchange(url: String, method: HttpMethod) {
        restTemplate.exchange(
            url,
            method,
            null,
            String::class.java
        )
    }
}
