package testhelper.generator

import no.nav.syfo.consumer.fastlege.FastlegeProxyDTO
import no.nav.syfo.fastlege.domain.*
import testhelper.UserConstants
import java.time.LocalDate

fun generateFastlegeProxyDTO(
    relasjonKodeVerdi: RelasjonKodeVerdi = RelasjonKodeVerdi.FASTLEGE,
) = FastlegeProxyDTO(
    fornavn = "Dana",
    mellomnavn = "Katherine",
    etternavn = "Scully",
    fnr = UserConstants.FASTLEGE_FNR.value,
    herId = 1337,
    helsepersonellregisterId = 1234,
    fastlegekontor = Fastlegekontor(
        navn = "Fastlegens kontor",
        besoeksadresse = null,
        postadresse = null,
        telefon = "",
        epost = "",
        orgnummer = null,
    ),
    pasientforhold = Periode(
        fom = LocalDate.now().minusDays(10),
        tom = LocalDate.now().plusDays(10),
    ),
    gyldighet = Periode(
        fom = LocalDate.now().minusDays(10),
        tom = LocalDate.now().plusDays(10),
    ),
    relasjon = Relasjon(
        kodeTekst = "Fastlege",
        kodeVerdi = relasjonKodeVerdi.kodeVerdi,
    ),
)
