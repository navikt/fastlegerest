package no.nav.syfo.rest.ressurser;


import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.syfo.services.TilgangService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import static no.nav.syfo.OIDCIssuer.AZURE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = "/api/internad/tilgang")
public class TilgangADRessurs {

    private final TilgangService tilgangService;

    @Inject
    public TilgangADRessurs(TilgangService tilgangService) {
        this.tilgangService = tilgangService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public boolean isGrantedAccess() {
        return tilgangService.isVeilederGrantedAccessToSYFOWithAD();
    }
}
