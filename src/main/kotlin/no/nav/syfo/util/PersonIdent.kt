package no.nav.syfo.util

data class PersonIdent(val value: String) {
    private val elevenDigits = Regex("^\\d{11}\$")

    init {
        if (!elevenDigits.matches(value)) {
            throw IllegalArgumentException("Value is not a valid PersonIdentNumber")
        }
    }
}
