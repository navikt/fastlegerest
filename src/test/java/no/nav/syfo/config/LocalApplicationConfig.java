package no.nav.syfo.config;


import no.nav.syfo.config.caching.CacheConfig;
import org.springframework.context.annotation.*;

@Configuration
@EnableAspectJAutoProxy
@Import({
        AspectConfig.class,
        CacheConfig.class,
        ServiceConfig.class,
        AdresseregisterConfig.class,
        FastlegeInformasjonConfig.class,
        BrukerprofilConfig.class,
        PartnerEmottakConfig.class

})
public class LocalApplicationConfig {
    public LocalApplicationConfig(){

    }
}