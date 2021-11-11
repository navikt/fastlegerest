package no.nav.syfo.fastlege

import no.nav.syfo.consumer.fastlege.FastlegeConsumer
import no.nav.syfo.consumer.fastlege.toFastlege
import no.nav.syfo.consumer.pdl.PdlConsumer
import no.nav.syfo.consumer.pdl.PdlHentPerson
import no.nav.syfo.fastlege.domain.Fastlege
import no.nav.syfo.fastlege.domain.Pasient
import no.nav.syfo.util.lowerCapitalize
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.inject.Inject

@Service
class FastlegeService @Inject constructor(
    private val pdlConsumer: PdlConsumer,
    private val fastlegeConsumer: FastlegeConsumer
) {
    @Cacheable(value = ["fastlege"])
    fun hentBrukersFastlege(brukersFnr: String): Fastlege? {
        return hentBrukersFastleger(brukersFnr).firstOrNull {
            it.isAktiv()
        }
    }

    @Cacheable(value = ["fastlege"])
    fun hentBrukersFastleger(brukersFnr: String): List<Fastlege> {
        return try {
            val maybePerson = pdlConsumer.person(brukersFnr)
            val pasient = toPasient(maybePerson)
            fastlegeConsumer.getFastleger(brukersFnr).map { fastlege ->
                fastlege.toFastlege(
                    pasient = Pasient(
                        fnr = brukersFnr,
                        fornavn = pasient.fornavn,
                        mellomnavn = pasient.mellomnavn,
                        etternavn = pasient.etternavn,
                    ),
                    foreldreEnhetHerId = hentForeldreEnhetHerId(fastlege.herId),
                )
            }
        } catch (e: RuntimeException) {
            log.error("Søkte opp og fikk en feil fra fastlegetjenesten fordi tjenesten er nede", e)
            throw e
        }
    }

    private fun toPasient(maybePerson: PdlHentPerson?): Pasient {
        return maybePerson?.hentPerson?.let { pdlPerson ->
            pdlPerson.navn.firstOrNull()?.let { pdlPersonNavn ->
                Pasient(
                    fornavn = pdlPersonNavn.fornavn.lowerCapitalize(),
                    mellomnavn = pdlPersonNavn.mellomnavn?.lowerCapitalize(),
                    etternavn = pdlPersonNavn.etternavn.lowerCapitalize(),
                )
            } ?: Pasient()
        } ?: Pasient()
    }

    private fun hentForeldreEnhetHerId(fastlegeHerId: Int?): Int? {
        return fastlegeHerId?.let { herId ->
            fastlegeConsumer.getPraksisInfo(herId)?.foreldreEnhetHerId
        }
    }

    private fun Fastlege.isAktiv(): Boolean {
        return this.pasientforhold.fom.isBefore(LocalDate.now()) && this.pasientforhold.tom.isAfter(
            LocalDate.now()
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(FastlegeService::class.java)
    }
}
