package no.nav.syfo.rest.ressurser;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.domain.Tilgang;
import no.nav.syfo.metric.Metrikk;
import no.nav.syfo.services.FastlegeService;
import no.nav.syfo.services.TilgangService;
import no.nav.syfo.services.exceptions.FastlegeIkkeFunnet;
import no.nav.syfo.services.exceptions.HarIkkeTilgang;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.syfo.OIDCIssuer.INTERN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(value = "fastlege", description = "Endepunkt for henting av fastlege")
@Slf4j
public class FastlegeRessurs {

    private final FastlegeService fastlegeService;
    private final Metrikk metrikk;
    private final TilgangService tilgangService;

    @Inject
    public FastlegeRessurs(
            FastlegeService fastlegeService,
            Metrikk metrikk,
            TilgangService tilgangService
    ) {
        this.fastlegeService = fastlegeService;
        this.metrikk = metrikk;
        this.tilgangService = tilgangService;
    }

    @GetMapping(path = "/api/fastlege/v1", produces = APPLICATION_JSON_VALUE)
    @ProtectedWithClaims(issuer = INTERN)
    public Fastlege finnFastlege(@RequestParam(value = "fnr") String fnr) {
        metrikk.tellHendelse("finn_fastlege");

        kastExceptionHvisIkkeTilgang(fnr);

        return fastlegeService.hentBrukersFastlege(fnr).orElseThrow(FastlegeIkkeFunnet::new);
    }

    @GetMapping(path = "/api/fastlege/v1/fastleger", produces = APPLICATION_JSON_VALUE)
    @ProtectedWithClaims(issuer = INTERN)
    public List<Fastlege> finnFastleger(@RequestParam(value = "fnr") String fnr) {
        metrikk.tellHendelse("finn_fastleger");

        kastExceptionHvisIkkeTilgang(fnr);

        try {
            return fastlegeService.hentBrukersFastleger(fnr);
        } catch (FastlegeIkkeFunnet e) {
            return emptyList();
        }
    }

    private void kastExceptionHvisIkkeTilgang(String fnr) {
        Tilgang tilgang = tilgangService.sjekkTilgang(fnr);
        if (!tilgang.harTilgang) {
            log.info("Har ikke tilgang til å se fastlegeinformasjon om brukeren");
            throw new HarIkkeTilgang(tilgang.begrunnelse);
        }
    }
}
