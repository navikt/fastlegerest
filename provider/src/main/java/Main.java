import no.nav.syfo.config.ApplicationConfig;

import static java.lang.System.getenv;
import static no.nav.apiapp.ApiApp.startApp;

public class Main {
    public static void main(String... args) throws Exception {
        getenv().forEach(System::setProperty);
        startApp(ApplicationConfig.class, args);
    }
}