package no.nav.syfo.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.OIDCIssuer;
import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.domain.Partnerinformasjon;
import no.nav.syfo.domain.dialogmelding.RSHodemelding;
import no.nav.syfo.domain.oppfolgingsplan.RSOppfolgingsplan;
import no.nav.syfo.services.exceptions.FastlegeIkkeFunnet;
import no.nav.syfo.util.OIDCUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.Collections;

@Service
@Slf4j
public class DialogService {

    private FastlegeService fastlegeService;
    private PartnerService partnerService;
    private OIDCRequestContextHolder contextHolder;
    private RestTemplate restTemplate;
    private String HOST;

    @Inject
    public DialogService (
            final FastlegeService fastlegeService,
            final PartnerService partnerService,
            final OIDCRequestContextHolder contextHolder,
            final RestTemplate restTemplate,
            @Value("${fasit.environment.name}") String host ) {
        this.fastlegeService = fastlegeService;
        this.partnerService = partnerService;
        this.contextHolder = contextHolder;
        this.restTemplate = restTemplate;
        this.HOST = "local".equalsIgnoreCase(host) ? "localhost:8080" : "dialogfordeler";
    }

    public void sendOppfolgingsplan(final RSOppfolgingsplan oppfolgingsplan) {
        Fastlege fastlege = fastlegeService.hentBrukersFastlege(oppfolgingsplan.getSykmeldtFnr())
                .orElseThrow(() -> new FastlegeIkkeFunnet("Fant ikke aktiv fastlege"));

        Partnerinformasjon partnerinformasjon = partnerService.getPartnerinformasjon(fastlege);

        RSHodemelding hodemelding = new RSHodemelding(fastlege, partnerinformasjon, oppfolgingsplan);

        send(hodemelding);
    }


    private void send(RSHodemelding hodemelding) {
        ResponseEntity<String> response = restTemplate.exchange(
                "http://" + HOST + "/api/dialogmelding/sendOppfolgingsplan",
                HttpMethod.POST,
                new HttpEntity<>(hodemelding, lagHeaders()),
                String.class
        );
        HttpStatus statusCode = response.getStatusCode();
        if (statusCode.is3xxRedirection() || statusCode.isError()){
            log.error("Feil ved sending av oppfølgingsdialog til fastlege: Fikk responskode {}", statusCode.value());
            throw new RuntimeException("Feil ved sending av oppfølgingsdialog til fastlege: Fikk responskode " + statusCode.value());
        }
    }

    private HttpHeaders lagHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(org.springframework.http.MediaType.APPLICATION_JSON));
        OIDCUtil oidutil = new OIDCUtil();
        headers.set("Authorization", "Bearer" + oidutil.tokenFraOIDC(contextHolder, OIDCIssuer.INTERN)); //TODO: er dette system token?
        return headers;

    }
}
