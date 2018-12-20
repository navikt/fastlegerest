package no.nav.syfo.util;

import no.nav.security.oidc.OIDCConstants;
import no.nav.security.oidc.context.*;

import javax.ws.rs.ForbiddenException;


public class OIDCUtil {

    public static String tokenFraOIDC(OIDCRequestContextHolder contextHolder, String issuer) {
        OIDCValidationContext context = (OIDCValidationContext) contextHolder
                .getRequestAttribute(OIDCConstants.OIDC_VALIDATION_CONTEXT);

        TokenContext tokenContext = context.getToken(issuer);
        if (tokenContext == null){
            throw new ForbiddenException("Finner ikke token.");
        }
        return tokenContext.getIdToken();
    }

}
