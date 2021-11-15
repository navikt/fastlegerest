package no.nav.syfo.dialogmelding.domain

data class RSMottaker(
    val partnerId: String,
    val herId: String,
    val orgnummer: String? = null,
    val navn: String,
    val adresse: String? = null,
    val postnummer: String? = null,
    val poststed: String? = null,
    val behandler: RSBehandler,
)
