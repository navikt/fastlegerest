package no.nav.syfo.fastlege.ws.fastlegeregister

import no.nav.syfo.fastlege.COUNT_FASTLEGE_FAIL
import no.nav.syfo.fastlege.COUNT_FASTLEGE_NOT_FOUND
import no.nav.syfo.fastlege.COUNT_FASTLEGE_SUCCESS
import no.nav.syfo.fastlege.domain.*
import no.nav.syfo.util.PersonIdent
import no.nhn.schemas.reg.common.en.WSPeriod
import no.nhn.schemas.reg.flr.*
import org.slf4j.LoggerFactory
import java.time.LocalDate

class FastlegeInformasjonClient(
    val fastlegeSoapClient: IFlrReadOperations,
) {
    fun hentBrukersFastleger(ident: PersonIdent): List<Fastlege> =
        try {
            val patientGPDetails: WSPatientToGPContractAssociation = fastlegeSoapClient.getPatientGPDetails(ident.value)
            COUNT_FASTLEGE_SUCCESS.increment()
            hentFastleger(patientGPDetails)
        } catch (e: IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage) {
            COUNT_FASTLEGE_NOT_FOUND.increment()
            log.warn("Søkte opp og fikk en feil fra fastlegetjenesten. Dette skjer trolig fordi FNRet ikke finnes", e)
            emptyList()
        } catch (e: RuntimeException) {
            COUNT_FASTLEGE_FAIL.increment()
            log.error("Søkte opp og fikk en feil fra fastlegetjenesten fordi tjenesten er nede", e)
            throw e
        }

    private fun hentFastleger(patientGPDetails: WSPatientToGPContractAssociation): List<Fastlege> =
        patientGPDetails.doctorCycles.gpOnContractAssociations
            .map { wsgPOnContractAssociation ->
                Fastlege(
                    fornavn = wsgPOnContractAssociation.gp.firstName ?: "",
                    mellomnavn = wsgPOnContractAssociation.gp.middleName ?: "",
                    etternavn = wsgPOnContractAssociation.gp.lastName,
                    fnr = wsgPOnContractAssociation.gp.nin,
                    herId = patientGPDetails.gpHerId,
                    helsepersonellregisterId = wsgPOnContractAssociation.hprNumber,
                    fastlegekontor = getFastlegekontor(patientGPDetails.gpContract.gpOffice),
                    pasientforhold = getPeriode(patientGPDetails.period),
                    gyldighet = getPeriode(wsgPOnContractAssociation.valid),
                    relasjon = Relasjon(
                        kodeVerdi = wsgPOnContractAssociation.relationship.codeValue,
                        kodeTekst = wsgPOnContractAssociation.relationship.codeText,
                    ),
                    stillingsprosent = wsgPOnContractAssociation.workingPercentage,
                )
            }

    private fun getPeriode(period: WSPeriod) =
        Periode(
            fom = period.from.toLocalDate(),
            tom = if (period.to == null) LocalDate.parse("9999-12-31") else period.to.toLocalDate()
        )

    private fun getFastlegekontor(wsgpOffice: WSGPOffice) =
        Fastlegekontor(
            navn = wsgpOffice.name,
            orgnummer = wsgpOffice.organizationNumber?.toString(),
            telefon = extractElectronicAddressField(wsgpOffice, "E_TLF"),
            epost = extractElectronicAddressField(wsgpOffice, "E_EDI"),
            postadresse = extractPhysicalAddress(wsgpOffice, "PST"),
            besoeksadresse = extractPhysicalAddress(wsgpOffice, "RES"),
        )

    private fun extractPhysicalAddress(wsgpOffice: WSGPOffice, field: String) =
        wsgpOffice.physicalAddresses.physicalAddresses.stream()
            .filter { wsAddress ->
                wsAddress.type != null &&
                    wsAddress.type.isActive &&
                    wsAddress.type.codeValue == field
            }
            .findFirst()
            .map { wsAddress ->
                Adresse(
                    adresse = if (wsAddress.postbox.isNullOrBlank()) wsAddress.streetAddress else wsAddress.postbox,
                    postnummer = postnummer(wsAddress.postalCode),
                    poststed = wsAddress.city,
                )
            }
            .orElse(null)

    private fun extractElectronicAddressField(wsgpOffice: WSGPOffice, field: String) =
        wsgpOffice.electronicAddresses.electronicAddresses.stream()
            .filter { it.type.codeValue == field }
            .findFirst()
            .map { it.address }
            .orElse("")

    private fun postnummer(postalCode: Int): String {
        val postnummer = StringBuilder(postalCode.toString())
        while (postnummer.length < 4) {
            postnummer.insert(0, "0")
        }
        return postnummer.toString()
    }

    companion object {
        private val log = LoggerFactory.getLogger(FastlegeInformasjonClient::class.java)
    }
}
