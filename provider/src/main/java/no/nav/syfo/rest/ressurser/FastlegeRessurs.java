package no.nav.syfo.rest.ressurser;

import io.swagger.annotations.Api;
import no.nav.metrics.aspects.Count;
import no.nav.metrics.aspects.Timed;
import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.services.FastlegeService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;

@Path("/fastlege")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(value = "fastlege", description = "Endepunkt for henting av fastlege")
public class FastlegeRessurs {

    @Inject
    private FastlegeService fastlegeService;

    @GET
    @Timed(name = "finnFastlege")
    @Count(name = "finnFastlege")
    public Fastlege finnFastlege(@QueryParam("fnr") String fnr) {
        return fastlegeService.hentBrukersFastlege(fnr);
    }
}
