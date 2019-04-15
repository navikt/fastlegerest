package no.nav.syfo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Slf4j
public class RestTemplateErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        switch (httpResponse.getStatusCode().series()) {
            case SUCCESSFUL:
                return false;
            case CLIENT_ERROR:
                return httpResponse.getStatusCode() != HttpStatus.FORBIDDEN;
            default:
                return true;
        }
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            log.error("Fikk en server error fra syfo-tilgangskontroll statusCode: {}, statusText: {}, body: {}", httpResponse.getStatusCode(), httpResponse.getStatusText(), httpResponse.getBody());
            throw new RuntimeException("Fikk en server error fra syfo-tilgangskontroll");
        } else if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            log.error("Fikk en client error fra syfo-tilgangskontroll, som ikke er forbidden statusCode: {}, statusText: {}, body: {}", httpResponse.getStatusCode(), httpResponse.getStatusText(), httpResponse.getBody());
            throw new RuntimeException("Noe gikk galt ved henting av tilgang fra syfo-tilgangskontroll");
        }
    }
}
