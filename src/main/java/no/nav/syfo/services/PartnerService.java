package no.nav.syfo.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.emottak.schemas.HentPartnerIDViaOrgnummerRequest;
import no.nav.emottak.schemas.PartnerResource;
import no.nav.syfo.domain.*;
import no.nav.syfo.services.exceptions.PartnerinformasjonIkkeFunnet;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class PartnerService {

    private PartnerResource partnerResource;
    private AdresseregisterService adresseregisterService;

    @Inject
    public PartnerService(final PartnerResource partnerResource, final AdresseregisterService adresseregisterService) {
        this.partnerResource = partnerResource;
        this.adresseregisterService = adresseregisterService;
    }

    Partnerinformasjon getPartnerinformasjon(Fastlege fastlege) {
        String orgnummer = fastlege.fastlegekontor().orgnummer();
        Optional<String> fastlegeForeldreEnhetHerId = hentForeldreEnhetHerId(fastlege);
        List<Partnerinformasjon> partnerinformasjonListe = hentPartnerinformasjonListe(orgnummer);

        try {
            if (partnerinformasjonListe.isEmpty()) {
                log.warn("Fant ikke partnerinformasjon for orgnummer {} fordi partnerregister returnerte tom liste", orgnummer);
                throw new PartnerinformasjonIkkeFunnet("Fant ikke partnerinformasjon for orgnummer " + orgnummer);
            }
            return velgPartner(orgnummer, fastlegeForeldreEnhetHerId, partnerinformasjonListe);
        } catch (PartnerinformasjonIkkeFunnet e) {
            log.warn("DIALOGMELDING-TRACE: Fant {} partnere, men ingen ble valgt", partnerinformasjonListe.size());
            log.warn("DIALOGMELDING-TRACE: Partnerinformasjon.herid: {} - fastlegeForeldreEnhetHerId: {}",
                    partnerinformasjonListe
                            .stream()
                            .map(Partnerinformasjon::getHerId)
                            .collect(toList()),
                    fastlegeForeldreEnhetHerId.orElse(null));
            throw e;
        } catch (Exception e) {
            log.warn("DIALOGMELDING-TRACE: Annen feil oppstått", e);
            throw e;
        }
    }

    private Optional<String> hentForeldreEnhetHerId(Fastlege fastlege){
        Optional<String> fastlegeForeldreEnhetHerId = of(fastlege)
                .map(Fastlege::herId)
                .map(adresseregisterService::hentFastlegeOrganisasjonPerson)
                .map(OrganisasjonPerson::foreldreEnhetHerId)
                .map(Object::toString);

        log.info("DIALOGMELDING-TRACE: Fant fastlegeForeldreEnhetHerId: {}", fastlegeForeldreEnhetHerId.orElse(null));

        return fastlegeForeldreEnhetHerId;
    }

    private List<Partnerinformasjon> hentPartnerinformasjonListe(String orgnummer) {
        List<Partnerinformasjon> partnerinformasjonListe = partnerResource.hentPartnerIDViaOrgnummer(
                new HentPartnerIDViaOrgnummerRequest()
                        .withOrgnr(orgnummer))
                .getPartnerInformasjon()
                .stream()
                .map(pi -> new Partnerinformasjon(pi.getPartnerID(), pi.getHERid()))
                .collect(toList());

        log.warn("DIALOGMELDING-TRACE: Fant {} partnere for orgnummer {}: [{}]",
                partnerinformasjonListe.size(),
                orgnummer,
                partnerinformasjonListe
                        .stream()
                        .map(partnerinformasjon -> "(index=" + partnerinformasjonListe.indexOf(partnerinformasjon)
                                + ", partner=" + partnerinformasjon.getPartnerId()
                                + ", her=" + partnerinformasjon.getHerId() + ")")
                        .collect(joining(", ")));

        return partnerinformasjonListe;
    }


    private Partnerinformasjon velgPartner(String orgnummer, Optional<String> fastlegeForeldreEnhetHerId, List<Partnerinformasjon> partnerinformasjonListe) {
        Partnerinformasjon valgtPartnerinformasjon = partnerinformasjonListe
                .stream()
                .filter(partner -> partner.getHerId() != null)
                .filter(partnerinfo -> {
                    boolean harHerIdLikFastlegeForeldreHerID = fastlegeForeldreEnhetHerId.map(partnerinfo.getHerId()::equals).orElse(false);
                    log.info("DIALOGMELDING-TRACE: Er Parterinformasjon sin HerId({}) lik Fastlege sin HerId({}): {}",
                            partnerinfo.getHerId(),
                            fastlegeForeldreEnhetHerId.orElse(null),
                            harHerIdLikFastlegeForeldreHerID);
                    return harHerIdLikFastlegeForeldreHerID;
                })
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Fant ikke partnerinformasjon for orgnummer {} fordi HerId ikke var lik fasteleges ForeldreHerId", orgnummer);
                    return new PartnerinformasjonIkkeFunnet("Fant ikke partnerinformasjon for orgnummer " + orgnummer);
                });

        log.info("DIALOGMELDING-TRACE: Av {} mulige partnere er index {} valgt",
                partnerinformasjonListe.size(),
                partnerinformasjonListe.indexOf(valgtPartnerinformasjon));

        return valgtPartnerinformasjon;
    }


}
