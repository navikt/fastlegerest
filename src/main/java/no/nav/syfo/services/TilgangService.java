package no.nav.syfo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.Collections;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
public class TilgangService {

    private final String TILGANGSKONTROLLAPI_URL;
    private final boolean HAR_LOCAL_MOCK;
    private RestTemplate restTemplate;


    @Inject
    public TilgangService(
            @Value("${tilgangskontrollapi.url}") String url,
            @Value("${local_mock}") boolean erLokalMock,
            RestTemplate restTemplate
    ){
        this.TILGANGSKONTROLLAPI_URL = url;
        this.HAR_LOCAL_MOCK = erLokalMock;
        this.restTemplate = restTemplate;
    }


    @Cacheable(value = "tilgang", keyGenerator = "userkeygenerator")
    public boolean sjekkTilgang(String fnr) {
        if (HAR_LOCAL_MOCK == true) {
            return true;
        }

        final String url = fromHttpUrl(TILGANGSKONTROLLAPI_URL + "/tilgangtilbruker")
                .queryParam("fnr", fnr)
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                lagRequest(),
                String.class
                );

        return response.getStatusCode().is2xxSuccessful();
    }

    public boolean harIkkeTilgang(String fnr) {
        return !sjekkTilgang(fnr);
    }

    @Cacheable(value = "tilgang", keyGenerator = "userkeygenerator")
    public boolean harTilgangTilTjenesten() {
        if (HAR_LOCAL_MOCK == true) {
            return true;
        }

        ResponseEntity<String> response = restTemplate.exchange(
                TILGANGSKONTROLLAPI_URL + "/tilgangtilbruker",
                HttpMethod.GET,
                lagRequest(),
                String.class
        );

        return response.getStatusCode().is2xxSuccessful();
    }

    private HttpEntity<String> lagRequest(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }

}
