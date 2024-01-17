package no.nav.syfo.fastlege.domain

data class Behandler(
    val aktiv: Boolean,
    val fornavn: String,
    val mellomnavn: String?,
    val etternavn: String,
    val personIdent: String?,
    val herId: Int,
    val hprId: String?,
)
