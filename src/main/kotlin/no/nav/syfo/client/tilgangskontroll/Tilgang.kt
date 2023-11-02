package no.nav.syfo.client.tilgangskontroll

import java.io.Serializable

data class Tilgang(
    val erGodkjent: Boolean,
) : Serializable
