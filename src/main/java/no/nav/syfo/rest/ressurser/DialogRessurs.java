package no.nav.syfo.rest.ressurser;


import io.swagger.annotations.Api;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.syfo.domain.oppfolgingsplan.RSOppfolgingsplan;
import no.nav.syfo.services.DialogService;
import no.nav.syfo.services.exceptions.PartnerinformasjonIkkeFunnet;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(value = "dialogmelding", description = "Endepunkt for sending av dialogmelding")
public class DialogRessurs {
    private static final Logger LOG = getLogger(DialogRessurs.class);

    private final DialogService dialogService;

    public DialogRessurs(final DialogService dialogService) {
        this.dialogService = dialogService;
    }

    @ResponseBody
    @PostMapping(path = "/dialogmelding/v1/sendOppfolgingsplan", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ProtectedWithClaims(issuer = "intern")
    public void sendOppfolgingsplan(@RequestBody @Valid RSOppfolgingsplan oppfolgingsplan) {
        try {
            dialogService.sendOppfolgingsplan(oppfolgingsplan);
        } catch (PartnerinformasjonIkkeFunnet e) {
            LOG.warn("Feil ved sending av oppfølgingsplan", e);
            throw e;
        } catch (Exception e) {
            LOG.error("Feil ved sending av oppfølgingsplan", e);
            throw e;
        }
    }
}
