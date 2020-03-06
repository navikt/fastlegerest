package no.nav.syfo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

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
    public void handleError(ClientHttpResponse httpResponse) throws IOException {}

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse httpResponse) throws IOException {
        String requestUrlWithHiddenFnr = hideFnrFromUrl(url.toString());
        if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            log.error("Fikk server error ved {}-kall til {}. statusCode: {}, statusText: {}, body: {}", method, requestUrlWithHiddenFnr, httpResponse.getStatusCode(), httpResponse.getStatusText(), httpResponse.getBody());
            throw new RuntimeException("Fikk en server-error ved " + method + "-kall til " + requestUrlWithHiddenFnr);
        } else if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            log.error("Fikk en client error ved {}-kall til {}, som ikke er forbidden. statusCode: {}, statusText: {}, body: {}", method, requestUrlWithHiddenFnr, httpResponse.getStatusCode(), httpResponse.getStatusText(), httpResponse.getBody());
            throw new RuntimeException("Noe gikk galt ved " + method + "-kall til " + requestUrlWithHiddenFnr);
        }
    }

    private String hideFnrFromUrl(String url) {
        return url.replaceAll("\\d", "*");
    }
}
