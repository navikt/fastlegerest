package no.nav.syfo;

import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@EnableOIDCTokenValidation(ignore="org.springframework")
@Profile("remote")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}