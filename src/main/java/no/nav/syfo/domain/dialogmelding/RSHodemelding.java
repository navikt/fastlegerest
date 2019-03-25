package no.nav.syfo.domain.dialogmelding;

import lombok.Getter;
import no.nav.syfo.domain.*;
import no.nav.syfo.domain.oppfolgingsplan.RSOppfolgingsplan;

import static java.util.Optional.ofNullable;

@Getter
public class RSHodemelding {
    private RSMeldingInfo meldingInfo;
    private RSVedlegg vedlegg;

    public RSHodemelding(Fastlege fastlege, Partnerinformasjon partnerinformasjon, RSOppfolgingsplan oppfolgingsplan){
        this.meldingInfo =  tilMeldingInfo(
                tilMottaker(fastlege, partnerinformasjon),
                tilPasient(fastlege.pasient()));
        this.vedlegg = tilVedlegg(oppfolgingsplan);
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
                ofNullable(fastlege.fastlegekontor().postadresse()).map(Adresse::adresse).orElse(null),
                ofNullable(fastlege.fastlegekontor().postadresse()).map(Adresse::postnummer).orElse(null),
                ofNullable(fastlege.fastlegekontor().postadresse()).map(Adresse::poststed).orElse(null),
                tilBehandler(fastlege));
    }
}
