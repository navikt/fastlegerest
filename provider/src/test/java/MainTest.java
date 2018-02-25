import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static no.nav.brukerdialog.security.context.CustomizableSubjectHandler.setInternSsoToken;
import static no.nav.brukerdialog.security.context.CustomizableSubjectHandler.setUid;
import static no.nav.brukerdialog.tools.ISSOProvider.getIDToken;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;
import static no.nav.testconfig.ApiAppTest.setupTestContext;

public class MainTest {
    public static void main(String[] args) throws Exception {
        setupTestContext();
        setFrom("environment.properties");
        getenv().forEach(System::setProperty);
        setUid(getProperty("veileder.username"));
        setInternSsoToken(getIDToken());
        Main.main("8585");
    }
}
