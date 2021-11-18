package no.nav.syfo.fastlege.domain

data class Pasient(
    val fnr: String,
    val fornavn: String,
    val mellomnavn: String? = null,
    val etternavn: String,
)
