package no.nav.syfo.rest.ressurser;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.services.FastlegeService;
import no.nav.syfo.services.TilgangService;
import no.nav.syfo.services.exceptions.FastlegeIkkeFunnet;
import no.nav.syfo.services.exceptions.HarIkkeTilgang;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(value = "fastlege", description = "Endepunkt for henting av fastlege")
@Slf4j
public class FastlegeRessurs {

    private FastlegeService fastlegeService;
    private TilgangService tilgangService;

    @Inject
    public FastlegeRessurs(final FastlegeService fastlegeService, final TilgangService tilgangService) {
        this.fastlegeService = fastlegeService;
        this.tilgangService = tilgangService;
    }

    @GetMapping(path = "/api/fastlege/v1", produces = APPLICATION_JSON_VALUE)
    @ProtectedWithClaims(issuer = "intern")
    public Fastlege finnFastlege(@RequestParam(value = "fnr", required = true) String fnr) {
        if (tilgangService.harIkkeTilgang(fnr)) {
            log.warn("fnr {} har ikke tilgang", fnr);
            throw new HarIkkeTilgang("Ikke tilgang");
        }

        return fastlegeService.hentBrukersFastlege(fnr).orElseThrow(() -> new FastlegeIkkeFunnet("Fant ikke aktiv fastlege"));
    }

    @GetMapping(path = "/api/fastlege/v1/fastleger", produces = APPLICATION_JSON_VALUE)
    @ProtectedWithClaims(issuer = "intern")
    public List<Fastlege> finnFastleger(@RequestParam(value = "fnr", required = true) String fnr) {
        if (tilgangService.harIkkeTilgang(fnr)) {
            log.warn("fnr {} har ikke tilgang", fnr);
            throw new HarIkkeTilgang("Ikke tilgang");
        }

        return fastlegeService.hentBrukersFastleger(fnr);
    }

    @ExceptionHandler({FastlegeIkkeFunnet.class})
    void handleFastlegeIkkeFunnet(HttpServletResponse response, FastlegeIkkeFunnet fastlegeIkkeFunnet) throws IOException {
        response.sendError(NOT_FOUND.value(), fastlegeIkkeFunnet.getMessage());
    }

    @ExceptionHandler({RuntimeException.class})
    void handleBadRequests(HttpServletResponse response, RuntimeException exception) throws IOException {
        response.sendError(BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler({HarIkkeTilgang.class})
    void handleHarIkkeTilgang(HttpServletResponse response, HarIkkeTilgang harIkkeTilgang) throws IOException {
        response.sendError(FORBIDDEN.value(), harIkkeTilgang.getMessage());
    }

}
