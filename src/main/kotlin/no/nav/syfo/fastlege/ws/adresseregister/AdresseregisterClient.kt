package no.nav.syfo.fastlege.ws.adresseregister

import no.nav.syfo.fastlege.*
import no.nav.syfo.fastlege.domain.Behandler
import no.nhn.register.communicationparty.ICommunicationPartyService
import no.nhn.register.communicationparty.ICommunicationPartyServiceGetOrganizationDetailsGenericFaultFaultFaultMessage
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

    fun hentBehandlereForKontor(parentHerId: Int) =
        try {
            val wsOrg = adresseregisterSoapClient.getOrganizationDetails(parentHerId)
            wsOrg.people.organizationPersons.map { orgPerson ->
                Behandler(
                    aktiv = orgPerson.isActive,
                    herId = orgPerson.herId,
                    personIdent = orgPerson.person.citizenId.id,
                    hprId = orgPerson.person.hprInformation.hprNo,
                    fornavn = orgPerson.person.firstName,
                    mellomnavn = orgPerson.person.middleName,
                    etternavn = orgPerson.person.lastName,
                    type = orgPerson.type,
                )
            }.also {
                COUNT_ADRESSEREGISTER_BEHANDLERE_SUCCESS.increment()
            }
        } catch (e: ICommunicationPartyServiceGetOrganizationDetailsGenericFaultFaultFaultMessage) {
            COUNT_ADRESSEREGISTER_BEHANDLERE_NOT_FOUND.increment()
            log.error(
                "Søkte opp behandlere for kontor med HerId {} men aktuelt kontor ble ikke funnet",
                parentHerId,
                e
            )
            emptyList()
        } catch (e: RuntimeException) {
            COUNT_ADRESSEREGISTER_BEHANDLERE_FAIL.increment()
            log.error(
                "Søkte opp behandlere for kontor med HerId {} og fikk en feil fra adresseregister",
                parentHerId,
                e
            )
            throw e
        }

    companion object {
        private val log = LoggerFactory.getLogger(AdresseregisterClient::class.java)
    }
}
