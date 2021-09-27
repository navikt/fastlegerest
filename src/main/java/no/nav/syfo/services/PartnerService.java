package no.nav.syfo.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.syfo.consumer.syfopartnerinfo.PartnerInfoResponse;
import no.nav.syfo.consumer.syfopartnerinfo.SyfoPartnerInfoConsumer;
import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.domain.Partnerinformasjon;
import no.nav.syfo.services.exceptions.PartnerinformasjonIkkeFunnet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class PartnerService {

    private final SyfoPartnerInfoConsumer syfoPartnerInfoConsumer;

    public PartnerService(final SyfoPartnerInfoConsumer syfoPartnerInfoConsumer) {
        this.syfoPartnerInfoConsumer = syfoPartnerInfoConsumer;
    }

    Partnerinformasjon getPartnerinformasjon(Fastlege fastlege) {
        try {
            String fastlegeForeldreEnhetHerId = Optional.ofNullable(fastlege.foreldreEnhetHerId)
                    .orElseThrow(() -> new PartnerinformasjonIkkeFunnet("Kunne ikke finne partnerinformasjon! Fant ikke fastlegeForeldreEnhetHerId."))
                    .toString();
            PartnerInfoResponse partnerInfoResponse = getPartnerInfoResponse(fastlegeForeldreEnhetHerId)
                    .orElseThrow(() -> new PartnerinformasjonIkkeFunnet("Kunne ikke finne partnerinformasjon! Ingen partnerInfo er knyttet til fastlegeForeldreEnhetHerId."));
            return new Partnerinformasjon(String.valueOf(partnerInfoResponse.getPartnerId()), fastlegeForeldreEnhetHerId);
        } catch (PartnerinformasjonIkkeFunnet e) {
            throw e;
        } catch (Exception e) {
            log.warn("DIALOGMELDING-TRACE: Annen feil oppstått", e);
            throw e;
        }
    }

    private Optional<PartnerInfoResponse> getPartnerInfoResponse(String herId) {
        List<PartnerInfoResponse> partnerInfoResponses = syfoPartnerInfoConsumer.getPartnerId(herId);
        return Optional.ofNullable(partnerInfoResponses.isEmpty() ? null : partnerInfoResponses.get(0));
    }
}
