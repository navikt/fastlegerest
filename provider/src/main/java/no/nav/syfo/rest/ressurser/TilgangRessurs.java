package no.nav.syfo.rest.ressurser;


import io.swagger.annotations.Api;
import no.nav.syfo.services.LdapService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;

@Path("/tilgang")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(value = "tilgang", description = "Endepunkt for sjekking av tilgang til fastlegeoppslag")
@Controller
public class TilgangRessurs {

    @Inject
    private LdapService ldapService;

    @GET
    public boolean harTilgang() {
        return ldapService.harTilgang(getSubjectHandler().getUid(), "0000-GA-SYFO-SENSITIV");
    }
}
