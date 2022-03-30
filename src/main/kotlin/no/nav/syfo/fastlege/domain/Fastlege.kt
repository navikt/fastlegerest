package no.nav.syfo.fastlege.domain

data class Fastlege(
    val fornavn: String,
    val mellomnavn: String,
    val etternavn: String,
    val fnr: String?,
    val herId: Int?,
    val helsepersonellregisterId: Int?,
    val fastlegekontor: Fastlegekontor,
    val pasientforhold: Periode,
    val foreldreEnhetHerId: Int? = null,
    val pasient: Pasient? = null,
    val gyldighet: Periode,
    val relasjon: Relasjon,
    val stillingsprosent: Int?,
)
