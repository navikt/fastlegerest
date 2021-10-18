package no.nav.syfo.rest.ressurser;

import io.swagger.annotations.Api;
import no.nav.security.token.support.core.api.ProtectedWithClaims;
import no.nav.syfo.consumer.tilgangskontroll.Tilgang;
import no.nav.syfo.consumer.tilgangskontroll.TilgangkontrollConsumer;
import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.metric.Metrikk;
import no.nav.syfo.services.FastlegeService;
import no.nav.syfo.services.exceptions.FastlegeIkkeFunnet;
import no.nav.syfo.services.exceptions.HarIkkeTilgang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.syfo.api.auth.OIDCIssuer.VEILEDER_AZURE_V2;
import static no.nav.syfo.util.RequestUtilKt.NAV_PERSONIDENT_HEADER;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(value = "fastlege", description = "Endepunkt for henting av fastlege")
@ProtectedWithClaims(issuer = VEILEDER_AZURE_V2)
@RequestMapping(value = "/api/v2/fastlege")
public class FastlegeAzureAD2Ressurs {

    private final Logger log = LoggerFactory.getLogger(FastlegeAzureAD2Ressurs.class);

    private final FastlegeService fastlegeService;
    private final Metrikk metrikk;
    private final TilgangkontrollConsumer tilgangkontrollConsumer;

    @Inject
    public FastlegeAzureAD2Ressurs(
            FastlegeService fastlegeService,
            Metrikk metrikk,
            TilgangkontrollConsumer tilgangkontrollConsumer
    ) {
        this.fastlegeService = fastlegeService;
        this.metrikk = metrikk;
        this.tilgangkontrollConsumer = tilgangkontrollConsumer;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Fastlege finnFastlegeAazure(
            @RequestHeader MultiValueMap<String, String> headers,
            @RequestParam(value = "fnr", required = false) String fnr
    ) {
        metrikk.tellHendelse("finn_fastlege");

        String requestedPersonIdent = getRequestPersonIdent(headers, fnr);

        kastExceptionHvisIkkeTilgang(requestedPersonIdent);

        return fastlegeService.hentBrukersFastlege(requestedPersonIdent).orElseThrow(FastlegeIkkeFunnet::new);
    }

    @GetMapping(path = "/fastleger", produces = APPLICATION_JSON_VALUE)
    public List<Fastlege> getFastleger(
            @RequestHeader MultiValueMap<String, String> headers,
            @RequestParam(value = "fnr", required = false) String fnr
    ) {
        metrikk.tellHendelse("get_fastleger");

        String requestedPersonIdent = getRequestPersonIdent(headers, fnr);

        kastExceptionHvisIkkeTilgang(requestedPersonIdent);

        try {
            return fastlegeService.hentBrukersFastleger(requestedPersonIdent);
        } catch (FastlegeIkkeFunnet e) {
            return emptyList();
        }
    }

    private String getRequestPersonIdent(
            MultiValueMap<String, String> headers,
            String fnr
    ) {
        String requestedPersonIdent;
        if (fnr == null) {
            requestedPersonIdent = headers.getFirst(NAV_PERSONIDENT_HEADER);
        } else {
            requestedPersonIdent = fnr;
        }
        if (requestedPersonIdent == null) {
            throw new IllegalArgumentException("Did not find a PersonIdent in request headers or in Request param");
        } else {
            return requestedPersonIdent;
        }
    }

    private void kastExceptionHvisIkkeTilgang(String fnr) {
        Tilgang tilgang = tilgangkontrollConsumer.accessAzureAdV2(fnr);
        if (!tilgang.getHarTilgang()) {
            log.info("Har ikke tilgang til å se fastlegeinformasjon om brukeren");
            throw new HarIkkeTilgang(tilgang.getBegrunnelse());
        }
    }
}
