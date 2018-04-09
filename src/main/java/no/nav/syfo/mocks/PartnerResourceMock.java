package no.nav.syfo.mocks;

import no.nav.emottak.schemas.*;

import static java.util.Arrays.*;

public class PartnerResourceMock implements PartnerResource {
    @Override
    public EbrevAbonnementResponse opprettEbrevAbonnement(OpprettEbrevAbonnementRequest opprettEbrevAbonnementRequest) {
        return new EbrevAbonnementResponse()
                .withBeskrivelse("MOCKDATA: beskrivelse")
                .withKey("MOCKDATA: key")
                .withStatus("MOCKDATA: status");
    }

    @Override
    public TjenesteResponse tjeneste(TjenesteRequest tjenesteRequest) {
        return new TjenesteResponse()
                .withKvittering(new WSKvitteringType()
                        .withBeskrivelse("MOCKDATA: beskrivelse"));
    }

    @Override
    public HentPartnerIDViaOrgnummerResponse hentPartnerIDViaOrgnummer(HentPartnerIDViaOrgnummerRequest hentPartnerIDViaOrgnummerRequest) {
        return new HentPartnerIDViaOrgnummerResponse()
                .withPartnerInformasjon(asList(
                        new WSPartnerInformasjon()
                                .withPartnerID("MOCKDATA: partnerId")
                                .withHERid("MOCKDATA: herId")));
    }
}
