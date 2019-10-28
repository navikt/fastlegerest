package no.nav.syfo.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.domain.Partnerinformasjon;
import no.nav.syfo.domain.dialogmelding.RSHodemelding;
import no.nav.syfo.domain.oppfolgingsplan.RSOppfolgingsplan;
import no.nav.syfo.services.exceptions.FastlegeIkkeFunnet;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private TokenService tokenService;
    private RestTemplate restTemplate;
    private String dialogfordelerDomain;

    @Inject
    public DialogService (
            final FastlegeService fastlegeService,
            final PartnerService partnerService,
            final TokenService tokenService,
            final @Qualifier("Oidc") RestTemplate restTemplate,
            final @Value("${environment.name:nonlocal}") String environmenName ) {
        this.fastlegeService = fastlegeService;
        this.partnerService = partnerService;
        this.restTemplate = restTemplate;
        this.tokenService = tokenService;
        this.dialogfordelerDomain = "local".equalsIgnoreCase(environmenName) ? "localhost:8080" : "dialogfordeler";
    }

    public void sendOppfolgingsplan(final RSOppfolgingsplan oppfolgingsplan) {
        Fastlege fastlege = fastlegeService.hentBrukersFastlege(oppfolgingsplan.getSykmeldtFnr())
                .orElseThrow(FastlegeIkkeFunnet::new);

        Partnerinformasjon partnerinformasjon = partnerService.getPartnerinformasjon(fastlege);

        RSHodemelding hodemelding = new RSHodemelding(fastlege, partnerinformasjon, oppfolgingsplan);

        send(hodemelding);
    }


    private void send(RSHodemelding hodemelding) {
        ResponseEntity<String> response = restTemplate.exchange(
                "http://" + dialogfordelerDomain + "/api/dialogmelding/sendOppfolgingsplan",
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
        headers.set("Authorization", "Bearer " + tokenService.getToken());
        return headers;

    }

}
