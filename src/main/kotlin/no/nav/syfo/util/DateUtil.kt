package no.nav.syfo.util

import java.time.LocalDate

fun LocalDate.isBeforeOrEqual(anotherDate: LocalDate) =
    this == anotherDate || this.isBefore(anotherDate)

fun LocalDate.isAfterOrEqual(anotherDate: LocalDate) =
    this == anotherDate || this.isAfter(anotherDate)