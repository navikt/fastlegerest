package no.nav.syfo.fastlege.domain

import java.time.LocalDate

data class Pasientforhold(
    val fom: LocalDate,
    val tom: LocalDate,
)
