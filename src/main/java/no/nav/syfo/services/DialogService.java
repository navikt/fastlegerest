package no.nav.syfo.services;

import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider;
import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.domain.OrganisasjonPerson;
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
import java.util.List;
import java.util.Optional;

import static java.lang.System.getProperty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
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
    @Inject
    private AdresseregisterService adresseregisterService;

    public void sendOppfolgingsplan(final RSOppfolgingsplan oppfolgingsplan) {
        Fastlege fastlege = fastlegeService.hentBrukersFastlege(oppfolgingsplan.getSykmeldtFnr())
                .orElseThrow(() -> new FastlegeIkkeFunnet("Fant ikke aktiv fastlege"));

        Partnerinformasjon partnerinformasjon = getPartnerinformasjon(fastlege);

        RSHodemelding hodemelding =
                tilHodemelding(
                        tilMeldingInfo(
                                tilMottaker(fastlege, partnerinformasjon),
                                tilPasient(fastlege.pasient())),
                        tilVedlegg(oppfolgingsplan));

        send(hodemelding);
    }

    Partnerinformasjon getPartnerinformasjon(Fastlege fastlege) {
        //TODO: Loggmeldinger med DIALOGMELDING-TRACE skal fjernes når sending til fastlege er verifisert at fungerer

        String orgnummer = fastlege.fastlegekontor().orgnummer();
        Optional<String> fastlegeForeldreEnhetHerId = of(fastlege)
                .map(Fastlege::herId)
                .map(adresseregisterService::hentFastlegeOrganisasjonPerson)
                .map(OrganisasjonPerson::foreldreEnhetHerId)
                .map(Object::toString);

        LOG.info("DIALOGMELDING-TRACE: Fant fastlegeForeldreEnhetHerId: {}", fastlegeForeldreEnhetHerId.orElse(null));

        List<Partnerinformasjon> partnerinformasjonListe = partnerService.hentPartnerinformasjon(orgnummer);

        LOG.info("DIALOGMELDING-TRACE: Fant {} partnere for orgnummer {}: [{}]",
                partnerinformasjonListe.size(),
                orgnummer,
                partnerinformasjonListe.stream().map(partnerinformasjon -> "(index=" + partnerinformasjonListe.indexOf(partnerinformasjon) + ", partner=" + partnerinformasjon.getPartnerId() + ", her=" + partnerinformasjon.getHerId() + ")").collect(joining(", ")));

        try {
            if (partnerinformasjonListe.isEmpty()) {
                LOG.warn("Fant ikke partnerinformasjon for orgnummer {} fordi partnerregister returnerte tom liste", orgnummer);
                throw new PartnerinformasjonIkkeFunnet("Fant ikke partnerinformasjon for orgnummer " + orgnummer);
            }
            Partnerinformasjon valgtPartnerinformasjon = partnerinformasjonListe
                    .stream()
                    .filter(partner -> partner.getHerId() != null)
                    .filter(partnerinfo -> {
                        boolean harHerIdLikFastlegeForeldreHerID = fastlegeForeldreEnhetHerId.map(partnerinfo.getHerId()::equals).orElse(false);
                        LOG.info("DIALOGMELDING-TRACE: Er Parterinformasjon sin HerId({}) lik Fastlege sin HerId({}): {}",
                                partnerinfo.getHerId(),
                                fastlegeForeldreEnhetHerId.orElse(null),
                                harHerIdLikFastlegeForeldreHerID);
                        return harHerIdLikFastlegeForeldreHerID;
                    })
                    .findFirst()
                    .orElseThrow(() -> {
                        LOG.warn("Fant ikke partnerinformasjon for orgnummer {} fordi HerId ikke var lik fasteleges ForeldreHerId", orgnummer);
                        return new PartnerinformasjonIkkeFunnet("Fant ikke partnerinformasjon for orgnummer " + orgnummer);
                    });

            LOG.info("DIALOGMELDING-TRACE: Av {} mulige partnere er index {} valgt",
                    partnerinformasjonListe.size(),
                    partnerinformasjonListe.indexOf(valgtPartnerinformasjon));

            return valgtPartnerinformasjon;
        } catch (PartnerinformasjonIkkeFunnet e) {
            LOG.info("DIALOGMELDING-TRACE: Fant {} partnere, men ingen ble valgt", partnerinformasjonListe.size());
            LOG.info("DIALOGMELDING-TRACE: Partnerinformasjon.herid: {} - fastlegeForeldreEnhetHerId: {}",
                    partnerinformasjonListe.stream().map(Partnerinformasjon::getHerId).collect(toList()),
                    fastlegeForeldreEnhetHerId.orElse(null));
            throw e;
        } catch (Exception e) {
            LOG.info("DIALOGMELDING-TRACE: Annen feil oppstått", e);
            throw e;
        }
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
