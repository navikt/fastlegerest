import no.nav.brukerdialog.security.context.InternbrukerSubjectHandler;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import org.apache.geronimo.components.jaspi.AuthConfigFactoryImpl;
import javax.security.auth.message.config.AuthConfigFactory;
import java.security.Security;

import static java.lang.System.setProperty;
import static no.nav.brukerdialog.security.context.InternbrukerSubjectHandler.setServicebruker;
import static no.nav.brukerdialog.security.context.InternbrukerSubjectHandler.setVeilederIdent;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.common.jetty.JettyStarterUtils.*;

public class StartJetty {
    public static void main(String[] args) throws Exception {
        setVeilederIdent("Z990572");
        setServicebruker("srvfastlegerest");
        setProperty("no.nav.brukerdialog.security.context.subjectHandlerImplementationClass", InternbrukerSubjectHandler.class.getName());
        Security.setProperty(AuthConfigFactory.DEFAULT_FACTORY_SECURITY_PROPERTY, AuthConfigFactoryImpl.class.getCanonicalName());

        Jetty jetty = usingWar()
                .at("fastlegerest")
                .port(8585)
                .disableAnnotationScanning()
                .configureForJaspic()
                .overrideWebXml()
                .loadProperties("/environment.properties")
                .buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }
}
