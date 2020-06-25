package mappers;

import no.nav.syfo.domain.Fastlegekontor;
import no.nhn.register.fastlegeinformasjon.common.*;
import no.nhn.schemas.reg.flr.WSGPOffice;
import org.junit.Test;

import static no.nav.syfo.mappers.FastlegeMappers.ws2fastlegekontor;
import static org.assertj.core.api.Assertions.assertThat;

public class FastlegeMappersTest {


    @Test
    public void fastlegekontorMedTlfMenUtenNummerMappesRiktig() {
        Fastlegekontor fastlegekontor = ws2fastlegekontor.apply(new WSGPOffice()
                .withName("Fastlegekontoret")
                .withOrganizationNumber(123456789)
                .withElectronicAddresses(new WSArrayOfElectronicAddress().withElectronicAddresses(
                        new WSElectronicAddress()
                                .withAddress(null)
                                .withType(new WSCode()
                                        .withCodeValue("E_TLF")
                                ),
                        new WSElectronicAddress()
                                .withAddress(null)
                                .withType(new WSCode()
                                        .withCodeValue("E_EDI")
                                )
                ))
                .withPhysicalAddresses(new WSArrayOfPhysicalAddress().withPhysicalAddresses(
                        new WSPhysicalAddress()
                                .withType(new WSCode()
                                        .withActive(true)
                                        .withCodeValue("PST")
                                )
                                .withCity("Oslo")
                        .withPostalCode(3030)
                        .withPostbox("St. Olavs plass"),
                        new WSPhysicalAddress()
                                .withType(new WSCode()
                                        .withActive(true)
                                        .withCodeValue("RES")
                                )
                                .withStreetAddress("Sannergata 2")
                                .withPostalCode(4000)
                                .withCity("Oslo")
                )
        ));

        assertThat(fastlegekontor.navn()).isEqualTo("Fastlegekontoret");
        assertThat(fastlegekontor.orgnummer()).isEqualTo("123456789");
        assertThat(fastlegekontor.telefon()).isEqualTo("");
        assertThat(fastlegekontor.epost()).isEqualTo("");
        assertThat(fastlegekontor.besoeksadresseToString()).isEqualTo("Sannergata 2, 4000 Oslo");
        assertThat(fastlegekontor.postadresseToString()).isEqualTo("Postboks St. Olavs plass, 3030 Oslo");
    }

    @Test
    public void henteradresseOgEpostDersomDisseIkkeErNull() {
        Fastlegekontor fastlegekontor = ws2fastlegekontor.apply(new WSGPOffice()
                .withName("Fastlegekontoret")
                .withOrganizationNumber(123456789)
                .withElectronicAddresses(new WSArrayOfElectronicAddress().withElectronicAddresses(
                        new WSElectronicAddress()
                                .withAddress("12345678")
                                .withType(new WSCode()
                                        .withCodeValue("E_TLF")
                                ),
                        new WSElectronicAddress()
                                .withAddress("test@nav.no")
                                .withType(new WSCode()
                                        .withCodeValue("E_EDI")
                                )
                        )
                )
                .withPhysicalAddresses(new WSArrayOfPhysicalAddress())
        );

        assertThat(fastlegekontor.navn()).isEqualTo("Fastlegekontoret");
        assertThat(fastlegekontor.orgnummer()).isEqualTo("123456789");
        assertThat(fastlegekontor.telefon()).isEqualTo("12345678");
        assertThat(fastlegekontor.epost()).isEqualTo("test@nav.no");
    }
}
