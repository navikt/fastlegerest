package no.nav.syfo.consumer.ws.adresseregister

import no.nav.syfo.services.exceptions.OrganisasjonPersonInformasjonIkkeFunnet
import no.nhn.register.communicationparty.ICommunicationPartyService
import no.nhn.register.communicationparty.ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import javax.inject.Inject

@Component
class AdresseregisterConsumer @Inject constructor(
    private val adresseregisterSoapClient: ICommunicationPartyService
) {
    @Cacheable(value = ["fastlegeOrganisasjon"])
    fun hentFastlegeOrganisasjonPerson(herId: Int): OrganisasjonPerson {
        return try {
            val wsOrganizationPerson = adresseregisterSoapClient.getOrganizationPersonDetails(herId)
            OrganisasjonPerson(
                foreldreEnhetHerId = wsOrganizationPerson.parentHerId
            )
        } catch (e: ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage) {
            log.error("Søkte opp fastlege med HerId {} og fikk en feil fra adresseregister fordi fastlegen mangler HerId", herId, e)
            throw OrganisasjonPersonInformasjonIkkeFunnet("Fant ikke parentHerId for fastlege med HerId $herId")
        } catch (e: RuntimeException) {
            log.error("Søkte opp fastlege med HerId {} og fikk en uventet feil fra adresseregister fordi tjenesten er nede", herId, e)
            throw e
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(AdresseregisterConsumer::class.java)
    }
}
