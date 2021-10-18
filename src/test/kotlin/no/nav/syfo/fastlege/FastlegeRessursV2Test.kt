package no.nav.syfo.fastlege

import no.nav.security.token.support.test.JwtTokenGenerator
import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.pdl.PdlConsumer
import no.nav.syfo.consumer.tilgangskontroll.Tilgang
import no.nav.syfo.consumer.tilgangskontroll.TilgangkontrollConsumer
import no.nhn.register.communicationparty.ICommunicationPartyService
import no.nhn.register.communicationparty.WSOrganizationPerson
import no.nhn.schemas.reg.flr.IFlrReadOperations
import org.hamcrest.Matchers
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import testhelper.MockUtils.mockHarFastlege
import testhelper.generatePdlHentPerson
import testhelper.mockIngenFastleger

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class FastlegeRessursV2Test {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var pdlConsumer: PdlConsumer

    @MockBean
    private lateinit var tilgangkontrollConsumer: TilgangkontrollConsumer

    @MockBean
    private lateinit var fastlegeSoapClient: IFlrReadOperations

    @MockBean
    private lateinit var adresseregisterSoapClient: ICommunicationPartyService

    @Before
    fun setUp() {
        Mockito.`when`(pdlConsumer.person(ArgumentMatchers.anyString()))
            .thenReturn(generatePdlHentPerson(null))
    }

    @Test
    fun finnAktivFastlege() {
        mockOrganisationPerson()
        mockHarFastlege(fastlegeSoapClient)
        Mockito.`when`(tilgangkontrollConsumer.accessAzureAdV2(FNR)).thenReturn(
            Tilgang(
                harTilgang = true,
                begrunnelse = ""
            )
        )
        mvc.perform(
            MockMvcRequestBuilders.get("/api/v2/fastlege?fnr=$FNR")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(LEGEKONTOR)))
    }

    @Test
    fun finnAktivFastlegeMedParentHerId() {
        mockOrganisationPerson(PARENT_HER_ID)
        mockHarFastlege(fastlegeSoapClient)
        Mockito.`when`(tilgangkontrollConsumer.accessAzureAdV2(FNR)).thenReturn(
            Tilgang(
                harTilgang = true,
                begrunnelse = ""
            )
        )
        mvc.perform(
            MockMvcRequestBuilders.get("/api/v2/fastlege?fnr=$FNR")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(PARENT_HER_ID.toString())))

    }

    @Test
    fun finnAlleFastleger() {
        mockOrganisationPerson()
        mockHarFastlege(fastlegeSoapClient)
        Mockito.`when`(tilgangkontrollConsumer.accessAzureAdV2(FNR)).thenReturn(
            Tilgang(
                harTilgang = true,
                begrunnelse = ""
            )
        )
        mvc.perform(
            MockMvcRequestBuilders.get("/api/v2/fastlege/fastleger?fnr=$FNR")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(LEGEKONTOR)))
    }

    @Test
    fun brukerHarIngenFastleger() {
        mockIngenFastleger(fastlegeSoapClient)
        Mockito.`when`(tilgangkontrollConsumer.accessAzureAdV2(FNR)).thenReturn(
            Tilgang(
                harTilgang = true,
                begrunnelse = ""
            )
        )
        mvc.perform(
            MockMvcRequestBuilders.get("/api/v2/fastlege/fastleger?fnr=$FNR")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("[]"))
    }

    @Test
    fun brukerManglerAktivFastlege() {
        mockIngenFastleger(fastlegeSoapClient)
        Mockito.`when`(tilgangkontrollConsumer.accessAzureAdV2(FNR)).thenReturn(
            Tilgang(
                harTilgang = true,
                begrunnelse = ""
            )
        )
        mvc.perform(
            MockMvcRequestBuilders.get("/api/v2/fastlege?fnr=$FNR")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.content().json(mockApiError404()))
    }

    @Test
    fun brukerHarIkkeTilgang() {
        mockHarFastlege(fastlegeSoapClient)
        Mockito.`when`(tilgangkontrollConsumer.accessAzureAdV2(FNR)).thenReturn(
            Tilgang(
                harTilgang = false,
                begrunnelse = "GEOGRAFISK"
            )
        )
        mvc.perform(
            MockMvcRequestBuilders.get("/api/v2/fastlege?fnr=$FNR")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
            .andExpect(MockMvcResultMatchers.content().json(mockApiError403()))
    }

    private fun mockApiError403(): String {
        return mockApiErrorAsJson(403, "GEOGRAFISK")
    }

    private fun mockApiError404(): String {
        return mockApiErrorAsJson(404, "Feil ved oppslag av fastlege")
    }

    private fun mockApiErrorAsJson(status: Int, message: String): String {
        val jsonObject = JSONObject()
        jsonObject.put("status", status)
        jsonObject.put("message", message)
        return jsonObject.toString()
    }

    private fun mockOrganisationPerson(parentHerId: Int? = null) {
        val wsOrganisationPerson = WSOrganizationPerson().withParentHerId(parentHerId)
        Mockito.`when`(adresseregisterSoapClient.getOrganizationPersonDetails(ArgumentMatchers.anyInt()))
            .thenReturn(wsOrganisationPerson)
    }

    companion object {
        private const val FNR = "99999900000"
        private const val VEILEDER_ID = "veilederID"
        private const val LEGEKONTOR = "Pontypandy Legekontor"
        private const val PARENT_HER_ID: Int = 707
        private val token = JwtTokenGenerator.createSignedJWT(VEILEDER_ID).serialize()
    }
}
