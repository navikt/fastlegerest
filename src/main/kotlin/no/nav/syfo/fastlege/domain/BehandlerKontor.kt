package no.nav.syfo.fastlege.domain

data class BehandlerKontor(
    val aktiv: Boolean,
    val herId: Int,
    val navn: String,
    val besoksadresse: Adresse?,
    val postadresse: Adresse?,
    val telefon: String?,
    val epost: String?,
    val orgnummer: String?,
    val behandlere: List<Behandler>,
)
