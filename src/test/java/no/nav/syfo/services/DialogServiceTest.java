package no.nav.syfo.services;

import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.domain.Fastlegekontor;
import no.nav.syfo.domain.OrganisasjonPerson;
import no.nav.syfo.domain.Partnerinformasjon;
import no.nav.syfo.services.exceptions.PartnerinformasjonIkkeFunnet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static no.nav.brukerdialog.tools.SecurityConstants.SYSTEMUSER_PASSWORD;
import static no.nav.brukerdialog.tools.SecurityConstants.SYSTEMUSER_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class DialogServiceTest {
    @Mock
    private FastlegeService fastlegeService;
    @Mock
    private PartnerService partnerService;
    @Mock
    private AdresseregisterService adresseregisterService;

    @InjectMocks
    private DialogService dialogService;

    @BeforeEach
    void setUp() {
        System.setProperty(SYSTEMUSER_USERNAME, "user");
        System.setProperty(SYSTEMUSER_PASSWORD, "pass");
        initMocks(this);
    }

    @Test
    void getPartnerinformasjonEnMuligPartner() {
        when(partnerService.hentPartnerinformasjon(anyString())).thenReturn(singletonList(
                new Partnerinformasjon("partnerId1", "11")));
        when(adresseregisterService.hentFastlegeOrganisasjonPerson(anyInt()))
                .thenAnswer(invocation ->
                        new OrganisasjonPerson((Integer) invocation.getArgument(0) + 10));

        Partnerinformasjon partnerinformasjon = dialogService.getPartnerinformasjon(new Fastlege()
                .herId(1)
                .fastlegekontor(new Fastlegekontor()
                        .orgnummer("orgnummer")));

        assertThat(partnerinformasjon)
                .hasFieldOrPropertyWithValue("herId", "11")
                .hasFieldOrPropertyWithValue("partnerId", "partnerId1");
    }

    @Test
    void getPartnerinformasjonForsteAvToMuligePartnere() {
        List<Partnerinformasjon> partnerListe = asList(
                new Partnerinformasjon("partnerId1", "11"),
                new Partnerinformasjon("partnerId2", "12"));
        when(partnerService.hentPartnerinformasjon(anyString())).thenReturn(partnerListe);
        when(adresseregisterService.hentFastlegeOrganisasjonPerson(anyInt()))
                .thenAnswer(invocation ->
                        new OrganisasjonPerson((Integer) invocation.getArgument(0) + 10));

        Partnerinformasjon partnerinformasjon = dialogService.getPartnerinformasjon(new Fastlege()
                .herId(1)
                .fastlegekontor(new Fastlegekontor()
                        .orgnummer("orgnummer")));

        assertThat(partnerinformasjon)
                .isSameAs(partnerListe.get(0))
                .hasFieldOrPropertyWithValue("herId", "11")
                .hasFieldOrPropertyWithValue("partnerId", "partnerId1");
    }

    @Test
    void getPartnerinformasjonAndreAvToMuligePartnere() {
        List<Partnerinformasjon> partnerListe = asList(
                new Partnerinformasjon("partnerId1", "11"),
                new Partnerinformasjon("partnerId2", "12"));
        when(partnerService.hentPartnerinformasjon(anyString())).thenReturn(partnerListe);
        when(adresseregisterService.hentFastlegeOrganisasjonPerson(anyInt()))
                .thenAnswer(invocation ->
                        new OrganisasjonPerson((Integer) invocation.getArgument(0) + 10));

        Partnerinformasjon partnerinformasjon = dialogService.getPartnerinformasjon(new Fastlege()
                .herId(2)
                .fastlegekontor(new Fastlegekontor()
                        .orgnummer("orgnummer")));

        assertThat(partnerinformasjon)
                .isSameAs(partnerListe.get(1))
                .hasFieldOrPropertyWithValue("herId", "12")
                .hasFieldOrPropertyWithValue("partnerId", "partnerId2");
    }

    @Test
    void getPartnerinformasjonToMuligePartnereHarFeilHerId() {
        List<Partnerinformasjon> partnerListe = asList(
                new Partnerinformasjon("partnerId1", "11"),
                new Partnerinformasjon("partnerId2", "12"));
        when(partnerService.hentPartnerinformasjon(anyString())).thenReturn(partnerListe);
        when(adresseregisterService.hentFastlegeOrganisasjonPerson(anyInt()))
                .thenAnswer(invocation ->
                        new OrganisasjonPerson((Integer) invocation.getArgument(0) + 10));

        Executable executable = () -> dialogService.getPartnerinformasjon(new Fastlege()
                .herId(3)
                .fastlegekontor(new Fastlegekontor()
                        .orgnummer("orgnummer")));

        assertThrows(PartnerinformasjonIkkeFunnet.class, executable);
    }
}