package no.nav.syfo.services;

import no.nav.syfo.domain.OrganisasjonPerson;
import no.nav.syfo.services.exceptions.OrganisasjonPersonInformasjonIkkeFunnet;
import no.nhn.register.communicationparty.ICommunicationPartyService;
import no.nhn.register.communicationparty.ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage;
import no.nhn.register.communicationparty.WSOrganizationPerson;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;

import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static org.slf4j.LoggerFactory.getLogger;

public class AdresseregisterService {
    private static final Logger LOG = getLogger(AdresseregisterService.class);

    @Inject
    private ICommunicationPartyService adresseregisterSoapClient;

    @Cacheable(value = "fastlegeOrganisasjon", keyGenerator = "userkeygenerator")
    public OrganisasjonPerson hentFastlegeOrganisasjonPerson(Integer herId) {
        try {
            WSOrganizationPerson wsOrganizationPerson = adresseregisterSoapClient.getOrganizationPersonDetails(herId);
            return wsOrganizationPerson.getDepartments().getDepartments()
                    .stream()
                    .filter(wsDepartment -> wsDepartment.getParentHerId().equals(herId))
                    .findFirst()
                    .map(op -> new OrganisasjonPerson(op.getParentHerId()))
                    .orElseThrow(() -> {
                        LOG.warn("Fant ikke organisasjon for fastlege med HerId {}", herId);
                        return new OrganisasjonPersonInformasjonIkkeFunnet("Fant ikke organisasjon for fastlege med HerId " + herId);
                    });
        } catch (ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage e) {
            LOG.error("{} Søkte opp fastlege med HerId {} og fikk en feil fra adresseregister fordi fastlegen mangler HerId", getSubjectHandler().getUid(), herId, e);
            throw new OrganisasjonPersonInformasjonIkkeFunnet("Fant ikke partnerinformasjon for orgnummer " + herId);
        } catch (RuntimeException e) {
            LOG.error("{} Søkte opp fastlege med HerId {} og fikk en uventet feil fra adresseregister fordi tjenesten er nede", getSubjectHandler().getUid(), herId, e);
            throw e;
        }
    }
}
