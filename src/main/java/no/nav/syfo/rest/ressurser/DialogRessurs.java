package no.nav.syfo.rest.ressurser;


import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.syfo.domain.oppfolgingsplan.RSOppfolgingsplan;
import no.nav.syfo.services.DialogService;
import no.nav.syfo.services.exceptions.PartnerinformasjonIkkeFunnet;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(value = "dialogmelding", description = "Endepunkt for sending av dialogmelding")
@Slf4j
public class DialogRessurs {

    private final DialogService dialogService;

    public DialogRessurs(final DialogService dialogService) {
        this.dialogService = dialogService;
    }

    @ResponseBody
    @PostMapping(path = "/dialogmelding/v1/sendOppfolgingsplan", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ProtectedWithClaims(issuer = "intern")
    public void sendOppfolgingsplan(@RequestBody @Valid RSOppfolgingsplan oppfolgingsplan) {
        try {
            log.info("Sender oppfølgingsplan");
            dialogService.sendOppfolgingsplan(oppfolgingsplan);
        } catch (PartnerinformasjonIkkeFunnet e) {
            log.warn("Feil ved sending av oppfølgingsplan", e);
            throw e;
        } catch (Exception e) {
            log.error("Feil ved sending av oppfølgingsplan", e);
            throw e;
        }
    }
}
