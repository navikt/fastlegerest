package no.nav.syfo.rest.ressurser;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import no.nav.metrics.aspects.Count;
import no.nav.metrics.aspects.Timed;
import no.nav.syfo.domain.*;
import no.nav.syfo.services.FastlegeService;
import no.nav.syfo.services.LdapService;
import no.nav.syfo.services.TilgangService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.util.List;

import static java.lang.System.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static no.nav.metrics.MetricsFactory.createEvent;
import static org.springframework.util.StringUtils.*;

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
        Tilgang tilgang = tilgangService.sjekkTilgang(fnr);

        if (!tilgang.harTilgang) {
            return Response
                    .status(403)
                    .entity(tilgang)
                    .type(APPLICATION_JSON)
                    .build();
        }
        return Response.ok(fastlegeService.hentBrukersFastlege(fnr)).build();
    }

    @GET
    @Timed(name = "finnFastleger")
    @Count(name = "finnFastleger")
    @Path("/fastleger")
    public Response finnFastleger(@QueryParam("fnr") String fnr) {
        Tilgang tilgang = tilgangService.sjekkTilgang(fnr);

        if (!tilgang.harTilgang) {
            return Response
                    .status(403)
                    .entity(tilgang)
                    .type(APPLICATION_JSON)
                    .build();
        }
        return Response.ok(fastlegeService.hentBrukersFastleger(fnr));
    }

    @GET
    @Path("/ping")
    public Response ping() {
        return Response.ok().build();
    }
}
