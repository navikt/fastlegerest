package no.nav.syfo.rest.ressurser;


import io.swagger.annotations.Api;
import no.nav.syfo.domain.oppfolgingsplan.RSOppfolgingsplan;
import no.nav.syfo.services.DialogService;
import no.nav.syfo.services.exceptions.PartnerinformasjonIkkeFunnet;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

@Path("/dialogmelding/v1")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(value = "dialogmelding", description = "Endepunkt for sending av dialogmelding")
@Controller
public class DialogRessurs {
    private static final Logger LOG = getLogger(DialogRessurs.class);

    private final DialogService dialogService;

    public DialogRessurs(DialogService dialogService) {
        this.dialogService = dialogService;
    }

    @POST
    @Path("/sendOppfolgingsplan")
    public void sendOppfolgingsplan(RSOppfolgingsplan oppfolgingsplan) {
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
