package no.nav.syfo.rest.ressurser;


import io.swagger.annotations.Api;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.syfo.services.TilgangService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(value = "tilgang", description = "Endepunkt for sjekking av tilgang til fastlegeoppslag")
@RestController
public class TilgangRessurs {

    private TilgangService tilgangService;

    @Inject
    public TilgangRessurs(final TilgangService tilgangService){
        this.tilgangService = tilgangService;
    }

    @GetMapping(path = "/api/tilgang", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    @ProtectedWithClaims(issuer = "intern")
    public boolean harTilgang() {
        return tilgangService.harTilgangTilTjenesten();
    }
}
