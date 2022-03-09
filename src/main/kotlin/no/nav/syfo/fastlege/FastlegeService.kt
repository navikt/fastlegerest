package no.nav.syfo.fastlege

import no.nav.syfo.consumer.fastlege.FastlegeConsumer
import no.nav.syfo.consumer.fastlege.toFastlege
import no.nav.syfo.consumer.pdl.PdlConsumer
import no.nav.syfo.consumer.pdl.PdlHentPerson
import no.nav.syfo.fastlege.domain.*
import no.nav.syfo.util.PersonIdent
import no.nav.syfo.util.lowerCapitalize
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class FastlegeService @Inject constructor(
    private val pdlConsumer: PdlConsumer,
    private val fastlegeConsumer: FastlegeConsumer
) {
    @Cacheable(value = ["fastlege"])
    fun hentBrukersFastlege(personIdent: PersonIdent): Fastlege? {
        return hentBrukersFastleger(personIdent).aktiv()
    }

    @Cacheable(value = ["fastlege"])
    fun hentBrukersFastleger(personIdent: PersonIdent): List<Fastlege> {
        return try {
            val maybePerson = pdlConsumer.person(personIdent)
            val pasient = toPasient(personIdent, maybePerson)
            fastlegeConsumer.getFastleger(personIdent).map { fastlege ->
                fastlege.toFastlege(
                    pasient = Pasient(
                        fnr = personIdent.value,
                        fornavn = pasient?.fornavn ?: "",
                        mellomnavn = pasient?.mellomnavn,
                        etternavn = pasient?.etternavn ?: "",
                    ),
                    foreldreEnhetHerId = hentForeldreEnhetHerId(fastlege.herId),
                )
            }
        } catch (e: RuntimeException) {
            log.error("Søkte opp og fikk en feil fra fastlegetjenesten fordi tjenesten er nede", e)
            throw e
        }
    }

    private fun toPasient(
        personIdent: PersonIdent,
        maybePerson: PdlHentPerson?,
    ): Pasient? {
        return maybePerson?.hentPerson?.let { pdlPerson ->
            pdlPerson.navn.firstOrNull()?.let { pdlPersonNavn ->
                Pasient(
                    fnr = personIdent.value,
                    fornavn = pdlPersonNavn.fornavn.lowerCapitalize(),
                    mellomnavn = pdlPersonNavn.mellomnavn?.lowerCapitalize(),
                    etternavn = pdlPersonNavn.etternavn.lowerCapitalize(),
                )
            }
        }
    }

    private fun hentForeldreEnhetHerId(fastlegeHerId: Int?): Int? {
        return fastlegeHerId?.let { herId ->
            fastlegeConsumer.getPraksisInfo(herId)?.foreldreEnhetHerId
        }
    }

    private fun List<Fastlege>.aktiv(): Fastlege? {
        return this.filter { fastlege ->
            fastlege.relasjon.kodeVerdi == RelasjonKodeVerdi.FASTLEGE.kodeVerdi
        }.maxByOrNull { fastlege ->
            fastlege.gyldighet.fom
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(FastlegeService::class.java)
    }
}
