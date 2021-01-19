package testhelper

import no.nav.syfo.consumer.pdl.PdlHentPerson
import no.nav.syfo.consumer.pdl.PdlPerson
import no.nav.syfo.consumer.pdl.PdlPersonNavn

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
