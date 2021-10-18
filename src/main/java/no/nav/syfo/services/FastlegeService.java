package no.nav.syfo.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.syfo.consumer.fastlege.FastlegeConsumer;
import no.nav.syfo.consumer.fastlege.PraksisInfo;
import no.nav.syfo.consumer.pdl.*;
import no.nav.syfo.domain.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.time.LocalDate.now;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static no.nav.syfo.util.StringUtil.lowerCapitalize;

@Slf4j
@Service
public class FastlegeService {
    private final PdlConsumer pdlConsumer;
    private final FastlegeConsumer fastlegeConsumer;

    @Inject
    public FastlegeService(
            final PdlConsumer pdlConsumer,
            final FastlegeConsumer fastlegeConsumer) {
        this.pdlConsumer = pdlConsumer;
        this.fastlegeConsumer = fastlegeConsumer;
    }

    @Cacheable(value = "fastlege")
    public Optional<Fastlege> hentBrukersFastlege(String brukersFnr) {
        return hentBrukersFastleger(brukersFnr).stream()
                .filter(isAktiv())
                .findFirst();
    }

    @Cacheable(value = "fastlege")
    public List<Fastlege> hentBrukersFastleger(String brukersFnr) {
        try {
            Optional<PdlHentPerson> maybePerson = Optional.ofNullable(pdlConsumer.person(brukersFnr));
            Pasient pasient = toPasient(maybePerson);
            List<Fastlege> fastlegeListe = fastlegeConsumer.getFastleger(brukersFnr);
            return fastlegeListe.stream()
                    .map(fastlege -> fastlege
                            .pasient(new Pasient()
                                    .fnr(brukersFnr)
                                    .fornavn(pasient.fornavn())
                                    .mellomnavn(pasient.mellomnavn())
                                    .etternavn(pasient.etternavn()))
                            .foreldreEnhetHerId(hentForeldreEnhetHerId(fastlege).orElse(null))
                    ).collect(toList());
        } catch (RuntimeException e) {
            log.error("Søkte opp og fikk en feil fra fastlegetjenesten fordi tjenesten er nede", e);
            throw e;
        }
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

    private Optional<Integer> hentForeldreEnhetHerId(Fastlege fastlege) {
        return ofNullable(fastlege.herId())
                .map(fastlegeConsumer::getPraksisInfo)
                .map(PraksisInfo::getForeldreEnhetHerId);
    }

    private static Predicate<Fastlege> isAktiv() {
        return fastlege -> fastlege.pasientforhold().fom().isBefore(now()) && fastlege.pasientforhold().tom().isAfter(now());
    }
}
