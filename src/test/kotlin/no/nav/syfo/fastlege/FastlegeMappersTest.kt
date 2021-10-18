package no.nav.syfo.fastlege

import no.nav.syfo.mappers.FastlegeMappers
import no.nhn.register.fastlegeinformasjon.common.*
import no.nhn.schemas.reg.flr.WSGPOffice
import org.assertj.core.api.Assertions
import org.junit.Test

class FastlegeMappersTest {
    @Test
    fun fastlegekontorMedTlfMenUtenNummerMappesRiktig() {
        val fastlegekontor = FastlegeMappers.ws2fastlegekontor.apply(WSGPOffice()
            .withName("Fastlegekontoret")
            .withOrganizationNumber(123456789)
            .withElectronicAddresses(WSArrayOfElectronicAddress().withElectronicAddresses(
                WSElectronicAddress()
                    .withAddress(null)
                    .withType(WSCode()
                        .withCodeValue("E_TLF")
                    ),
                WSElectronicAddress()
                    .withAddress(null)
                    .withType(WSCode()
                        .withCodeValue("E_EDI")
                    )
            ))
            .withPhysicalAddresses(WSArrayOfPhysicalAddress().withPhysicalAddresses(
                WSPhysicalAddress()
                    .withType(WSCode()
                        .withActive(true)
                        .withCodeValue("PST")
                    )
                    .withCity("Oslo")
                    .withPostalCode(3030)
                    .withPostbox("St. Olavs plass"),
                WSPhysicalAddress()
                    .withType(WSCode()
                        .withActive(true)
                        .withCodeValue("RES")
                    )
                    .withStreetAddress("Sannergata 2")
                    .withPostalCode(4000)
                    .withCity("Oslo")
            )
            ))
        Assertions.assertThat(fastlegekontor.navn()).isEqualTo("Fastlegekontoret")
        Assertions.assertThat(fastlegekontor.orgnummer()).isEqualTo("123456789")
        Assertions.assertThat(fastlegekontor.telefon()).isEqualTo("")
        Assertions.assertThat(fastlegekontor.epost()).isEqualTo("")
        Assertions.assertThat(fastlegekontor.besoeksadresseToString()).isEqualTo("Sannergata 2, 4000 Oslo")
        Assertions.assertThat(fastlegekontor.postadresseToString()).isEqualTo("Postboks St. Olavs plass, 3030 Oslo")
    }

    @Test
    fun henteradresseOgEpostDersomDisseIkkeErNull() {
        val fastlegekontor = FastlegeMappers.ws2fastlegekontor.apply(WSGPOffice()
            .withName("Fastlegekontoret")
            .withOrganizationNumber(123456789)
            .withElectronicAddresses(WSArrayOfElectronicAddress().withElectronicAddresses(
                WSElectronicAddress()
                    .withAddress("12345678")
                    .withType(WSCode()
                        .withCodeValue("E_TLF")
                    ),
                WSElectronicAddress()
                    .withAddress("test@nav.no")
                    .withType(WSCode()
                        .withCodeValue("E_EDI")
                    )
            )
            )
            .withPhysicalAddresses(WSArrayOfPhysicalAddress())
        )
        Assertions.assertThat(fastlegekontor.navn()).isEqualTo("Fastlegekontoret")
        Assertions.assertThat(fastlegekontor.orgnummer()).isEqualTo("123456789")
        Assertions.assertThat(fastlegekontor.telefon()).isEqualTo("12345678")
        Assertions.assertThat(fastlegekontor.epost()).isEqualTo("test@nav.no")
    }
}
