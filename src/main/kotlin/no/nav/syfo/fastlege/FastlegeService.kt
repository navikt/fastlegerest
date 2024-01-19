package no.nav.syfo.fastlege

import no.nav.syfo.application.cache.RedisStore
import no.nav.syfo.client.pdl.PdlClient
import no.nav.syfo.client.pdl.PdlHentPerson
import no.nav.syfo.fastlege.domain.*
import no.nav.syfo.fastlege.ws.adresseregister.AdresseregisterClient
import no.nav.syfo.fastlege.ws.fastlegeregister.FastlegeInformasjonClient
import no.nav.syfo.util.*
import org.slf4j.LoggerFactory
import java.time.LocalDate

class FastlegeService(
    private val pdlClient: PdlClient,
    private val fastlegeClient: FastlegeInformasjonClient,
    private val adresseregisterClient: AdresseregisterClient,
    private val cache: RedisStore,
) {
    suspend fun hentBrukersFastleger(
        personIdent: PersonIdent,
    ): List<Fastlege> =
        try {
            val cacheKey = "fastleger-$personIdent"
            val cachedValue = cache.getListObject<Fastlege>(cacheKey)
            if (cachedValue != null) {
                COUNT_CALL_FASTLEGER_CACHE_HIT.increment()
                cachedValue
            } else {
                val maybePerson = pdlClient.person(personIdent)
                val pasient = toPasient(personIdent, maybePerson)
                fastlegeClient.hentBrukersFastleger(personIdent).map { fastlege ->
                    fastlege.copy(
                        pasient = Pasient(
                            fnr = personIdent.value,
                            fornavn = pasient?.fornavn ?: "",
                            mellomnavn = pasient?.mellomnavn,
                            etternavn = pasient?.etternavn ?: "",
                        ),
                        foreldreEnhetHerId = hentForeldreEnhetHerId(fastlege.herId),
                    )
                }.also {
                    COUNT_CALL_FASTLEGER_CACHE_MISS.increment()
                    cache.setObject(cacheKey, it, 24 * 3600)
                }
            }
        } catch (e: RuntimeException) {
            log.error("Søkte opp og fikk en feil fra fastlegetjenesten fordi tjenesten er nede", e)
            throw e
        }

    suspend fun hentBrukersFastlege(
        personIdent: PersonIdent,
    ): Fastlege? {
        return hentBrukersFastleger(
            personIdent = personIdent,
        ).aktiv()
    }

    suspend fun hentBrukersFastlegevikar(
        personIdent: PersonIdent,
    ): Fastlege? {
        return hentBrukersFastleger(
            personIdent = personIdent,
        ).vikar()
    }

    fun hentBehandlereForKontor(
        kontorHerId: Int,
    ): BehandlerKontor? =
        adresseregisterClient.hentBehandlereForKontor(kontorHerId)

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

    private fun hentForeldreEnhetHerId(
        fastlegeHerId: Int?,
    ): Int? {
        return fastlegeHerId?.let { herId ->
            adresseregisterClient.hentPraksisInfoForFastlege(
                herId = herId,
            )?.foreldreEnhetHerId
        }
    }

    private fun List<Fastlege>.aktiv(): Fastlege? {
        return this.filter { fastlege ->
            fastlege.relasjon.kodeVerdi == RelasjonKodeVerdi.FASTLEGE.kodeVerdi
        }.maxByOrNull { fastlege ->
            fastlege.gyldighet.fom
        }
    }

    private fun List<Fastlege>.vikar(): Fastlege? {
        val today = LocalDate.now()
        return this.filter { fastlege ->
            fastlege.relasjon.kodeVerdi == RelasjonKodeVerdi.VIKAR.kodeVerdi
        }.filter { vikar ->
            vikar.gyldighet.fom.isBeforeOrEqual(today) && vikar.gyldighet.tom.isAfterOrEqual(today)
        }.maxByOrNull { vikar ->
            vikar.gyldighet.fom
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(FastlegeService::class.java)
    }
}
