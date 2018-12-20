package no.nav.syfo.rest.ressurser;

import io.swagger.annotations.Api;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.services.FastlegeService;
import no.nav.syfo.services.TilgangService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(value = "fastlege", description = "Endepunkt for henting av fastlege")
public class FastlegeRessurs {

    private FastlegeService fastlegeService;
    private TilgangService tilgangService;

    @Inject
    public FastlegeRessurs(final FastlegeService fastlegeService, final TilgangService tilgangService) {
        this.fastlegeService = fastlegeService;
        this.tilgangService = tilgangService;
    }

    @GetMapping(path = "/fastlege/v1", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    @ProtectedWithClaims(issuer = "intern")
    public Fastlege finnFastlege(@QueryParam("fnr") String fnr) {
        if (tilgangService.harIkkeTilgang(fnr)) {
            throw new ForbiddenException("Ikke tilgang");
        }

        return fastlegeService.hentBrukersFastlege(fnr).orElseThrow(() -> new NotFoundException("Fant ikke aktiv fastlege"));
    }

    @GetMapping(path = "/fastleger", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    @ProtectedWithClaims(issuer = "intern")
    public List<Fastlege> finnFastleger(@QueryParam("fnr") String fnr) {
        if (tilgangService.harIkkeTilgang(fnr)) {
            throw new ForbiddenException("Ikke tilgang");
        }

        return fastlegeService.hentBrukersFastleger(fnr);
    }

    @ExceptionHandler({NotFoundException.class})
    void handleBadRequests(HttpServletResponse response, NotFoundException exception) throws IOException {
        response.sendError(BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler({ForbiddenException.class})
    void handleForbiddenRequests(HttpServletResponse response, ForbiddenException exception) throws IOException {
        response.sendError(FORBIDDEN.value(), exception.getMessage());
    }

}
