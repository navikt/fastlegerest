package no.nav.syfo.services;

import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider;
import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.domain.Partnerinformasjon;
import no.nav.syfo.domain.Pasient;
import no.nav.syfo.domain.dialogmelding.*;
import no.nav.syfo.domain.oppfolgingsplan.RSOppfolgingsplan;
import no.nav.syfo.services.exceptions.FastlegeIkkeFunnet;
import no.nav.syfo.services.exceptions.PartnerinformasjonIkkeFunnet;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.lang.System.getProperty;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.slf4j.LoggerFactory.getLogger;

public class DialogService {
    private static final Logger LOG = getLogger(DialogService.class);

    private static final String HOST = "local".equalsIgnoreCase(
            getProperty("FASIT_ENVIRONMENT_NAME")) ? "localhost:8080" : "dialogfordeler";

    private Client client = newClient();
    private SystemUserTokenProvider systemUserTokenProvider = new SystemUserTokenProvider();

    @Inject
    private FastlegeService fastlegeService;
    @Inject
    private PartnerService partnerService;

    public void sendOppfolgingsplan(final RSOppfolgingsplan oppfolgingsplan) {
        Fastlege fastlege = fastlegeService.hentBrukersFastlege(oppfolgingsplan.getSykmeldtFnr())
                .orElseThrow(() -> new FastlegeIkkeFunnet("Fant ikke aktiv fastlege"));
        String orgnummer = fastlege.fastlegekontor().orgnummer();

        Partnerinformasjon partnerinformasjon = partnerService.hentPartnerinformasjon(orgnummer)
                .stream()
                .findFirst()
                .orElseThrow(() -> {
                    LOG.warn("Fant ikke partnerinformasjon for orgnummer {}", orgnummer);
                    return new PartnerinformasjonIkkeFunnet("Fant ikke partnerinformasjon for orgnummer " + orgnummer);
                });

        RSHodemelding hodemelding =
                tilHodemelding(
                        tilMeldingInfo(
                                tilMottaker(fastlege, partnerinformasjon),
                                tilPasient(fastlege.pasient())),
                        tilVedlegg(oppfolgingsplan));

        send(hodemelding);
    }

    private RSHodemelding tilHodemelding(RSMeldingInfo meldingInfo, RSVedlegg vedlegg) {
        return new RSHodemelding(meldingInfo, vedlegg);
    }

    private RSMeldingInfo tilMeldingInfo(RSMottaker mottaker, RSPasient pasient) {
        return new RSMeldingInfo(mottaker, pasient);
    }

    private RSPasient tilPasient(Pasient pasient) {
        return new RSPasient(
                pasient.fnr(),
                pasient.fornavn(),
                pasient.mellomnavn(),
                pasient.etternavn());
    }

    private RSVedlegg tilVedlegg(RSOppfolgingsplan oppfolgingsplan) {
        return new RSVedlegg(oppfolgingsplan.getOppfolgingsplanPdf());
    }

    private RSBehandler tilBehandler(Fastlege fastlege) {
        return new RSBehandler(
                fastlege.fnr(),
                fastlege.helsepersonellregisterId(),
                fastlege.fornavn(),
                fastlege.mellomnavn(),
                fastlege.etternavn());
    }

    private RSMottaker tilMottaker(Fastlege fastlege, Partnerinformasjon partnerinformasjon) {
        return new RSMottaker(
                partnerinformasjon.getPartnerId(),
                partnerinformasjon.getHerId(),
                fastlege.fastlegekontor().orgnummer(),
                fastlege.fastlegekontor().navn(),
                fastlege.fastlegekontor().postadresse().adresse(),
                fastlege.fastlegekontor().postadresse().postnummer(),
                fastlege.fastlegekontor().postadresse().poststed(),
                tilBehandler(fastlege));
    }

    private void send(RSHodemelding hodemelding) {
        Response response;
        try {
            response = client.target("http://" + HOST + "/api/dialogmelding/sendOppfolgingsplan")
                    .request()
                    .header(AUTHORIZATION, "Bearer " + systemUserTokenProvider.getToken())
                    .post(Entity.entity(hodemelding, MediaType.APPLICATION_JSON_TYPE));
        } catch (Exception e) {
            LOG.error("Feil ved kall til dialogfordeler", e);
            throw e;
        }

        int responsekode = response.getStatus();
        if (responsekode >= 300) {
            LOG.error("Feil ved sending av oppfølgingsdialog til fastlege: Fikk responskode {}", responsekode);
            throw new RuntimeException("Feil ved sending av oppfølgingsdialog til fastlege: Fikk responskode " + responsekode);
        }
    }
}
