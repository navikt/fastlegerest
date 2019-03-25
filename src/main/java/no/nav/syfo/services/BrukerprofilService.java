package no.nav.syfo.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.common.auth.SubjectHandler;
import no.nav.syfo.domain.Pasient;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.*;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;


@Service
@Slf4j
public class BrukerprofilService {

    private BrukerprofilV3 brukerprofilV3;

    @Inject
    public BrukerprofilService(final BrukerprofilV3 brukerprofilV3){
        this.brukerprofilV3 = brukerprofilV3;
    }

    public Pasient hentNavnByFnr(String fnr) {
        if (!fnr.matches("\\d{11}$")) {
            throw new RuntimeException();
        }
        try {
            WSPerson wsPerson = brukerprofilV3.hentKontaktinformasjonOgPreferanser(new WSHentKontaktinformasjonOgPreferanserRequest()
                    .withIdent(new WSNorskIdent()
                            .withType(new WSPersonidenter()
                                    .withKodeRef("http://nav.no/kodeverk/Term/Personidenter/FNR/nb/F_c3_b8dselnummer?v=1")
                                    .withValue("FNR")
                            )
                            .withIdent(fnr))).getBruker();
            return new Pasient()
                    .fnr(fnr)
                    .fornavn(wsPerson.getPersonnavn().getFornavn())
                    .mellomnavn(wsPerson.getPersonnavn().getMellomnavn())
                    .etternavn(wsPerson.getPersonnavn().getEtternavn());
        } catch (HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt e) {
            log.error("HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt for {} med FNR {}",
                    SubjectHandler.getIdent().orElse("-"), fnr);
            throw new RuntimeException();
        } catch (HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning e) {
            log.error("Sikkerhetsbegrensning for {} med FNR {}",
                    SubjectHandler.getIdent().orElse("-"), fnr);
            throw new ForbiddenException();
        } catch (HentKontaktinformasjonOgPreferanserPersonIkkeFunnet e) {
            log.error("HentKontaktinformasjonOgPreferanserPersonIkkeFunnet for {} med FNR {}",
                    SubjectHandler.getIdent().orElse("-"), fnr);
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            log.error("{} fikk RuntimeException mot TPS med ved oppslag av {}",
                    SubjectHandler.getIdent().orElse("-"), fnr, e);
            throw e;
        }
    }
}
