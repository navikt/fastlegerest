package no.nav.syfo.testhelper

import no.nav.syfo.client.pdl.PdlHentPerson
import no.nav.syfo.client.pdl.PdlPerson
import no.nav.syfo.client.pdl.PdlPersonNavn

fun generatePdlPersonNavn(): PdlPersonNavn {
    return PdlPersonNavn(
        fornavn = UserConstants.ARBEIDSTAKER_NAME_FIRST,
        mellomnavn = UserConstants.ARBEIDSTAKER_NAME_MIDDLE,
        etternavn = UserConstants.ARBEIDSTAKER_NAME_LAST
    )
}

fun generatePdlHentPerson(
    pdlPersonNavn: PdlPersonNavn?
): PdlHentPerson {
    return PdlHentPerson(
        hentPerson = PdlPerson(
            navn = listOf(
                pdlPersonNavn ?: generatePdlPersonNavn()
            )
        )
    )
}
