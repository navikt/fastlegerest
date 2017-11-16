package no.nav.syfo.rest.ressurser;

import io.swagger.annotations.Api;
import no.nav.metrics.aspects.Count;
import no.nav.metrics.aspects.Timed;
import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.services.FastlegeService;
import no.nav.syfo.services.LdapService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;

@Path("/fastlege/v1")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(value = "fastlege", description = "Endepunkt for henting av fastlege")
@Controller
public class FastlegeRessurs {

    @Inject
    private FastlegeService fastlegeService;
    @Inject
    private LdapService ldapService;

    @GET
    @Timed(name = "finnFastlege")
    @Count(name = "finnFastlege")
    public Fastlege finnFastlege(@QueryParam("fnr") String fnr) {
        boolean harTilgang = ldapService.harTilgang(getSubjectHandler().getUid(), "0000-GA-SYFO-SENSITIV");

        if (!harTilgang) {
            throw new ForbiddenException();
        }
        return fastlegeService.hentBrukersFastlege(fnr);
    }

    @GET
    @Timed(name = "finnFastleger")
    @Count(name = "finnFastleger")
    @Path("/fastleger")
    public List<Fastlege> finnFastleger(@QueryParam("fnr") String fnr) {
        boolean harTilgang = ldapService.harTilgang(getSubjectHandler().getUid(), "0000-GA-SYFO-SENSITIV");

        if (!harTilgang) {
            throw new ForbiddenException();
        }
        return fastlegeService.hentBrukersFastleger(fnr);
    }

    @GET
    @Path("/ping")
    public Response ping() {
        return Response.ok().build();
    }
}
