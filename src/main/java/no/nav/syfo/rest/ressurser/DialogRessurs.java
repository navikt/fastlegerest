package no.nav.syfo.rest.ressurser;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import no.nav.syfo.domain.oppfolgingsplan.RSOppfolgingsplan;
import no.nav.syfo.metric.Metrikk;
import no.nav.syfo.services.DialogService;
import no.nav.syfo.services.exceptions.FastlegeIkkeFunnet;
import no.nav.syfo.services.exceptions.PartnerinformasjonIkkeFunnet;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static no.nav.syfo.api.auth.OIDCIssuer.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(value = "dialogmelding", description = "Endepunkt for sending av dialogmelding")
@Slf4j
public class DialogRessurs {

    private final DialogService dialogService;
    private final Metrikk metrikk;

    public DialogRessurs(
            final DialogService dialogService,
            final Metrikk metrikk) {
        this.dialogService = dialogService;
        this.metrikk = metrikk;
    }

    @PostMapping(path = "/api/dialogmelding/v1/sendOppfolgingsplanFraSelvbetjening", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ProtectedWithClaims(issuer = EKSTERN)
    public void sendOppfolgingsplanFraSBS(@RequestBody @Valid RSOppfolgingsplan oppfolgingsplan) {
        sendPlan(oppfolgingsplan);
    }

    @PostMapping(path = "/api/dialogmelding/v2/oppfolgingsplan/lps", consumes = APPLICATION_JSON_VALUE)
    @ProtectedWithClaims(issuer = VEILEDER_AZURE_V2)
    public void mottaOppfolgingsplanLPSAzureAd(
            @RequestBody @Valid RSOppfolgingsplan oppfolgingsplan
    ) {
        sendPlan(oppfolgingsplan);
    }

    private void sendPlan(RSOppfolgingsplan oppfolgingsplan) {
        try {
            metrikk.countEvent("send_oppfolgingsplan");
            log.info("Sender oppfølgingsplan");
            dialogService.sendOppfolgingsplan(oppfolgingsplan);
        } catch (FastlegeIkkeFunnet e) {
            metrikk.countEvent("send_oppfolgingsplan_fastlegeikkefunnet");
            log.warn("Feil ved sending av oppfølgingsplan, FastlegeIkkeFunnet", e);
            throw e;
        } catch (PartnerinformasjonIkkeFunnet e) {
            metrikk.countEvent("send_oppfolgingsplan_partnerinformasjonikkefunnet");
            log.warn("Feil ved sending av oppfølgingsplan, PartnerinformasjonIkkeFunnet", e);
            throw e;
        } catch (Exception e) {
            metrikk.countEvent("send_oppfolgingsplan_excption");
            log.error("Feil ved sending av oppfølgingsplan, Ukjent Feil", e);
            throw e;
        }
    }
}
