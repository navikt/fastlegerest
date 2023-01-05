package no.nav.syfo.fastlege

import no.nav.syfo.application.cache.RedisStore
import no.nav.syfo.client.fastlege.FastlegeClient
import no.nav.syfo.client.fastlege.toFastlege
import no.nav.syfo.client.pdl.PdlClient
import no.nav.syfo.client.pdl.PdlHentPerson
import no.nav.syfo.fastlege.domain.*
import no.nav.syfo.util.PersonIdent
import no.nav.syfo.util.lowerCapitalize
import org.slf4j.LoggerFactory

class FastlegeService(
    private val pdlClient: PdlClient,
    private val fastlegeClient: FastlegeClient,
    private val cache: RedisStore,
) {
    suspend fun hentBrukersFastleger(
        personIdent: PersonIdent,
        callId: String,
    ): List<Fastlege> {
        return try {
            val cacheKey = "fastleger-$personIdent"
            val cachedValue = cache.getListObject<Fastlege>(cacheKey)
            if (cachedValue != null) {
                COUNT_CALL_CALL_FASTLEGER_CACHE_HIT.increment()
                cachedValue
            } else {
                val maybePerson = pdlClient.person(personIdent)
                val pasient = toPasient(personIdent, maybePerson)
                fastlegeClient.getFastleger(personIdent, callId).map { fastlege ->
                    fastlege.toFastlege(
                        pasient = Pasient(
                            fnr = personIdent.value,
                            fornavn = pasient?.fornavn ?: "",
                            mellomnavn = pasient?.mellomnavn,
                            etternavn = pasient?.etternavn ?: "",
                        ),
                        foreldreEnhetHerId = hentForeldreEnhetHerId(fastlege.herId, callId),
                    )
                }.also {
                    COUNT_CALL_CALL_FASTLEGER_CACHE_MISS.increment()
                    cache.setObject(cacheKey, it, 24 * 3600)
                }
            }
        } catch (e: RuntimeException) {
            log.error("Søkte opp og fikk en feil fra fastlegetjenesten fordi tjenesten er nede", e)
            throw e
        }
    }

    suspend fun hentBrukersFastlege(
        personIdent: PersonIdent,
        callId: String,
    ): Fastlege? {
        return hentBrukersFastleger(
            personIdent = personIdent,
            callId = callId,
        ).aktiv()
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

    private suspend fun hentForeldreEnhetHerId(
        fastlegeHerId: Int?,
        callId: String,
    ): Int? {
        return fastlegeHerId?.let { herId ->
            fastlegeClient.getPraksisInfo(
                herId = herId,
                callId = callId,
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

    companion object {
        private val log = LoggerFactory.getLogger(FastlegeService::class.java)
    }
}
