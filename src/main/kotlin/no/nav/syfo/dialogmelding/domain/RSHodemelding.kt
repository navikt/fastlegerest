package no.nav.syfo.dialogmelding.domain

import no.nav.syfo.dialogmelding.api.RSOppfolgingsplan
import no.nav.syfo.fastlege.domain.*

data class RSHodemelding(
    val meldingInfo: RSMeldingInfo,
    val vedlegg: RSVedlegg,
)

fun createRSHodemelding(
    fastlege: Fastlege,
    partnerinformasjon: Partnerinformasjon,
    oppfolgingsplan: RSOppfolgingsplan,
) = RSHodemelding(
    meldingInfo = tilMeldingInfo(
        mottaker = fastlege.tilMottaker(partnerinformasjon),
        pasient = fastlege.pasient.tilPasient(),
    ),
    vedlegg = oppfolgingsplan.tilVedlegg(),
)

private fun tilMeldingInfo(
    mottaker: RSMottaker,
    pasient: RSPasient,
) = RSMeldingInfo(
    mottaker = mottaker,
    pasient = pasient,
)

private fun Pasient?.tilPasient() = RSPasient(
    fnr = this?.fnr,
    fornavn = this?.fornavn,
    mellomnavn = this?.mellomnavn,
    etternavn = this?.etternavn,
)

private fun RSOppfolgingsplan.tilVedlegg() = RSVedlegg(this.oppfolgingsplanPdf)

private fun Fastlege.tilMottaker(
    partnerinformasjon: Partnerinformasjon,
) = RSMottaker(
    partnerId = partnerinformasjon.partnerId.toString(),
    herId = partnerinformasjon.herId.toString(),
    orgnummer = this.fastlegekontor.orgnummer,
    navn = this.fastlegekontor.navn,
    adresse = this.fastlegekontor.postadresse?.adresse,
    postnummer = this.fastlegekontor.postadresse?.postnummer,
    poststed = this.fastlegekontor.postadresse?.poststed,
    behandler = this.tilBehandler(),
)

private fun Fastlege.tilBehandler() = RSBehandler(
    this.fnr,
    this.helsepersonellregisterId,
    this.fornavn,
    this.mellomnavn,
    this.etternavn,
)
