import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.syfo.config.ApplicationConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfigTest extends ApplicationConfig {

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        apiAppConfigurator
                .sts();
    }

    @Override
    public Sone getSone() {
        return null;
    }

}
