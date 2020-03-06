package no.nav.syfo.exception;

import no.nav.syfo.LocalApplication;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.client.ExpectedCount.once;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplication.class)
@DirtiesContext
public class RestTemplateErrorHandlerTest {
    @Inject
    @Qualifier("Oidc")
    private RestTemplate restTemplate;

    private MockRestServiceServer mockRestServiceServer;

    @Before
    public void setUp() {
        this.mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @After
    public void tearDown() {
        mockRestServiceServer.reset();
    }

    @Test
    public void no_exception_if_OK_from_server() {
        String url = "http://test.no";
        HttpMethod method = GET;
        mockRestRequest(url, method, OK);

        doRestTemplateExchange(url, method);
    }

    @Test
    public void server_error_from_resttemplate_gives_runtimeexception() {
        String url = "http://test.no";
        HttpMethod method = GET;
        mockRestRequest(url, method, INTERNAL_SERVER_ERROR);

        try {
            doRestTemplateExchange(url, method);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Fikk en server-error ved " + method + "-kall til " + url);
        } catch (Exception e) {
            fail("Got the wrong type of exception");
        }
    }

    @Test
    public void client_error_from_resttemplate_thats_not_forbidden_gives_runtimeexception() {
        String url = "http://test.no";
        HttpMethod method = GET;
        mockRestRequest(url, method, NOT_FOUND);

        try {
            doRestTemplateExchange(url, method);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Noe gikk galt ved " + method + "-kall til " + url);
        } catch (Exception e) {
            fail("Got the wrong type of exception");
        }
    }

    @Test
    public void client_error_from_resttemplate_thats_forbidden_does_not_give_exception() {
        String url = "http://test.no";
        HttpMethod method = GET;
        mockRestRequest(url, method, FORBIDDEN);

        doRestTemplateExchange(url, method);
    }

    @Test
    public void error_from_restTemplateErrorHandler_gives_url_without_fnr_in_thrown_exception() {
        String url = "http://test.no/?fnr=12345678901";
        String urlWithHiddenFnr = "http://test.no/?fnr=***********";
        HttpMethod method = GET;
        mockRestRequest(url, method, INTERNAL_SERVER_ERROR);

        try {
            doRestTemplateExchange(url, method);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Fikk en server-error ved " + method + "-kall til " + urlWithHiddenFnr);
        } catch (Exception e) {
            fail("Got the wrong type of exception");
        }
    }

    @Test
    public void error_from_restTemplateErrorHandler_gives_correct_method_in_thrown_exception() {
        String url = "http://test.no";
        String urlWithHiddenFnr = "http://test.no";
        HttpMethod method = POST;
        mockRestRequest(url, method, INTERNAL_SERVER_ERROR);

        try {
            doRestTemplateExchange(url, method);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Fikk en server-error ved " + method + "-kall til " + urlWithHiddenFnr);
        } catch (Exception e) {
            fail("Got the wrong type of exception");
        }
    }

    private void mockRestRequest(String url, HttpMethod httpMethod, HttpStatus httpStatus) {
        mockRestServiceServer.expect(once(), MockRestRequestMatchers.requestTo(url))
                .andExpect(MockRestRequestMatchers.method(httpMethod))
                .andRespond(MockRestResponseCreators.withStatus(httpStatus));
    }

    private void doRestTemplateExchange(String url, HttpMethod method) {
        restTemplate.exchange(
                url,
                method,
                null,
                String.class
        );
    }
}
