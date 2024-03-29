package no.nav.syfo.client.fastlege

import no.nav.syfo.fastlege.domain.*

data class FastlegeProxyDTO(
    val fornavn: String,
    val mellomnavn: String,
    val etternavn: String,
    val fnr: String?,
    val herId: Int?,
    val helsepersonellregisterId: Int?,
    val fastlegekontor: Fastlegekontor,
    val pasientforhold: Periode,
    val gyldighet: Periode,
    val relasjon: Relasjon,
    val stillingsprosent: Int?,
)

fun FastlegeProxyDTO.toFastlege(
    foreldreEnhetHerId: Int?,
    pasient: Pasient,
) = Fastlege(
    fornavn = this.fornavn,
    mellomnavn = this.mellomnavn,
    etternavn = this.etternavn,
    fnr = this.fnr,
    herId = this.herId,
    helsepersonellregisterId = this.helsepersonellregisterId,
    fastlegekontor = this.fastlegekontor,
    pasientforhold = this.pasientforhold,
    foreldreEnhetHerId = foreldreEnhetHerId,
    pasient = pasient,
    gyldighet = this.gyldighet,
    relasjon = this.relasjon,
    stillingsprosent = this.stillingsprosent,
)
