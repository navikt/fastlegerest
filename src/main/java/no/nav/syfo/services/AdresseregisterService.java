package no.nav.syfo.services;

import lombok.extern.slf4j.Slf4j;
import no.nav.common.auth.SubjectHandler;
import no.nav.syfo.domain.OrganisasjonPerson;
import no.nav.syfo.services.exceptions.OrganisasjonPersonInformasjonIkkeFunnet;
import no.nhn.register.communicationparty.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
@Slf4j
public class AdresseregisterService {

    private ICommunicationPartyService adresseregisterSoapClient;

    @Inject
    public AdresseregisterService (final ICommunicationPartyService adresseregisterSoapClient){
        this.adresseregisterSoapClient = adresseregisterSoapClient;
    }

    @Cacheable(value = "fastlegeOrganisasjon")
    public OrganisasjonPerson hentFastlegeOrganisasjonPerson(Integer herId) {
        try {
            WSOrganizationPerson wsOrganizationPerson = adresseregisterSoapClient.getOrganizationPersonDetails(herId);
            return new OrganisasjonPerson(wsOrganizationPerson.getParentHerId());
        } catch (ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage e) {
            log.error("{} Søkte opp fastlege med HerId {} og fikk en feil fra adresseregister fordi fastlegen mangler HerId",
                    SubjectHandler.getIdent().orElse("-"), herId, e);
            throw new OrganisasjonPersonInformasjonIkkeFunnet("Fant ikke parentHerId for fastlege med HerId " + herId);
        } catch (RuntimeException e) {
            log.error("{} Søkte opp fastlege med HerId {} og fikk en uventet feil fra adresseregister fordi tjenesten er nede",
                    SubjectHandler.getIdent().orElse("-"), herId, e);
            throw e;
        }
    }
}
