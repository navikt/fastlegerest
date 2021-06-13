package testhelper

import no.nhn.register.fastlegeinformasjon.common.WSArrayOfElectronicAddress
import no.nhn.register.fastlegeinformasjon.common.WSArrayOfPhysicalAddress
import no.nhn.schemas.reg.common.en.WSPeriod
import no.nhn.schemas.reg.flr.*
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import javax.xml.datatype.*

object MockUtils {
    const val LEGEKONTOR = "Pontypandy Legekontor"
    fun toXMLGregorianCalendar(localDateTime: LocalDateTime): XMLGregorianCalendar? {
        var xmlGregorianCalendar: XMLGregorianCalendar? = null
        try {
            xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(localDateTime.toString())
        } catch (e: DatatypeConfigurationException) {
            e.printStackTrace()
        }
        return xmlGregorianCalendar
    }

    @JvmStatic
    @Throws(IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage::class)
    fun mockHarFastlege(fastlegeSoapClient: IFlrReadOperations) {
        val fastlege = mockFastlege()
        val legeKontor = mockLegeKontor()
        val fastlegeKontrakt = WSGPContract().withGPOffice(legeKontor)
        val fastlegeResponse = WSPatientToGPContractAssociation()
            .withDoctorCycles(fastlege)
            .withGPContract(fastlegeKontrakt)
            .withPeriod(WSPeriod()
                .withFrom(LocalDateTime.now().minusYears(4))
                .withTo(LocalDateTime.now().plusYears(4)))
            .withGPHerId(404)
        Mockito.`when`(fastlegeSoapClient.getPatientGPDetails(ArgumentMatchers.anyString())).thenReturn(fastlegeResponse)
    }


    private fun mockFastlege(): WSArrayOfGPOnContractAssociation {
        return WSArrayOfGPOnContractAssociation()
            .withGPOnContractAssociations(WSGPOnContractAssociation()
                .withHprNumber(123)
                .withGP(WSPerson()
                    .withFirstName("Michaela")
                    .withMiddleName("Mike")
                    .withLastName("Quinn")
                    .withNIN("Kake"))
            )
    }

    private fun mockLegeKontor(): WSGPOffice {
        return WSGPOffice()
            .withName(LEGEKONTOR)
            .withOrganizationNumber(88888888)
            .withPhysicalAddresses(WSArrayOfPhysicalAddress())
            .withElectronicAddresses(WSArrayOfElectronicAddress())
    }
}

@Throws(IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage::class)
fun mockIngenFastleger(fastlegeSoapClient: IFlrReadOperations) {
    Mockito.`when`(fastlegeSoapClient.getPatientGPDetails(ArgumentMatchers.anyString())).thenThrow(IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage())
}

fun mockResponseFraTilgangskontroll(restTemplate: RestTemplate, httpStatus: HttpStatus) {
    val okJson = "{\"harTilgang\":true, \"begrunnelse\":\"\"}"
    val forbiddenJson = "{\"harTilgang\":false, \"begrunnelse\":\"GEOGRAFISK\"}"
    val responseEntity = if (httpStatus == HttpStatus.FORBIDDEN) ResponseEntity<Any>(forbiddenJson, httpStatus) else ResponseEntity<Any>(okJson, httpStatus)
    Mockito.`when`(restTemplate.exchange(
        Mockito.anyString(),
        Mockito.eq(HttpMethod.GET),
        Mockito.any(),
        Mockito.any<Class<Any>>()
    )).thenReturn(responseEntity)
}
