package no.nav.syfo.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer;
import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.domain.Partnerinformasjon;
import no.nav.syfo.domain.dialogmelding.RSHodemelding;
import no.nav.syfo.domain.oppfolgingsplan.RSOppfolgingsplan;
import no.nav.syfo.metric.Metrikk;
import no.nav.syfo.services.exceptions.FastlegeIkkeFunnet;
import no.nav.syfo.services.exceptions.InnsendingFeiletException;
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

    private final AzureAdV2TokenConsumer azureAdV2TokenConsumer;
    private final FastlegeService fastlegeService;
    private final Metrikk metrikk;
    private final PartnerService partnerService;
    private final RestTemplate restTemplate;
    private final String isdialogmeldingIdentifier;
    private final String isdialogmeldingUrl;

    @Inject
    public DialogService(
            final AzureAdV2TokenConsumer azureAdV2TokenConsumer,
            final FastlegeService fastlegeService,
            final Metrikk metrikk,
            final PartnerService partnerService,
            final @Qualifier("default") RestTemplate restTemplate,
            final @Value("${isdialogmelding.identifier}") String isdialogmeldingIdentifier,
            final @Value("${isdialogmelding.url}") String isdialogmeldingUrl) {
        this.azureAdV2TokenConsumer = azureAdV2TokenConsumer;
        this.fastlegeService = fastlegeService;
        this.metrikk = metrikk;
        this.partnerService = partnerService;
        this.restTemplate = restTemplate;
        this.isdialogmeldingIdentifier = isdialogmeldingIdentifier;
        this.isdialogmeldingUrl = isdialogmeldingUrl;
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
                isdialogmeldingUrl + "/api/v1/send/oppfolgingsplan",
                HttpMethod.POST,
                new HttpEntity<>(hodemelding, lagHeaders()),
                String.class
        );
        HttpStatus statusCode = response.getStatusCode();
        if (statusCode.is3xxRedirection() || statusCode.isError()) {
            metrikk.countEvent("send_oppfolgingsplan_isdialogmelding_fail");
            log.error("Feil ved sending av oppfølgingsdialog til fastlege: Fikk responskode {}", statusCode.value());
            throw new InnsendingFeiletException("Feil ved sending av oppfølgingsdialog til fastlege: Fikk responskode " + statusCode.value());
        }
        metrikk.countEvent("send_oppfolgingsplan_isdialogmelding_success");
    }

    private HttpHeaders lagHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(org.springframework.http.MediaType.APPLICATION_JSON));
        headers.setBearerAuth(azureAdV2TokenConsumer.getSystemToken(isdialogmeldingIdentifier));
        return headers;
    }
}
