package no.nav.syfo.consumer.pdl

import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer
import no.nav.syfo.metric.Metrikk
import no.nav.syfo.util.ALLE_TEMA_HEADERVERDI
import no.nav.syfo.util.TEMA_HEADER
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
class PdlConsumer(
    private val metric: Metrikk,
    private val azureAdV2TokenConsumer: AzureAdV2TokenConsumer,
    @Value("\${pdl.client.id}") private val pdlClientId: String,
    @Value("\${pdl.url}") private val pdlUrl: String,
    @Qualifier("default") private val defaultRestTemplate: RestTemplate
) {
    fun person(personIdentNumber: String): PdlHentPerson? {
        metric.countEvent("call_pdl")

        val query = this::class.java.getResource("/pdl/hentPerson.graphql").readText().replace("[\n\r]", "")
        val entity = createRequestEntity(PdlRequest(query, Variables(personIdentNumber)))
        try {
            val pdlPerson = defaultRestTemplate.exchange(
                pdlUrl,
                HttpMethod.POST,
                entity,
                PdlPersonResponse::class.java
            )

            val pdlPersonReponse = pdlPerson.body!!
            return if (pdlPersonReponse.errors != null && pdlPersonReponse.errors.isNotEmpty()) {
                metric.countEvent(CALL_PDL_PERSON_FAIL)
                pdlPersonReponse.errors.forEach {
                    LOG.error("Error while requesting person from PersonDataLosningen: ${it.errorMessage()}")
                }
                null
            } else {
                metric.countEvent(CALL_PDL_PERSON_SUCCESS)
                pdlPersonReponse.data
            }
        } catch (exception: RestClientException) {
            metric.countEvent(CALL_PDL_PERSON_FAIL)
            LOG.error("Error from PDL with request-url: $pdlUrl", exception)
            throw exception
        }
    }

    private fun createRequestEntity(request: PdlRequest): HttpEntity<PdlRequest> {
        val azureADSystemToken = azureAdV2TokenConsumer.getToken(pdlClientId)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
        headers.setBearerAuth(azureADSystemToken)
        return HttpEntity(request, headers)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(PdlConsumer::class.java)

        private const val CALL_PDL_BASE = "call_pdl"
        const val CALL_PDL_PERSON_FAIL = "${CALL_PDL_BASE}_fail"
        const val CALL_PDL_PERSON_SUCCESS = "${CALL_PDL_BASE}_success"
    }
}
