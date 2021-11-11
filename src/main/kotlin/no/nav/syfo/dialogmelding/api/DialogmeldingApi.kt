package no.nav.syfo.dialogmelding.api

import io.swagger.annotations.Api
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.syfo.api.auth.OIDCIssuer.EKSTERN
import no.nav.syfo.api.auth.OIDCIssuer.VEILEDER_AZURE_V2
import no.nav.syfo.metric.Metrikk
import no.nav.syfo.dialogmelding.DialogmeldingService
import no.nav.syfo.fastlege.expection.FastlegeIkkeFunnet
import no.nav.syfo.dialogmelding.exception.PartnerinformasjonIkkeFunnet
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@Api(value = "dialogmelding", description = "Endepunkt for sending av dialogmelding")
class DialogmeldingApi(
    private val dialogmeldingService: DialogmeldingService,
    private val metrikk: Metrikk
) {
    @PostMapping(
        path = ["/api/dialogmelding/v1/sendOppfolgingsplanFraSelvbetjening"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ProtectedWithClaims(issuer = EKSTERN)
    fun sendOppfolgingsplanFraSBS(@RequestBody oppfolgingsplan: @Valid RSOppfolgingsplan) {
        sendPlan(oppfolgingsplan)
    }

    @PostMapping(path = ["/api/dialogmelding/v2/oppfolgingsplan/lps"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ProtectedWithClaims(issuer = VEILEDER_AZURE_V2)
    fun mottaOppfolgingsplanLPSAzureAd(
        @RequestBody oppfolgingsplan: @Valid RSOppfolgingsplan
    ) {
        sendPlan(oppfolgingsplan)
    }

    private fun sendPlan(oppfolgingsplan: RSOppfolgingsplan) {
        try {
            metrikk.countEvent("send_oppfolgingsplan")
            log.info("Sender oppfølgingsplan")
            dialogmeldingService.sendOppfolgingsplan(oppfolgingsplan)
        } catch (e: FastlegeIkkeFunnet) {
            metrikk.countEvent("send_oppfolgingsplan_fastlegeikkefunnet")
            log.warn("Feil ved sending av oppfølgingsplan, FastlegeIkkeFunnet", e)
            throw e
        } catch (e: PartnerinformasjonIkkeFunnet) {
            metrikk.countEvent("send_oppfolgingsplan_partnerinformasjonikkefunnet")
            log.warn("Feil ved sending av oppfølgingsplan, PartnerinformasjonIkkeFunnet", e)
            throw e
        } catch (e: Exception) {
            metrikk.countEvent("send_oppfolgingsplan_excption")
            log.error("Feil ved sending av oppfølgingsplan, Ukjent Feil", e)
            throw e
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DialogmeldingApi::class.java)
    }
}
