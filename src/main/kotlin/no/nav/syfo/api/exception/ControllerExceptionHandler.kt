package no.nav.syfo.api.exception

import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException
import no.nav.syfo.dialogmelding.exception.InnsendingFeiletException
import no.nav.syfo.dialogmelding.exception.PartnerinformasjonIkkeFunnet
import no.nav.syfo.fastlege.expection.*
import no.nav.syfo.metric.Metrikk
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.util.WebUtils
import javax.inject.Inject
import javax.validation.ConstraintViolationException
import javax.ws.rs.ForbiddenException

@ControllerAdvice
class ControllerExceptionHandler @Inject constructor(
    private val metrikk: Metrikk
) {
    @ExceptionHandler(
        Exception::class,
        ConstraintViolationException::class,
        HarIkkeTilgang::class,
        FastlegeIkkeFunnet::class,
        ForbiddenException::class,
        IllegalArgumentException::class,
        JwtTokenUnauthorizedException::class,
        PartnerinformasjonIkkeFunnet::class,
        InnsendingFeiletException::class
    )
    fun handleException(ex: Exception, request: WebRequest): ResponseEntity<ApiError> {
        val headers = HttpHeaders()
        return when (ex) {
            is JwtTokenUnauthorizedException -> {
                handleJwtTokenUnauthorizedException(ex, headers, request)
            }
            is ForbiddenException -> {
                handleForbiddenException(ex, headers, request)
            }
            is IllegalArgumentException -> {
                handleIllegalArgumentException(ex, headers, request)
            }
            is ConstraintViolationException -> {
                handleConstraintViolationException(ex, headers, request)
            }
            is FastlegeIkkeFunnet -> {
                handleFastlegeIkkeFunnetException(ex, headers, request)
            }
            is PartnerinformasjonIkkeFunnet -> {
                handlePartnerinformasjonIkkeFunnetException(ex, headers, request)
            }
            is InnsendingFeiletException -> {
                handleInnsendingFeiletException(ex, headers, request)
            }
            else -> {
                val status = HttpStatus.INTERNAL_SERVER_ERROR
                handleExceptionInternal(ex, ApiError(status.value(), INTERNAL_MSG), headers, status, request)
            }
        }
    }

    private fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        headers: HttpHeaders,
        request: WebRequest
    ): ResponseEntity<ApiError> {
        return handleExceptionInternal(ex, ApiError(HttpStatus.BAD_REQUEST.value(), BAD_REQUEST_MSG), headers, HttpStatus.BAD_REQUEST, request)
    }

    private fun handleForbiddenException(
        ex: ForbiddenException,
        headers: HttpHeaders,
        request: WebRequest
    ): ResponseEntity<ApiError> {
        val status = HttpStatus.FORBIDDEN
        return handleExceptionInternal(ex, ApiError(status.value(), ex.message!!), headers, status, request)
    }

    private fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        headers: HttpHeaders,
        request: WebRequest
    ): ResponseEntity<ApiError> {
        val status = HttpStatus.BAD_REQUEST
        return handleExceptionInternal(ex, ApiError(status.value(), BAD_REQUEST_MSG), headers, status, request)
    }

    private fun handleJwtTokenUnauthorizedException(
        ex: JwtTokenUnauthorizedException,
        headers: HttpHeaders,
        request: WebRequest
    ): ResponseEntity<ApiError> {
        val status = HttpStatus.UNAUTHORIZED
        return handleExceptionInternal(ex, ApiError(status.value(), UNAUTHORIZED_MSG), headers, status, request)
    }

    private fun handleFastlegeIkkeFunnetException(
        ex: FastlegeIkkeFunnet,
        headers: HttpHeaders,
        request: WebRequest
    ): ResponseEntity<ApiError> {
        val status = HttpStatus.NOT_FOUND
        return handleExceptionInternal(ex, ApiError(status.value(), ex.message!!), headers, status, request)
    }

    private fun handlePartnerinformasjonIkkeFunnetException(
        ex: PartnerinformasjonIkkeFunnet,
        headers: HttpHeaders,
        request: WebRequest
    ): ResponseEntity<ApiError> {
        val status = HttpStatus.NOT_FOUND
        return handleExceptionInternal(ex, ApiError(status.value(), ex.message!!), headers, status, request)
    }

    private fun handleInnsendingFeiletException(
        ex: InnsendingFeiletException,
        headers: HttpHeaders,
        request: WebRequest
    ): ResponseEntity<ApiError> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        return handleExceptionInternal(ex, ApiError(status.value(), ex.message!!), headers, status, request)
    }

    private fun handleExceptionInternal(
        ex: Exception,
        body: ApiError,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<ApiError> {
        metrikk.tellHttpKall(status.value())
        if (HttpStatus.INTERNAL_SERVER_ERROR == status) {
            log.error("Uventet feil: {} : {}", ex.javaClass.toString(), ex.message, ex)
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST)
        }
        return ResponseEntity(body, headers, status)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ControllerExceptionHandler::class.java)

        private const val BAD_REQUEST_MSG = "Vi kunne ikke tolke inndataene"
        private const val INTERNAL_MSG = "Det skjedde en uventet feil"
        private const val UNAUTHORIZED_MSG = "Autorisasjonsfeil"
    }
}
