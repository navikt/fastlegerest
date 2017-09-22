package no.nav.syfo.mappers;

import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.domain.Fastlegekontor;
import no.nav.syfo.domain.Pasient;
import no.nav.syfo.domain.Pasientforhold;
import no.nhn.register.common.WSElectronicAddress;
import no.nhn.schemas.reg.flr.WSGPOffice;
import no.nhn.schemas.reg.flr.WSGPOnContractAssociation;

import java.util.function.Function;

import static no.nav.sbl.java8utils.MapUtil.map;

public class FastlegeMappers {

    public static Function<WSGPOffice, Fastlegekontor> ws2fastlegekontor = wsgpOffice ->
            new Fastlegekontor()
                    .withNavn(wsgpOffice.getDisplayName())
                    .withOrgnummer(wsgpOffice.getOrganizationNumber())
                    .withTelefon(wsgpOffice.getElectronicAddresses().getElectronicAddresses().stream()
                            .filter(wsElectronicAddress -> wsElectronicAddress.getType().getCodeValue().equals("E_TLF"))
                            .map(WSElectronicAddress::getAddress)
                            .findFirst().orElse("")
                    )
                    .withEpost(wsgpOffice.getElectronicAddresses().getElectronicAddresses().stream()
                            .filter(wsElectronicAddress -> wsElectronicAddress.getType().getCodeValue().equals("E_EDI"))
                            .map(WSElectronicAddress::getAddress)
                            .findFirst().orElse("")
                    )
                    .withAdresse(wsgpOffice.getPhysicalAddresses().getPhysicalAddresses().stream()
                            .filter(wsPhysicalAddress -> wsPhysicalAddress.getType().isActive())
                            .map(wsPhysicalAddress -> wsPhysicalAddress.getStreetAddress() + ", " + wsPhysicalAddress.getPostalCode() + " " + wsPhysicalAddress.getCity())
                            .findFirst().orElse("")
                    );


    public static Function<WSGPOnContractAssociation, Fastlege> ws2fastlege = wsPatientToGPContractAssociation ->
            new Fastlege()
                    .withNavn(wsPatientToGPContractAssociation.getGP().getFirstName() + " " + wsPatientToGPContractAssociation.getGP().getLastName())
                    .withFnr(wsPatientToGPContractAssociation.getGP().getNIN())
                    .withFastlegekontor(map(wsPatientToGPContractAssociation.getGPContract().getGPOffice(), ws2fastlegekontor))
                    .withPasientforhold(new Pasientforhold()
                            .withFom(wsPatientToGPContractAssociation.getValid().getFrom().toLocalDate())
                            .withTom(wsPatientToGPContractAssociation.getValid().getTo().toLocalDate())
                    );

}
