package no.nav.syfo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.*;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import static java.util.Arrays.asList;


@Configuration
@EnableAspectJAutoProxy
@Import({
        AdresseregisterConfig.class,
        FastlegeInformasjonConfig.class,
        BrukerprofilConfig.class,
        PartnerEmottakConfig.class

})
@Profile("remote")
public class ApplicationConfig{

    @Bean(name = "Oidc")
    public RestTemplate restTemplate(ClientHttpRequestInterceptor... interceptors) {
        RestTemplate template = new RestTemplate();
        template.setInterceptors(asList(interceptors));
        return template;
    }

    @Bean(name = "BasicAuth")
    public RestTemplate basicAuthRestTemplate(@Value("${srvfastlegerest.username}") String username,
    @Value("${srvfastlegerest.password}") String password) {
        return new RestTemplateBuilder()
                .basicAuthorization(username, password)
                .build();
    }
}
