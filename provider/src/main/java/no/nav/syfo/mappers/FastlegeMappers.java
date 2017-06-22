package no.nav.syfo.mappers;

import no.nav.syfo.domain.Fastlege;
import no.nhn.schemas.reg.flr.WSGPOnContractAssociation;

import java.util.function.Function;

public class FastlegeMappers {

    public static Function<WSGPOnContractAssociation, Fastlege> ws2fastlege = wsPatientToGPContractAssociation ->
            new Fastlege()
            .withNavn(wsPatientToGPContractAssociation.getGP().getFirstName() + " " + wsPatientToGPContractAssociation.getGP().getLastName())
            .withFra(wsPatientToGPContractAssociation.getValid().getFrom().toLocalDate())
            .withTil(wsPatientToGPContractAssociation.getValid().getTo().toLocalDate())
             ;
}
