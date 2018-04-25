package no.nav.syfo.services;

import no.nav.emottak.schemas.HentPartnerIDViaOrgnummerRequest;
import no.nav.emottak.schemas.PartnerResource;
import no.nav.syfo.domain.Partnerinformasjon;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class PartnerService {

    @Inject
    private PartnerResource partnerResource;

    public List<Partnerinformasjon> hentPartnerinformasjon(String orgnummer) {
        return partnerResource.hentPartnerIDViaOrgnummer(
                new HentPartnerIDViaOrgnummerRequest()
                        .withOrgnr(orgnummer))
                .getPartnerInformasjon()
                .stream()
                .map(pi -> new Partnerinformasjon(pi.getPartnerID(), pi.getHERid()))
                .collect(toList());
    }
}
