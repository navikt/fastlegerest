package no.nav.syfo.mocks;

import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPersonnavn;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserResponse;

public class BrukerprofilMock implements BrukerprofilV3 {
    @Override
    public void ping() {

    }

    @Override
    public WSHentKontaktinformasjonOgPreferanserResponse hentKontaktinformasjonOgPreferanser(WSHentKontaktinformasjonOgPreferanserRequest request) {
        return new WSHentKontaktinformasjonOgPreferanserResponse().withBruker(new WSBruker()
                .withPersonnavn(new WSPersonnavn().withFornavn("MOCKDATA: Test").withEtternavn("MOCKDATA: Testesen")));
    }
}
