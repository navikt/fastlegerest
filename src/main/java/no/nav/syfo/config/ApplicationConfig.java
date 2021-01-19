package no.nav.syfo.config;

import no.nav.syfo.exception.RestTemplateErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.*;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import static java.util.Arrays.asList;


@Configuration
@Import({
        AdresseregisterConfig.class,
        FastlegeInformasjonConfig.class,
})
@Profile("remote")
public class ApplicationConfig {

    @Bean(name = "Oidc")
    public RestTemplate restTemplate(ClientHttpRequestInterceptor... interceptors) {
        RestTemplate template = new RestTemplate();
        template.setInterceptors(asList(interceptors));
        template.setErrorHandler(new RestTemplateErrorHandler());
        return template;
    }

    @Bean(name = "BasicAuth")
    public RestTemplate basicAuthRestTemplate(@Value("${srv.username}") String username,
                                              @Value("${srv.password}") String password) {
        return new RestTemplateBuilder()
                .basicAuthentication(username, password)
                .build();
    }

    @Bean(name = "default")
    public RestTemplate defaultRestTemplate() {
        return new RestTemplate();
    }
}
