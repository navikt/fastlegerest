package no.nav.syfo.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.syfo.consumer.pdl.*;
import no.nav.syfo.domain.*;
import no.nav.syfo.services.exceptions.FastlegeIkkeFunnet;
import no.nhn.schemas.reg.common.en.WSPeriod;
import no.nhn.schemas.reg.flr.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static no.nav.syfo.mappers.FastlegeMappers.ws2fastlege;
import static no.nav.syfo.mappers.FastlegeMappers.ws2fastlegekontor;
import static no.nav.syfo.util.MapUtil.map;
import static no.nav.syfo.util.MapUtil.mapListe;
import static no.nav.syfo.util.StringUtil.lowerCapitalize;

@Slf4j
@Service
public class FastlegeService {
    private IFlrReadOperations fastlegeSoapClient;
    private PdlConsumer pdlConsumer;

    @Inject
    public FastlegeService(
            final IFlrReadOperations fastlegeSoapClient,
            final PdlConsumer pdlConsumer
    ) {
        this.fastlegeSoapClient = fastlegeSoapClient;
        this.pdlConsumer = pdlConsumer;
    }

    @Cacheable(value = "fastlege")
    public Optional<Fastlege> hentBrukersFastlege(String brukersFnr) {
        return finnAktivFastlege(hentBrukersFastleger(brukersFnr));
    }

    @Cacheable(value = "fastlege")
    public List<Fastlege> hentBrukersFastleger(String brukersFnr) {
        try {
            Optional<PdlHentPerson> maybePerson = Optional.ofNullable(pdlConsumer.person(brukersFnr));
            Pasient pasient = toPasient(maybePerson);
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
            log.warn("Søkte opp og fikk en feil fra fastlegetjenesten. Dette skjer trolig fordi FNRet ikke finnes", e);
            throw new FastlegeIkkeFunnet("Feil ved oppslag av fastlege");
        } catch (RuntimeException e) {
            log.error("Søkte opp og fikk en feil fra fastlegetjenesten fordi tjenesten er nede", e);
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

    private Pasient toPasient(Optional<PdlHentPerson> maybePerson) {
        Pasient pasient = new Pasient();
        if (maybePerson.isPresent()) {
            Optional<PdlPerson> pdlHentPerson = Optional.ofNullable(maybePerson.get().getHentPerson());
            if (pdlHentPerson.isPresent()) {
                List<PdlPersonNavn> nameList = pdlHentPerson.get().getNavn();
                if (nameList.isEmpty()) {
                    return pasient;
                } else {
                    PdlPersonNavn personNavn = nameList.get(0);
                    pasient.fornavn = lowerCapitalize(personNavn.getFornavn());
                    pasient.mellomnavn = lowerCapitalize(personNavn.getMellomnavn());
                    pasient.etternavn = lowerCapitalize(personNavn.getEtternavn());
                }
            }
        }
        return pasient;
    }

    private LocalDate toLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {
        return LocalDate.of(
                xmlGregorianCalendar.getYear(),
                xmlGregorianCalendar.getMonth(),
                xmlGregorianCalendar.getDay());
    }


    private Pasientforhold getPasientForhold(WSPeriod period) {
        return new Pasientforhold()
                .fom(period.getFrom().toLocalDate())
                .tom(period.getTo() == null ? LocalDate.parse("9999-12-31") : period.getTo().toLocalDate());
    }

    private static Optional<Fastlege> finnAktivFastlege(List<Fastlege> fastleger) {
        return fastleger.stream()
                .filter(fastlege -> fastlege.pasientforhold().fom().isBefore(now()) && fastlege.pasientforhold().tom().isAfter(now()))
                .findFirst();
    }
}
