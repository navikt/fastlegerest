package no.nav.syfo.config;

import no.nav.syfo.services.*;
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

    @Bean
    public DiskresjonskodeService diskresjonskodeService() {
        return new DiskresjonskodeService();
    }

    @Bean
    public EgenAnsattService egenAnsattService() {
        return new EgenAnsattService();
    }

    @Bean
    public TilgangService tilgangService() {
        return new TilgangService();
    }
}

