package no.nav.syfo.fastlege.domain

data class Relasjon(
    val kodeVerdi: String,
    val kodeTekst: String,
)

enum class RelasjonKodeVerdi(val kodeVerdi: String) {
    FASTLEGE("LPFL"),
    VIKAR("LPVI"),
}
