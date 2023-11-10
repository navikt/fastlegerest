package no.nav.syfo.fastlege.ws.adresseregister

import no.nav.syfo.fastlege.COUNT_ADRESSEREGISTER_FAIL
import no.nav.syfo.fastlege.COUNT_ADRESSEREGISTER_NOT_FOUND
import no.nav.syfo.fastlege.COUNT_ADRESSEREGISTER_SUCCESS
import no.nhn.register.communicationparty.ICommunicationPartyService
import no.nhn.register.communicationparty.ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage
import org.slf4j.LoggerFactory

class AdresseregisterClient(
    private val adresseregisterSoapClient: ICommunicationPartyService
) {
    fun hentPraksisInfoForFastlege(herId: Int) =
        try {
            val wsOrganizationPerson = adresseregisterSoapClient.getOrganizationPersonDetails(herId)
            COUNT_ADRESSEREGISTER_SUCCESS.increment()
            PraksisInfo(
                foreldreEnhetHerId = wsOrganizationPerson.parentHerId
            )
        } catch (e: ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage) {
            COUNT_ADRESSEREGISTER_NOT_FOUND.increment()
            log.error(
                "Søkte opp fastlege med HerId {} og fikk en feil fra adresseregister fordi fastlegen mangler HerId",
                herId,
                e
            )
            null
        } catch (e: RuntimeException) {
            COUNT_ADRESSEREGISTER_FAIL.increment()
            log.error(
                "Søkte opp fastlege med HerId {} og fikk en uventet feil fra adresseregister fordi tjenesten er nede",
                herId,
                e
            )
            throw e
        }

    companion object {
        private val log = LoggerFactory.getLogger(AdresseregisterClient::class.java)
    }
}
