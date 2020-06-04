package no.nav.syfo.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.syfo.domain.*;
import no.nav.syfo.services.exceptions.PartnerinformasjonIkkeFunnet;
import no.nav.syfo.syfopartnerinfo.PartnerInfoResponse;
import no.nav.syfo.syfopartnerinfo.SyfoPartnerInfoConsumer;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Optional.of;

@Slf4j
@Service
public class PartnerService {

    private AdresseregisterService adresseregisterService;
    private SyfoPartnerInfoConsumer syfoPartnerInfoConsumer;

    public PartnerService(final AdresseregisterService adresseregisterService, final SyfoPartnerInfoConsumer syfoPartnerInfoConsumer) {
        this.adresseregisterService = adresseregisterService;
        this.syfoPartnerInfoConsumer = syfoPartnerInfoConsumer;
    }

    Partnerinformasjon getPartnerinformasjon(Fastlege fastlege) {
        Optional<String> fastlegeForeldreEnhetHerId = hentForeldreEnhetHerId(fastlege);
        try {
            if (fastlegeForeldreEnhetHerId.isPresent()) {
                String herId = fastlegeForeldreEnhetHerId.get();
                PartnerInfoResponse response = syfoPartnerInfoConsumer.getPartnerId(herId).get(0);
                return new Partnerinformasjon(String.valueOf(response.getPartnerId()), herId);
            } else {
                throw new PartnerinformasjonIkkeFunnet("Kunne ikke finne partnerinformasjon!");
            }
        } catch (PartnerinformasjonIkkeFunnet e) {
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
}
