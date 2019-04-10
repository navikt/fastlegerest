package no.nav.syfo.exception;

import lombok.extern.slf4j.Slf4j;
import no.nav.security.spring.oidc.validation.interceptor.OIDCUnauthorizedException;
import no.nav.syfo.metric.Metrikk;
import no.nav.syfo.services.exceptions.FastlegeIkkeFunnet;
import no.nav.syfo.services.exceptions.HarIkkeTilgang;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.ForbiddenException;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    private final static String BAD_REQUEST_MSG = "Vi kunne ikke tolke inndataene";
    public final static String FORBIDDEN_MSG = "Har ikke tilgang";
    private final static String INTERNAL_MSG = "Det skjedde en uventet feil";
    private final static String UNAUTHORIZED_MSG = "Autorisasjonsfeil";

    private Metrikk metrikk;

    @Inject
    public ControllerExceptionHandler(Metrikk metrikk) {
        this.metrikk = metrikk;
    }

    @ExceptionHandler({
            Exception.class,
            ConstraintViolationException.class,
            HarIkkeTilgang.class,
            FastlegeIkkeFunnet.class,
            ForbiddenException.class,
            IllegalArgumentException.class,
            OIDCUnauthorizedException.class,
    })
    public final ResponseEntity<ApiError> handleException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof OIDCUnauthorizedException) {
            OIDCUnauthorizedException notAuthorizedException = (OIDCUnauthorizedException) ex;

            return handleOIDCUnauthorizedException(notAuthorizedException, headers, request);
        } else if (ex instanceof ForbiddenException) {
            ForbiddenException forbiddenException = (ForbiddenException) ex;

            return handleForbiddenException(forbiddenException, headers, request);
        } else if (ex instanceof IllegalArgumentException) {
            IllegalArgumentException illegalArgumentException = (IllegalArgumentException) ex;

            return handleIllegalArgumentException(illegalArgumentException, headers, request);
        } else if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex;

            return handleConstraintViolationException(constraintViolationException, headers, request);
        } else if (ex instanceof FastlegeIkkeFunnet) {
            FastlegeIkkeFunnet notFoundException = (FastlegeIkkeFunnet) ex;

            return handleFastlegeIkkeFunnetException(notFoundException, headers, request);
        } else {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

            log.warn("Fikk RuntimeException i én av controllerene");
            return handleExceptionInternal(ex, new ApiError(status.value(), INTERNAL_MSG), headers, status, request);
        }
    }

    private ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex, HttpHeaders headers, WebRequest request) {
        return handleExceptionInternal(ex, new ApiError(HttpStatus.BAD_REQUEST.value(), BAD_REQUEST_MSG), headers, HttpStatus.BAD_REQUEST, request);
    }

    private ResponseEntity<ApiError> handleForbiddenException(ForbiddenException ex, HttpHeaders headers, WebRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return handleExceptionInternal(ex, new ApiError(status.value(), FORBIDDEN_MSG), headers, status, request);
    }

    private ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex, HttpHeaders headers, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return handleExceptionInternal(ex, new ApiError(status.value(), BAD_REQUEST_MSG), headers, status, request);
    }

    private ResponseEntity<ApiError> handleOIDCUnauthorizedException(OIDCUnauthorizedException ex, HttpHeaders headers, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return handleExceptionInternal(ex, new ApiError(status.value(), UNAUTHORIZED_MSG), headers, status, request);
    }

    private ResponseEntity<ApiError> handleFastlegeIkkeFunnetException(FastlegeIkkeFunnet ex, HttpHeaders headers, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return handleExceptionInternal(ex, new ApiError(status.value(), ex.getMessage()), headers, status, request);
    }

    private ResponseEntity<ApiError> handleExceptionInternal(Exception ex, ApiError body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        metrikk.tellHttpKall(status.value());

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        return new ResponseEntity<>(body, headers, status);
    }
}
