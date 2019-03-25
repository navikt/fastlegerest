package no.nav.syfo.services;


import lombok.extern.slf4j.Slf4j;
import no.nav.syfo.domain.Token;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
public class TokenService {

    private RestTemplate basicAuthRestTemplate;
    private String url;
    private final boolean HAR_LOKAL_MOCK;

    @Autowired
    public TokenService(@Qualifier("BasicAuth") RestTemplate basicAuthRestTemplate,
                        @Value("${security-token-service-token.url}") String url,
                        final @Value("${local_mock}") boolean erLokalMock) {
        this.basicAuthRestTemplate = basicAuthRestTemplate;
        this.url = url;
        this.HAR_LOKAL_MOCK = erLokalMock;
    }

    String getToken() {
        if(HAR_LOKAL_MOCK){
            return "token";
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final String uriString = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("grant_type", "client_credentials")
                .queryParam("scope", "openid")
                .toUriString();

        final ResponseEntity<Token> result = basicAuthRestTemplate.exchange(uriString, GET, new HttpEntity<>(headers), Token.class);

        if (result.getStatusCode() != OK) {
            throw new RuntimeException("Henting av token feiler med HTTP-" + result.getStatusCode());
        }

        return requireNonNull(result.getBody()).getAccess_token();
    }
}
