package no.nav.syfo.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.OIDCIssuer;
import no.nav.syfo.domain.Tilgang;
import no.nav.syfo.util.OIDCUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.Collections;

import static no.nav.syfo.mappers.TilgangMappers.rs2Tilgang;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
@Slf4j
public class TilgangService {

    private final String TILGANGSKONTROLLAPI_URL;
    private final boolean HAR_LOKAL_MOCK;
    private RestTemplate restTemplate;
    private OIDCRequestContextHolder contextHolder;

    private static final String TILGANG_TIL_BRUKER_PATH = "/tilgangtilbruker";
    private static final String TILGANG_TIL_BRUKER_VIA_AZURE_PATH = "/bruker";


    @Inject
    public TilgangService(
            final @Value("${tilgangskontrollapi.url}") String url,
            final @Value("${local_mock}") boolean erLokalMock,
            final @Qualifier("Oidc") RestTemplate restTemplate,
            final OIDCRequestContextHolder contextHolder
    ) {
        this.TILGANGSKONTROLLAPI_URL = url;
        this.HAR_LOKAL_MOCK = erLokalMock;
        this.restTemplate = restTemplate;
        this.contextHolder = contextHolder;
    }


    @Cacheable(value = "tilgang")
    public Tilgang sjekkTilgang(String fnr, boolean callWithInterAzureAD) {
        if (HAR_LOKAL_MOCK) {
            return new Tilgang()
                    .harTilgang(true)
                    .begrunnelse("");
        }
        final String url = fromHttpUrl(getTilgangTilBrukerUrl(callWithInterAzureAD))
                .queryParam("fnr", fnr)
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                lagRequest(callWithInterAzureAD ? OIDCIssuer.AZURE : OIDCIssuer.INTERN),
                String.class
        );

        log.info("Fikk responskode: {} fra syfo-tilgangskontroll, med body: {}", response.getStatusCode(), response.getBody());
        return rs2Tilgang(response);
    }

    @Cacheable(value = "tilgang")
    public boolean harTilgangTilTjenesten() {
        if (HAR_LOKAL_MOCK) {
            return true;
        }

        ResponseEntity<String> response = restTemplate.exchange(
                TILGANGSKONTROLLAPI_URL + "/tilgangtiltjenesten",
                HttpMethod.GET,
                lagRequest(OIDCIssuer.INTERN),
                String.class
        );

        return response.getStatusCode().is2xxSuccessful();
    }

    public boolean isVeilederGrantedAccessToSYFOWithAD() {
        ResponseEntity<String> response = restTemplate.exchange(
                TILGANGSKONTROLLAPI_URL + "/syfo",
                HttpMethod.GET,
                lagRequest(OIDCIssuer.AZURE),
                String.class
        );
        return response.getStatusCode().is2xxSuccessful();
    }

    private HttpEntity<String> lagRequest(String issuer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + OIDCUtil.tokenFraOIDC(contextHolder, issuer));
        return new HttpEntity<>(headers);
    }

    private String getTilgangTilBrukerUrl(boolean callWithInterAzureAd) {
        String endUrl = callWithInterAzureAd
                ? TILGANG_TIL_BRUKER_VIA_AZURE_PATH
                : TILGANG_TIL_BRUKER_PATH;
        return TILGANGSKONTROLLAPI_URL + endUrl;
    }
}
