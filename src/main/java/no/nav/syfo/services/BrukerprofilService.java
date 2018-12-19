package no.nav.syfo.services;

import no.nav.common.auth.SubjectHandler;
import no.nav.syfo.domain.Pasient;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.*;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;

import static org.slf4j.LoggerFactory.getLogger;


@Service
public class BrukerprofilService {
    private static final Logger LOG = getLogger(BrukerprofilService.class);
    @Inject
    private BrukerprofilV3 brukerprofilV3;

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
            LOG.error("HentKontaktinformasjonOgPreferanserPersonIdentErUtgaatt for {} med FNR {}",
                    SubjectHandler.getIdent().orElse("-"), fnr);
            throw new RuntimeException();
        } catch (HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning e) {
            LOG.error("Sikkerhetsbegrensning for {} med FNR {}",
                    SubjectHandler.getIdent().orElse("-"), fnr);
            throw new ForbiddenException();
        } catch (HentKontaktinformasjonOgPreferanserPersonIkkeFunnet e) {
            LOG.error("HentKontaktinformasjonOgPreferanserPersonIkkeFunnet for {} med FNR {}",
                    SubjectHandler.getIdent().orElse("-"), fnr);
            throw new ForbiddenException();
        } catch (RuntimeException e) {
            LOG.error("{} fikk RuntimeException mot TPS med ved oppslag av {}",
                    SubjectHandler.getIdent().orElse("-"), fnr, e);
            throw e;
        }
    }
}
