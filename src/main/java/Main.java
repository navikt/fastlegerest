import no.nav.syfo.config.ApplicationConfig;

import static java.lang.System.*;
import static no.nav.apiapp.ApiApp.startApp;

public class Main {
    public static void main(String... args) throws Exception {
        getenv().forEach(System::setProperty);
        setProperty("OIDC_REDIRECT_URL", getProperty("VEILARBLOGIN_REDIRECT_URL_URL"));
        startApp(ApplicationConfig.class, args);
    }
}