package no.nav.syfo.fastlege.api

import io.swagger.annotations.Api
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.syfo.api.auth.OIDCIssuer.VEILEDER_AZURE_V2
import no.nav.syfo.consumer.tilgangskontroll.TilgangkontrollConsumer
import no.nav.syfo.fastlege.FastlegeService
import no.nav.syfo.fastlege.domain.Fastlege
import no.nav.syfo.fastlege.expection.FastlegeIkkeFunnet
import no.nav.syfo.fastlege.expection.HarIkkeTilgang
import no.nav.syfo.metric.Metrikk
import no.nav.syfo.util.PersonIdent
import no.nav.syfo.util.getPersonIdent
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@Api(value = "fastlege", description = "Endepunkt for henting av fastlege")
@ProtectedWithClaims(issuer = VEILEDER_AZURE_V2)
@RequestMapping(value = ["/api/v2/fastlege"])
class FastlegeAzureADApi @Inject constructor(
    private val fastlegeService: FastlegeService,
    private val metrikk: Metrikk,
    private val tilgangkontrollConsumer: TilgangkontrollConsumer,
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun finnFastlegeAazure(
        @RequestHeader headers: MultiValueMap<String, String>,
    ): Fastlege {
        metrikk.tellHendelse("finn_fastlege")

        val requestedPersonIdent = headers.getPersonIdent()?.let { personIdent ->
            PersonIdent(personIdent)
        } ?: throw IllegalArgumentException("No PersonIdent supplied")

        kastExceptionHvisIkkeTilgang(requestedPersonIdent)
        return fastlegeService.hentBrukersFastlege(requestedPersonIdent)
            ?: throw FastlegeIkkeFunnet()
    }

    @GetMapping(path = ["/fastleger"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getFastleger(
        @RequestHeader headers: MultiValueMap<String, String>,
    ): List<Fastlege> {
        metrikk.tellHendelse("get_fastleger")

        val requestedPersonIdent = headers.getPersonIdent()?.let { personIdent ->
            PersonIdent(personIdent)
        } ?: throw IllegalArgumentException("No PersonIdent supplied")

        kastExceptionHvisIkkeTilgang(requestedPersonIdent)
        return fastlegeService.hentBrukersFastleger(requestedPersonIdent)
    }

    private fun kastExceptionHvisIkkeTilgang(personIdent: PersonIdent) {
        val (harTilgang) = tilgangkontrollConsumer.accessAzureAdV2(personIdent)
        if (!harTilgang) {
            log.info("Har ikke tilgang til å se fastlegeinformasjon om brukeren")
            throw HarIkkeTilgang(null)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(FastlegeAzureADApi::class.java)
    }
}
