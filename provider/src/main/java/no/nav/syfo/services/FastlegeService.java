package no.nav.syfo.services;

import no.nav.sbl.java8utils.MapUtil;
import no.nav.syfo.domain.Fastlege;
import no.nhn.schemas.reg.flr.IFlrReadOperations;
import no.nhn.schemas.reg.flr.IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage;
import no.nhn.schemas.reg.flr.WSPatientToGPContractAssociation;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static no.nav.sbl.java8utils.MapUtil.*;
import static no.nav.syfo.mappers.FastlegeMappers.ws2fastlege;
import static org.slf4j.LoggerFactory.getLogger;

/*

Jeg har nevnt problemet for HDir og de sier at løsningen er å hente ut orgnummer fra fastlegespørringen og så bruke orgnummer for å finne legekontoret i adresseregisteret.
Jeg vil tro det kan fungere for de fleste legekontorene, selv om det nok fortsatt vil være noen unntak.

 */
public class FastlegeService {
    private static final Logger LOG = getLogger(FastlegeService.class);

    @Inject
    private IFlrReadOperations fastlegeSoapClient;


    public Fastlege hentBrukersFastlege(String brukersFnr) {
        try {
            WSPatientToGPContractAssociation patientGPDetails = fastlegeSoapClient.getPatientGPDetails(brukersFnr);
            List<Fastlege> fastleger = mapListe(patientGPDetails.getDoctorCycles().getGPOnContractAssociations(), ws2fastlege);
            return finnAktivFastlege(fastleger);
        } catch (IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage e) {
            LOG.error("Det skjedde en feil i soap-kallet", e);
            throw new RuntimeException();
        }
    }

    private static Fastlege finnAktivFastlege(List<Fastlege> fastleger) {
        return fastleger.stream()
                .filter(fastlege -> fastlege.fra.isBefore(now()) && fastlege.til.isAfter(now()))
                .findFirst().orElseThrow(() -> new NotFoundException("Fant ikke aktiv fastlege"));
    }
}
