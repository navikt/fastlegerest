package no.nav.syfo.fastlege.domain

data class Fastlegekontor(
    val navn: String,
    val besoeksadresse: Adresse?,
    val postadresse: Adresse?,
    val telefon: String,
    val epost: String,
    val orgnummer: String?,
)
