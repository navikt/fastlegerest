package no.nav.syfo.util

import java.util.*

fun String.lowerCapitalize(): String {
    return this.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}
