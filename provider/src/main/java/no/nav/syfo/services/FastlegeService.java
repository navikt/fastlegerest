package no.nav.syfo.services;

import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.domain.Pasient;
import no.nav.syfo.domain.Pasientforhold;
import no.nhn.register.common.WSPhysicalAddress;
import no.nhn.schemas.reg.flr.IFlrReadOperations;
import no.nhn.schemas.reg.flr.IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage;
import no.nhn.schemas.reg.flr.WSPatientToGPContractAssociation;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.java8utils.MapUtil.map;
import static no.nav.sbl.java8utils.MapUtil.mapListe;
import static no.nav.syfo.mappers.FastlegeMappers.ws2fastlege;
import static no.nav.syfo.mappers.FastlegeMappers.ws2fastlegekontor;
import static org.slf4j.LoggerFactory.getLogger;

/*

Jeg har nevnt problemet for HDir og de sier at løsningen er å hente ut orgnummer fra fastlegespørringen og så bruke orgnummer for å finne legekontoret i adresseregisteret.
Jeg vil tro det kan fungere for de fleste legekontorene, selv om det nok fortsatt vil være noen unntak.

 */
public class FastlegeService {
    private static final Logger LOG = getLogger(FastlegeService.class);

    @Inject
    private IFlrReadOperations fastlegeSoapClient;
    @Inject
    private BrukerprofilService brukerprofilService;

    public Fastlege hentBrukersFastlege(String brukersFnr) {
        try {
            WSPatientToGPContractAssociation patientGPDetails = fastlegeSoapClient.getPatientGPDetails(brukersFnr);
            List<Fastlege> fastleger = mapListe(patientGPDetails.getDoctorCycles().getGPOnContractAssociations(), ws2fastlege).stream()
                    .map(fastlege -> fastlege.pasientforhold(new Pasientforhold()
                            .fom(patientGPDetails.getPeriod().getFrom().toLocalDate())
                            .tom(patientGPDetails.getPeriod().getTo().toLocalDate())))
                    .collect(toList());

            return finnAktivFastlege(fastleger)
                    .pasient(new Pasient()
                            .fnr(brukersFnr)
                            .navn(brukerprofilService.hentNavnByFnr(brukersFnr))
                    )
                    .fastlegekontor(map(patientGPDetails.getGPContract().getGPOffice(), ws2fastlegekontor));
        } catch (IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage e) {
            LOG.error("Personen er ikke tilknyttet noen fastlegekontrakt.", e);
            throw new NotFoundException();
        }
    }

    public List<Fastlege> hentBrukersFastleger(String brukersFnr) {
        try {
            WSPatientToGPContractAssociation patientGPDetails = fastlegeSoapClient.getPatientGPDetails(brukersFnr);
            return mapListe(patientGPDetails.getDoctorCycles().getGPOnContractAssociations(), ws2fastlege).stream()
                    .map(fastlege -> fastlege.pasientforhold(new Pasientforhold()
                            .fom(patientGPDetails.getPeriod().getFrom().toLocalDate())
                            .tom(patientGPDetails.getPeriod().getTo().toLocalDate())))
                    .collect(toList());
        } catch (IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage e) {
            LOG.error("Personen er ikke tilknyttet noen fastlegekontrakt.", e);
            throw new NotFoundException();
        }
    }

    private static Fastlege finnAktivFastlege(List<Fastlege> fastleger) {
        return fastleger.stream()
                .filter(fastlege -> fastlege.pasientforhold.fom.isBefore(now()) && fastlege.pasientforhold.tom.isAfter(now()))
                .findFirst().orElseThrow(() -> new NotFoundException("Fant ikke aktiv fastlege"));
    }
}
