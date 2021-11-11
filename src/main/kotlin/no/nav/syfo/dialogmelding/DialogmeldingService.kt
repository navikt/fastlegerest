package no.nav.syfo.dialogmelding

import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer
import no.nav.syfo.dialogmelding.api.RSOppfolgingsplan
import no.nav.syfo.dialogmelding.domain.RSHodemelding
import no.nav.syfo.dialogmelding.domain.createRSHodemelding
import no.nav.syfo.dialogmelding.exception.InnsendingFeiletException
import no.nav.syfo.fastlege.FastlegeService
import no.nav.syfo.fastlege.expection.FastlegeIkkeFunnet
import no.nav.syfo.metric.Metrikk
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import javax.inject.Inject

@Service
class DialogmeldingService @Inject constructor(
    private val azureAdV2TokenConsumer: AzureAdV2TokenConsumer,
    private val fastlegeService: FastlegeService,
    private val metrikk: Metrikk,
    private val partnerService: PartnerService,
    @Qualifier("default") private val restTemplate: RestTemplate,
    @Value("\${isdialogmelding.identifier}") private val isdialogmeldingIdentifier: String,
    @Value("\${isdialogmelding.url}") private val isdialogmeldingUrl: String,
) {
    fun sendOppfolgingsplan(oppfolgingsplan: RSOppfolgingsplan) {
        val fastlege = fastlegeService.hentBrukersFastlege(
            brukersFnr = oppfolgingsplan.sykmeldtFnr,
        ) ?: throw FastlegeIkkeFunnet()
        val partnerinformasjon = partnerService.getPartnerinformasjon(
            fastlege = fastlege,
        )
        val hodemelding = createRSHodemelding(
            fastlege = fastlege,
            partnerinformasjon = partnerinformasjon,
            oppfolgingsplan = oppfolgingsplan,
        )
        send(hodemelding)
    }

    private fun send(hodemelding: RSHodemelding) {
        val response = restTemplate.exchange(
            "$isdialogmeldingUrl/api/v1/send/oppfolgingsplan",
            HttpMethod.POST,
            HttpEntity(hodemelding, lagHeaders()),
            String::class.java,
        )
        val statusCode = response.statusCode
        if (statusCode.is3xxRedirection || statusCode.isError) {
            metrikk.countEvent("send_oppfolgingsplan_isdialogmelding_fail")
            log.error(
                "Feil ved sending av oppfølgingsdialog til fastlege: Fikk responskode {}",
                statusCode.value()
            )
            throw InnsendingFeiletException("Feil ved sending av oppfølgingsdialog til fastlege: Fikk responskode ${statusCode.value()}")
        }
        metrikk.countEvent("send_oppfolgingsplan_isdialogmelding_success")
    }

    private fun lagHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val token = azureAdV2TokenConsumer.getSystemToken(
            scopeClientId = isdialogmeldingIdentifier,
        )
        headers.setBearerAuth(token)
        return headers
    }

    companion object {
        private val log = LoggerFactory.getLogger(DialogmeldingService::class.java)
    }
}
