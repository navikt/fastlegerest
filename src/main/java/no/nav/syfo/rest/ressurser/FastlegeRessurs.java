package no.nav.syfo.rest.ressurser;

import io.swagger.annotations.Api;
import no.nav.metrics.aspects.Count;
import no.nav.metrics.aspects.Timed;
import no.nav.syfo.services.FastlegeService;
import no.nav.syfo.services.TilgangService;
import no.nav.syfo.services.exceptions.FastlegeIkkeFunnet;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

@Path("/fastlege/v1")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(value = "fastlege", description = "Endepunkt for henting av fastlege")
@Controller
public class FastlegeRessurs {

    @Inject
    private FastlegeService fastlegeService;
    @Inject
    private TilgangService tilgangService;

    @GET
    @Timed(name = "finnFastlege")
    @Count(name = "finnFastlege")
    public Response finnFastlege(@QueryParam("fnr") String fnr) {
        if (tilgangService.sjekkTilgang(fnr)) {
            return ok(fastlegeService.hentBrukersFastlege(fnr)
                    .orElseThrow(() -> new NotFoundException("Fant ikke aktiv fastlege"))).build();
        } else {
            return status(403).build();
        }
    }

    @GET
    @Timed(name = "finnFastleger")
    @Count(name = "finnFastleger")
    @Path("/fastleger")
    public Response finnFastleger(@QueryParam("fnr") String fnr) {
        if (tilgangService.sjekkTilgang(fnr)) {
            try {
                return ok(fastlegeService.hentBrukersFastleger(fnr)).build();
            } catch (FastlegeIkkeFunnet e) {
                throw new NotFoundException();
            }
        } else {
            return status(403).build();
        }
    }

    @GET
    @Path("/ping")
    public Response ping() {
        return ok().build();
    }
}
