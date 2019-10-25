package no.nav.syfo.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.common.auth.SubjectHandler;
import no.nav.syfo.domain.*;
import no.nav.syfo.services.exceptions.FastlegeIkkeFunnet;
import no.nhn.schemas.reg.common.en.WSPeriod;
import no.nhn.schemas.reg.flr.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static no.nav.syfo.mappers.FastlegeMappers.ws2fastlege;
import static no.nav.syfo.mappers.FastlegeMappers.ws2fastlegekontor;
import static no.nav.syfo.util.MapUtil.map;
import static no.nav.syfo.util.MapUtil.mapListe;

@Slf4j
@Service
public class FastlegeService {
    private IFlrReadOperations fastlegeSoapClient;
    private BrukerprofilService brukerprofilService;

    @Inject
    public FastlegeService(final IFlrReadOperations fastlegeSoapClient, final BrukerprofilService brukerprofilService) {
        this.fastlegeSoapClient = fastlegeSoapClient;
        this.brukerprofilService = brukerprofilService;
    }

    @Cacheable(value = "fastlege")
    public Optional<Fastlege> hentBrukersFastlege(String brukersFnr) {
        return finnAktivFastlege(hentBrukersFastleger(brukersFnr));
    }

    @Cacheable(value = "fastlege")
    public List<Fastlege> hentBrukersFastleger(String brukersFnr) {
        try {
            Pasient pasient = brukerprofilService.hentNavnByFnr(brukersFnr);
            WSPatientToGPContractAssociation patientGPDetails = fastlegeSoapClient.getPatientGPDetails(brukersFnr);
            return hentFastleger(patientGPDetails).stream()
                    .map(fastlege -> fastlege
                            .pasient(new Pasient()
                                    .fnr(brukersFnr)
                                    .fornavn(pasient.fornavn())
                                    .mellomnavn(pasient.mellomnavn())
                                    .etternavn(pasient.etternavn()))
                            .fastlegekontor(map(patientGPDetails.getGPContract().getGPOffice(), ws2fastlegekontor))
                    ).collect(toList());
        } catch (IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage e) {
            log.warn("{} Søkte opp {} og fikk en feil fra fastlegetjenesten. Dette skjer trolig fordi FNRet ikke finnes",
                    SubjectHandler.getIdent().orElse("-"), brukersFnr, e);
            throw new FastlegeIkkeFunnet("Feil ved oppslag av fastlege");
        } catch (RuntimeException e) {
            log.error("{} Søkte opp {} og fikk en feil fra fastlegetjenesten fordi tjenesten er nede",
                    SubjectHandler.getIdent().orElse("-"), brukersFnr, e);
            throw e;
        }
    }

    private List<Fastlege> hentFastleger(WSPatientToGPContractAssociation patientGPDetails) {
        return mapListe(patientGPDetails.getDoctorCycles().getGPOnContractAssociations(), ws2fastlege).stream()
                .map(fastlege -> fastlege
                        .pasientforhold(getPasientForhold(patientGPDetails.getPeriod()))
                        .herId(patientGPDetails.getGPHerId()))
                .collect(toList());
    }

    private Pasientforhold getPasientForhold(WSPeriod period) {
        Pasientforhold pasientforhold = new Pasientforhold();
        pasientforhold.fom(period.getFrom().toLocalDate());
        if (period.getTo() != null) {
            pasientforhold.tom(period.getTo().toLocalDate());
        }
        return pasientforhold;
    }

    private static Optional<Fastlege> finnAktivFastlege(List<Fastlege> fastleger) {
        return fastleger.stream()
                .filter(fastlege -> fastlege.pasientforhold().fom().isBefore(now())
                        && (fastlege.pasientforhold.tom() == null || fastlege.pasientforhold().tom().isAfter(now()))
                )
                .findFirst();
    }
}
