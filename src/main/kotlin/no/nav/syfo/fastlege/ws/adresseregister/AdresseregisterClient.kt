package no.nav.syfo.fastlege.ws.adresseregister

import no.nav.syfo.fastlege.*
import no.nav.syfo.fastlege.domain.Adresse
import no.nav.syfo.fastlege.domain.Behandler
import no.nav.syfo.fastlege.domain.BehandlerKontor
import no.nhn.register.communicationparty.ICommunicationPartyService
import no.nhn.register.communicationparty.ICommunicationPartyServiceGetOrganizationDetailsGenericFaultFaultFaultMessage
import no.nhn.register.communicationparty.ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage
import no.nhn.register.communicationparty.WSOrganization
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

    fun hentBehandlerKontor(parentHerId: Int) =
        try {
            val wsOrg = adresseregisterSoapClient.getOrganizationDetails(parentHerId)
            BehandlerKontor(
                aktiv = wsOrg.isActive,
                herId = wsOrg.herId,
                navn = wsOrg.name,
                besoksadresse = extractPhysicalAddress(wsOrg, "RES"),
                postadresse = extractPhysicalAddress(wsOrg, "PST"),
                telefon = extractElectronicAddressField(wsOrg, "E_TLF"),
                epost = extractElectronicAddressField(wsOrg, "E_EDI"),
                orgnummer = wsOrg.organizationNumber?.toString(),
                behandlere = wsOrg.people.organizationPersons.map { orgPerson ->
                    Behandler(
                        aktiv = orgPerson.isActive,
                        herId = orgPerson.herId,
                        personIdent = orgPerson.person.citizenId.id,
                        hprId = orgPerson.person.hprInformation.hprNumber,
                        fornavn = orgPerson.person.firstName,
                        mellomnavn = orgPerson.person.middleName,
                        etternavn = orgPerson.person.lastName,
                        kategori = orgPerson.person.hprInformation.authorizations?.authorizations?.map { aut ->
                            aut.profession?.codeValue
                        }?.firstOrNull()
                    )
                },
            ).also {
                COUNT_ADRESSEREGISTER_BEHANDLERE_SUCCESS.increment()
            }
        } catch (e: ICommunicationPartyServiceGetOrganizationDetailsGenericFaultFaultFaultMessage) {
            COUNT_ADRESSEREGISTER_BEHANDLERE_NOT_FOUND.increment()
            log.error(
                "Søkte opp behandlere for kontor med HerId {} men aktuelt kontor ble ikke funnet",
                parentHerId,
                e
            )
            null
        } catch (e: RuntimeException) {
            COUNT_ADRESSEREGISTER_BEHANDLERE_FAIL.increment()
            log.error(
                "Søkte opp behandlere for kontor med HerId {} og fikk en feil fra adresseregister",
                parentHerId,
                e
            )
            throw e
        }

    private fun extractPhysicalAddress(wsOrg: WSOrganization, field: String) =
        wsOrg.physicalAddresses?.physicalAddresses?.stream()
            ?.filter { wsAddress ->
                wsAddress.type != null &&
                    wsAddress.type.isActive &&
                    wsAddress.type.codeValue == field
            }
            ?.findFirst()
            ?.map { wsAddress ->
                Adresse(
                    adresse = if (wsAddress.postbox.isNullOrBlank()) wsAddress.streetAddress else wsAddress.postbox,
                    postnummer = postnummer(wsAddress.postalCode),
                    poststed = wsAddress.city,
                )
            }
            ?.orElse(null)

    private fun extractElectronicAddressField(wsOrg: WSOrganization, field: String) =
        wsOrg.electronicAddresses?.electronicAddresses?.stream()
            ?.filter { it.type.codeValue == field }
            ?.findFirst()
            ?.map { it.address }
            ?.orElse("")

    private fun postnummer(postalCode: Int): String {
        val postnummer = StringBuilder(postalCode.toString())
        while (postnummer.length < 4) {
            postnummer.insert(0, "0")
        }
        return postnummer.toString()
    }

    companion object {
        private val log = LoggerFactory.getLogger(AdresseregisterClient::class.java)
    }
}
