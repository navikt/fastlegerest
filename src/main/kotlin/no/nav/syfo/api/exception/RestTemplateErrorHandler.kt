package no.nav.syfo.api.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.Series
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseErrorHandler
import java.io.IOException
import java.net.URI

class RestTemplateErrorHandler : ResponseErrorHandler {
    @Throws(IOException::class)
    override fun hasError(httpResponse: ClientHttpResponse): Boolean {
        return when (httpResponse.statusCode.series()) {
            Series.SUCCESSFUL -> false
            Series.CLIENT_ERROR -> httpResponse.statusCode != HttpStatus.FORBIDDEN
            else -> true
        }
    }

    @Throws(IOException::class)
    override fun handleError(httpResponse: ClientHttpResponse) {
    }

    @Throws(IOException::class)
    override fun handleError(url: URI, method: HttpMethod, httpResponse: ClientHttpResponse) {
        val requestUrlWithHiddenFnr = hideFnrFromUrl(url.toString())
        if (httpResponse.statusCode.series() == Series.SERVER_ERROR) {
            log.error("Fikk server error ved {}-kall til {}. statusCode: {}, statusText: {}, body: {}", method, requestUrlWithHiddenFnr, httpResponse.statusCode, httpResponse.statusText, httpResponse.body)
            throw RuntimeException("Fikk en server-error ved $method-kall til $requestUrlWithHiddenFnr")
        } else if (httpResponse.statusCode.series() == Series.CLIENT_ERROR) {
            log.error("Fikk en client error ved {}-kall til {}, som ikke er forbidden. statusCode: {}, statusText: {}, body: {}", method, requestUrlWithHiddenFnr, httpResponse.statusCode, httpResponse.statusText, httpResponse.body)
            throw RuntimeException("Noe gikk galt ved $method-kall til $requestUrlWithHiddenFnr")
        }
    }

    private fun hideFnrFromUrl(url: String): String {
        return url.replace("\\d".toRegex(), "*")
    }

    companion object {
        private val log = LoggerFactory.getLogger(RestTemplateErrorHandler::class.java)
    }
}
