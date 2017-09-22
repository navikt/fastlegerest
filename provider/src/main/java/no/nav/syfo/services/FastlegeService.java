package no.nav.syfo.services;

import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.domain.Pasient;
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
            List<WSPhysicalAddress> adresser = patientGPDetails.getGPContract().getGPOffice().getPhysicalAddresses().getPhysicalAddresses().stream().filter(wsPhysicalAddress -> wsPhysicalAddress.getType().isActive()).collect(toList());
            if (adresser.size() > 1) {
                LOG.warn("NB! Dette legekontoret har mer enn en aktiv adresse!!");
                for (WSPhysicalAddress address : adresser) {
                    LOG.warn("CodeValue: " + address.getType().getCodeValue());
                    LOG.warn("SimpleType: " + address.getType().getSimpleType());
                    LOG.warn("OID: " + address.getType().getOID());
                    LOG.warn("CodeText: " + address.getType().getCodeText());
                    LOG.warn("getDescription: " + address.getDescription());
                }
            }
            List<Fastlege> fastleger = mapListe(patientGPDetails.getDoctorCycles().getGPOnContractAssociations(), ws2fastlege);
            return finnAktivFastlege(fastleger)
                    .withPasient(new Pasient()
                            .withFnr(brukersFnr)
                            .withNavn(brukerprofilService.hentNavnByFnr(brukersFnr))
                    )
                    .withFastlegekontor(map(patientGPDetails.getGPContract().getGPOffice(), ws2fastlegekontor))
                  ;
        } catch (IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage e) {
            LOG.error("Det skjedde en feil i soap-kallet", e);
            throw new RuntimeException();
        }
    }

    private static Fastlege finnAktivFastlege(List<Fastlege> fastleger) {
        return fastleger.stream()
                .filter(fastlege -> fastlege.pasientforhold.fom.isBefore(now()) && fastlege.pasientforhold.tom.isAfter(now()))
                .findFirst().orElseThrow(() -> new NotFoundException("Fant ikke aktiv fastlege"));
    }
}
