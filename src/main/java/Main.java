import no.nav.syfo.config.ApplicationConfig;

import java.net.InetAddress;

import static java.lang.System.*;
import static no.nav.apiapp.ApiApp.startApp;

public class Main {
    public static void main(String... args) throws Exception {
        getenv().forEach(System::setProperty);
        setProperty("OIDC_REDIRECT_URL", getProperty("VEILARBLOGIN_REDIRECT_URL_URL"));
        setProperty("applicationName", "fastlegerest");
        setProperty("node.hostname", InetAddress.getLocalHost().getHostName());
        setProperty("environment.name", getProperty("FASIT_ENVIRONMENT_NAME"));

        //Workaround for å få med begrunnelse for feil
        setProperty("VIS_DETALJER_VED_FEIL", "true");

        startApp(ApplicationConfig.class, args);
    }
}