package no.nav.syfo.rest.ressurser;

import io.swagger.annotations.Api;
import no.nav.metrics.aspects.Count;
import no.nav.metrics.aspects.Timed;
import no.nav.syfo.domain.*;
import no.nav.syfo.services.FastlegeService;
import no.nav.syfo.services.TilgangService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

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
        Response tilgangResponse = tilgangService.sjekkTilgang(fnr);

        if (200 == tilgangResponse.getStatus()) {
            return Response.ok(fastlegeService.hentBrukersFastlege(fnr)).build();
        } else {
            return tilgangResponse;
        }
    }

    @GET
    @Timed(name = "finnFastleger")
    @Count(name = "finnFastleger")
    @Path("/fastleger")
    public Response finnFastleger(@QueryParam("fnr") String fnr) {
        Response tilgangResponse = tilgangService.sjekkTilgang(fnr);

        if (200 == tilgangResponse.getStatus()) {
            return Response.ok(fastlegeService.hentBrukersFastleger(fnr)).build();
        } else {
            return tilgangResponse;
        }
    }

    @GET
    @Path("/ping")
    public Response ping() {
        return Response.ok().build();
    }
}
