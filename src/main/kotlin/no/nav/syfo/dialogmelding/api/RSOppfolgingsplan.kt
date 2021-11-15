package no.nav.syfo.dialogmelding.api

data class RSOppfolgingsplan (
    val sykmeldtFnr: String,
    val oppfolgingsplanPdf: ByteArray,
)
