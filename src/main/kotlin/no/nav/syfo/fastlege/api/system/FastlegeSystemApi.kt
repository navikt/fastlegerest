package no.nav.syfo.fastlege.api.system

import io.swagger.annotations.Api
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.syfo.api.auth.OIDCIssuer.VEILEDER_AZURE_V2
import no.nav.syfo.fastlege.FastlegeService
import no.nav.syfo.fastlege.api.system.access.APIConsumerAccessService
import no.nav.syfo.fastlege.domain.Fastlege
import no.nav.syfo.fastlege.expection.FastlegeIkkeFunnet
import no.nav.syfo.metric.Metrikk
import no.nav.syfo.util.PersonIdent
import no.nav.syfo.util.getPersonIdent
import org.springframework.http.MediaType
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@Api(value = "fastlege", description = "Endepunkt for henting av fastlege som system")
@ProtectedWithClaims(issuer = VEILEDER_AZURE_V2)
@RequestMapping(value = ["/api/system/v1/fastlege"])
class FastlegeSystemApi @Inject constructor(
    private val apiConsumerAccessService: APIConsumerAccessService,
    private val fastlegeService: FastlegeService,
    private val metrikk: Metrikk,
) {
    @GetMapping(path = ["/aktiv/personident"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun fastlege(
        @RequestHeader headers: MultiValueMap<String, String>,
    ): Fastlege {
        metrikk.tellHendelse("system_finn_fastlege")

        val requestedPersonIdent = headers.getPersonIdent()?.let { personIdent ->
            PersonIdent(personIdent)
        } ?: throw IllegalArgumentException("No PersonIdent supplied")

        apiConsumerAccessService.validateConsumerApplicationAzp(
            authorizedApplicationNameList = authorizedAPIConsumerApplicationNameList
        )

        return fastlegeService.hentBrukersFastlege(requestedPersonIdent)
            ?: throw FastlegeIkkeFunnet()
    }

    companion object {
        private const val ISDIALOGMELDING_NAME = "isdialogmelding"
        val authorizedAPIConsumerApplicationNameList = listOf(
            ISDIALOGMELDING_NAME,
        )
    }
}
