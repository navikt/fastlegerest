package no.nav.syfo.services;

import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.domain.Pasient;
import no.nav.syfo.domain.Pasientforhold;
import no.nhn.schemas.reg.flr.IFlrReadOperations;
import no.nhn.schemas.reg.flr.IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage;
import no.nhn.schemas.reg.flr.WSPatientToGPContractAssociation;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static no.nav.sbl.java8utils.MapUtil.map;
import static no.nav.sbl.java8utils.MapUtil.mapListe;
import static no.nav.syfo.mappers.FastlegeMappers.ws2fastlege;
import static no.nav.syfo.mappers.FastlegeMappers.ws2fastlegekontor;
import static org.slf4j.LoggerFactory.getLogger;

public class FastlegeService {
    private static final Logger LOG = getLogger(FastlegeService.class);

    @Inject
    private IFlrReadOperations fastlegeSoapClient;
    @Inject
    private BrukerprofilService brukerprofilService;

    @Cacheable(value = "fastlege", keyGenerator = "userkeygenerator")
    public Fastlege hentBrukersFastlege(String brukersFnr) {
        return finnAktivFastlege(hentBrukersFastleger(brukersFnr));
    }

    @Cacheable(value = "fastlege", keyGenerator = "userkeygenerator")
    public List<Fastlege> hentBrukersFastleger(String brukersFnr) {
        try {
            WSPatientToGPContractAssociation patientGPDetails = fastlegeSoapClient.getPatientGPDetails(brukersFnr);
            return hentFastleger(patientGPDetails).stream()
                    .map(fastlege -> fastlege
                            .pasient(new Pasient()
                                    .fnr(brukersFnr)
                                    .navn(brukerprofilService.hentNavnByFnr(brukersFnr)))
                            .fastlegekontor(map(patientGPDetails.getGPContract().getGPOffice(), ws2fastlegekontor))
                    ).collect(toList());
        } catch (IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage e) {
            LOG.error("{} Søkte opp {} og fikk en feil fra fastlegetjenesten. Dette skjer trolig fordi FNRet ikke finnes", getSubjectHandler().getUid(), brukersFnr, e);
            throw new NotFoundException();
        } catch (RuntimeException e) {
            LOG.error("{} Søkte opp {} og fikk en feil fra fastlegetjenesten fordi tjenesten er nede", getSubjectHandler().getUid(), brukersFnr, e);
            throw e;
        }
    }

    private List<Fastlege> hentFastleger(WSPatientToGPContractAssociation patientGPDetails) {
        return mapListe(patientGPDetails.getDoctorCycles().getGPOnContractAssociations(), ws2fastlege).stream()
                .map(fastlege -> fastlege.pasientforhold(new Pasientforhold()
                        .fom(patientGPDetails.getPeriod().getFrom().toLocalDate())
                        .tom(patientGPDetails.getPeriod().getTo().toLocalDate())))
                .collect(toList());
    }

    private static Fastlege finnAktivFastlege(List<Fastlege> fastleger) {
        return fastleger.stream()
                .filter(fastlege -> fastlege.pasientforhold.fom.isBefore(now()) && fastlege.pasientforhold.tom.isAfter(now()))
                .findFirst().orElseThrow(() -> new NotFoundException("Fant ikke aktiv fastlege"));
    }
}
