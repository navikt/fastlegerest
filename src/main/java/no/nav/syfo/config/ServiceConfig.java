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
    public TilgangService tilgangService() {
        return new TilgangService();
    }

    @Bean
    public PartnerService partnerService() {
        return new PartnerService();
    }

    @Bean
    public DialogService dialogService() {
        return new DialogService();
    }
}

