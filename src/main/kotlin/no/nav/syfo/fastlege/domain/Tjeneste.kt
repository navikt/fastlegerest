package no.nav.syfo.fastlege.domain

data class Tjeneste(
    val aktiv: Boolean,
    val herId: Int,
    val navn: String,
    val visningsnavn: String,
)
