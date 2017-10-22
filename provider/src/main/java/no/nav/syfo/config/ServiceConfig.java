package no.nav.syfo.config;

import no.nav.syfo.services.BrukerprofilService;
import no.nav.syfo.services.FastlegeService;
import no.nav.syfo.services.LdapService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public FastlegeService fastlegeService() {
        return new FastlegeService();
    }

    @Bean
    public BrukerprofilService brukerprofilService() {
        return new BrukerprofilService();
    }

    @Bean
    public LdapService ldapService() {
        return new LdapService();
    }

}

