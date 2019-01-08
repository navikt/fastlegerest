package no.nav.syfo.services;

import no.nav.emottak.schemas.*;
import no.nav.syfo.domain.*;
import no.nav.syfo.services.exceptions.PartnerinformasjonIkkeFunnet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PartnerServiceTest {

    @Mock
    private AdresseregisterService adresseregisterService;

    @Mock
    private PartnerResource partnerResource;

    @InjectMocks
    private PartnerService partnerService;

    @Before
    public void setUp() {
        mockAdresseRegisterService();
    }

    @Test
    public void getPartnerinformasjonEnMuligPartner() {
        mockPartnerResource(partnerResource);

        Partnerinformasjon partnerinformasjon = partnerService.getPartnerinformasjon(new Fastlege()
                .herId(1)
                .fastlegekontor(new Fastlegekontor()
                        .orgnummer("orgnummer")));

        assertThat(partnerinformasjon)
                .hasFieldOrPropertyWithValue("herId", "11")
                .hasFieldOrPropertyWithValue("partnerId", "partnerId1");
    }

    @Test
    public void getPartnerinformasjonForsteAvToMuligePartnere() {
        mockPartnerResourceListe();

        Partnerinformasjon partnerinformasjon = partnerService.getPartnerinformasjon(new Fastlege()
                .herId(1)
                .fastlegekontor(new Fastlegekontor()
                        .orgnummer("orgnummer")));

        assertThat(partnerinformasjon)
                .hasFieldOrPropertyWithValue("herId", "11")
                .hasFieldOrPropertyWithValue("partnerId", "partnerId1");
    }

    @Test
    public void getPartnerinformasjonAndreAvToMuligePartnere() {
        mockPartnerResourceListe();

        Partnerinformasjon partnerinformasjon = partnerService.getPartnerinformasjon(new Fastlege()
                .herId(2)
                .fastlegekontor(new Fastlegekontor()
                        .orgnummer("orgnummer")));

        assertThat(partnerinformasjon)
                .hasFieldOrPropertyWithValue("herId", "12")
                .hasFieldOrPropertyWithValue("partnerId", "partnerId2");
    }

    @Test(expected = PartnerinformasjonIkkeFunnet.class)
    public void feilHERIDkasterException() {
        mockPartnerResourceListe();

        partnerService.getPartnerinformasjon(new Fastlege()
                .herId(3)
                .fastlegekontor(new Fastlegekontor()
                        .orgnummer("orgnummer")));

    }

    private void mockPartnerResource(PartnerResource partnerResource) {
        HentPartnerIDViaOrgnummerRequest anyRequest = any();
        HentPartnerIDViaOrgnummerResponse partnerInformasjonResponse = new HentPartnerIDViaOrgnummerResponse()
                .withPartnerInformasjon(new WSPartnerInformasjon()
                        .withHERid("11")
                        .withPartnerID("partnerId1"));
        when(partnerResource.hentPartnerIDViaOrgnummer(anyRequest)).thenReturn(partnerInformasjonResponse);
    }

    private void mockAdresseRegisterService() {
        //Sett foreldreHerId til HerId+10
        when(adresseregisterService.hentFastlegeOrganisasjonPerson(anyInt()))
                .thenAnswer(invocation ->
                        new OrganisasjonPerson((Integer) invocation.getArgument(0) + 10));

    }

    private void mockPartnerResourceListe() {
        HentPartnerIDViaOrgnummerRequest anyRequest = any();
        HentPartnerIDViaOrgnummerResponse partnerInformasjonResponse = new HentPartnerIDViaOrgnummerResponse()
                .withPartnerInformasjon(new WSPartnerInformasjon()
                        .withHERid("11")
                        .withPartnerID("partnerId1"))
                .withPartnerInformasjon(new WSPartnerInformasjon()
                        .withHERid("12")
                        .withPartnerID("partnerId2"));

        when(partnerResource.hentPartnerIDViaOrgnummer(anyRequest)).thenReturn(partnerInformasjonResponse);
    }
}