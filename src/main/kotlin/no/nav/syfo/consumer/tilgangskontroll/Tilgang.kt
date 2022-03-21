package no.nav.syfo.consumer.tilgangskontroll

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
data class Tilgang(
    val harTilgang: Boolean = false,
) : Serializable
