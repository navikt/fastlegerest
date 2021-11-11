package no.nav.syfo.dialogmelding

import no.nav.syfo.consumer.syfopartnerinfo.SyfoPartnerInfoConsumer
import no.nav.syfo.dialogmelding.exception.PartnerinformasjonIkkeFunnet
import no.nav.syfo.fastlege.domain.Fastlege
import no.nav.syfo.fastlege.domain.Partnerinformasjon
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PartnerService(
    private val syfoPartnerInfoConsumer: SyfoPartnerInfoConsumer,
) {
    fun getPartnerinformasjon(fastlege: Fastlege): Partnerinformasjon {
        return try {
            val fastlegeForeldreEnhetHerId = fastlege.foreldreEnhetHerId
                ?: throw PartnerinformasjonIkkeFunnet("Kunne ikke finne partnerinformasjon! Fant ikke fastlegeForeldreEnhetHerId.")
            val partnerId = syfoPartnerInfoConsumer.getPartnerId(fastlegeForeldreEnhetHerId).firstOrNull()?.partnerId
                ?: throw PartnerinformasjonIkkeFunnet("Kunne ikke finne partnerinformasjon! Ingen partnerInfo er knyttet til fastlegeForeldreEnhetHerId.")
            Partnerinformasjon(
                partnerId = partnerId,
                herId = fastlegeForeldreEnhetHerId,
            )
        } catch (e: PartnerinformasjonIkkeFunnet) {
            throw e
        } catch (e: Exception) {
            log.warn("DIALOGMELDING-TRACE: Annen feil oppstått", e)
            throw e
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(PartnerService::class.java)
    }
}
