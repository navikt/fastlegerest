package no.nav.syfo.config;

import no.nav.security.spring.oidc.test.TokenGeneratorConfiguration;
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
        PartnerEmottakConfig.class,
        TokenGeneratorConfiguration.class
})
@Profile("local")
public class LocalApplicationConfig {

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestInterceptor... interceptors) {
        RestTemplate template = new RestTemplate();
        template.setInterceptors(asList(interceptors));
        return template;
    }

}