package no.nav.syfo.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.syfo.consumer.syfopartnerinfo.PartnerInfoResponse;
import no.nav.syfo.consumer.syfopartnerinfo.SyfoPartnerInfoConsumer;
import no.nav.syfo.consumer.ws.adresseregister.AdresseregisterConsumer;
import no.nav.syfo.consumer.ws.adresseregister.OrganisasjonPerson;
import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.domain.Partnerinformasjon;
import no.nav.syfo.services.exceptions.PartnerinformasjonIkkeFunnet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;

@Slf4j
@Service
public class PartnerService {

    private final AdresseregisterConsumer adresseregisterConsumer;
    private final SyfoPartnerInfoConsumer syfoPartnerInfoConsumer;

    public PartnerService(final AdresseregisterConsumer adresseregisterConsumer, final SyfoPartnerInfoConsumer syfoPartnerInfoConsumer) {
        this.adresseregisterConsumer = adresseregisterConsumer;
        this.syfoPartnerInfoConsumer = syfoPartnerInfoConsumer;
    }

    Partnerinformasjon getPartnerinformasjon(Fastlege fastlege) {
        Optional<String> fastlegeForeldreEnhetHerId = hentForeldreEnhetHerId(fastlege);
        try {
            if (fastlegeForeldreEnhetHerId.isPresent()) {
                String herId = fastlegeForeldreEnhetHerId.get();
                List<PartnerInfoResponse> partnerInfoList = syfoPartnerInfoConsumer.getPartnerId(herId);
                if (partnerInfoList.isEmpty()) {
                    throw new PartnerinformasjonIkkeFunnet("Kunne ikke finne partnerinformasjon! Ingen partnerInfo er knyttet til fastlegeForeldreEnhetHerId.");
                } else {
                    PartnerInfoResponse response = partnerInfoList.get(0);
                    return new Partnerinformasjon(String.valueOf(response.getPartnerId()), herId);
                }
            } else {
                throw new PartnerinformasjonIkkeFunnet("Kunne ikke finne partnerinformasjon! Fant ikke fastlegeForeldreEnhetHerId.");
            }
        } catch (PartnerinformasjonIkkeFunnet e) {
            throw e;
        } catch (Exception e) {
            log.warn("DIALOGMELDING-TRACE: Annen feil oppstått", e);
            throw e;
        }
    }

    private Optional<String> hentForeldreEnhetHerId(Fastlege fastlege) {
        Optional<String> fastlegeForeldreEnhetHerId = of(fastlege)
                .map(Fastlege::herId)
                .map(adresseregisterConsumer::hentFastlegeOrganisasjonPerson)
                .map(OrganisasjonPerson::getForeldreEnhetHerId)
                .map(Object::toString);

        log.info("DIALOGMELDING-TRACE: Fant fastlegeForeldreEnhetHerId: {}", fastlegeForeldreEnhetHerId.orElse(null));

        return fastlegeForeldreEnhetHerId;
    }
}
