package no.nav.syfo.client.tilgangskontroll

import java.io.Serializable

data class Tilgang(
    val harTilgang: Boolean = false,
) : Serializable
