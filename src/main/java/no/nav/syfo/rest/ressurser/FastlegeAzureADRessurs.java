package no.nav.syfo.rest.ressurser;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import no.nav.syfo.consumer.tilgangskontroll.Tilgang;
import no.nav.syfo.consumer.tilgangskontroll.TilgangkontrollConsumer;
import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.metric.Metrikk;
import no.nav.syfo.services.FastlegeService;
import no.nav.syfo.services.exceptions.FastlegeIkkeFunnet;
import no.nav.syfo.services.exceptions.HarIkkeTilgang;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.syfo.api.auth.OIDCIssuer.AZURE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(value = "fastlege", description = "Endepunkt for henting av fastlege")
@Slf4j
@ProtectedWithClaims(issuer = AZURE)
@RequestMapping(value = "/api/internad/fastlege/v1")
public class FastlegeAzureADRessurs {

    private final FastlegeService fastlegeService;
    private final Metrikk metrikk;
    private final TilgangkontrollConsumer tilgangkontrollConsumer;

    @Inject
    public FastlegeAzureADRessurs(
            FastlegeService fastlegeService,
            Metrikk metrikk,
            TilgangkontrollConsumer tilgangkontrollConsumer
    ) {
        this.fastlegeService = fastlegeService;
        this.metrikk = metrikk;
        this.tilgangkontrollConsumer = tilgangkontrollConsumer;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Fastlege finnFastlegeAazure(@RequestParam(value = "fnr") String fnr) {
        metrikk.tellHendelse("finn_fastlege");

        kastExceptionHvisIkkeTilgang(fnr);

        return fastlegeService.hentBrukersFastlege(fnr).orElseThrow(FastlegeIkkeFunnet::new);
    }

    @GetMapping(path = "/fastleger", produces = APPLICATION_JSON_VALUE)
    public List<Fastlege> getFastleger(@RequestParam(value = "fnr") String fnr) {
        metrikk.tellHendelse("get_fastleger");

        kastExceptionHvisIkkeTilgang(fnr);

        try {
            return fastlegeService.hentBrukersFastleger(fnr);
        } catch (FastlegeIkkeFunnet e) {
            return emptyList();
        }
    }

    private void kastExceptionHvisIkkeTilgang(String fnr) {
        Tilgang tilgang = tilgangkontrollConsumer.sjekkTilgang(fnr);
        if (!tilgang.getHarTilgang()) {
            log.info("Har ikke tilgang til å se fastlegeinformasjon om brukeren");
            throw new HarIkkeTilgang(tilgang.getBegrunnelse());
        }
    }
}
