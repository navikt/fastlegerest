package no.nav.syfo.syfopartnerinfo

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.syfo.LocalApplication
import no.nav.syfo.azuread.AzureAdResponse
import no.nav.syfo.azuread.AzureAdTokenConsumer
import no.nav.syfo.domain.Token
import no.nav.syfo.metric.Metrikk
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate
import java.time.Instant
import java.util.*
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SyfoPartnerInfoConsumerTest {
    lateinit var mockRestServiceServer: MockRestServiceServer

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var syfoPartnerInfoConsumer: SyfoPartnerInfoConsumer


    @MockBean
    lateinit var metrikk: Metrikk

    @MockBean
    @Qualifier(value = "BasicAuth")
    lateinit var basicAuthRestTemplate: RestTemplate

    @Inject
    @Qualifier(value = "Oidc")
    lateinit var restTemplate: RestTemplate

    @Mock
    lateinit var azureAdTokenConsumer: AzureAdTokenConsumer

    @MockBean
    @Qualifier(value = "restTemplateWithProxy")
    lateinit var restTemplateWithProxy: RestTemplate

    @Before
    fun setUp() {
        mockRestServiceServer = MockRestServiceServer
                .bindTo(restTemplate)
                .build()

        mockAzureAD()
        mockSyfopartnerinfo()
        mockTokenService()

        syfoPartnerInfoConsumer = SyfoPartnerInfoConsumer(azureAdTokenConsumer = azureAdTokenConsumer, metrikk = metrikk, restTemplate = restTemplate, syfoPartnerInfoAppId = "")
    }

    @Test
    fun empty() {
        syfoPartnerInfoConsumer.getPartnerId(HER_ID.toString())
    }

    @After
    fun cleanUp() {
        mockRestServiceServer.reset()
    }

    private fun mockTokenService() {
        val token = Token.builder().access_token("testtoken").build()
        Mockito.`when`(basicAuthRestTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.any<Class<Any>>()
        )).thenReturn(ResponseEntity(token, HttpStatus.OK))
    }

    private fun mockSyfopartnerinfo() {
        val URL = "http://syfopartnerinfo/api/v1/behandler?herid=$HER_ID"
        val infoResponse: MutableList<PartnerInfoResponse>? = null // listOf(ResponseEntity<List<PartnerInfoResponse>>(infoResponse, HttpStatus.OK))

        mockRestServiceServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(URL))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(objectMapper.writeValueAsString(infoResponse), MediaType.APPLICATION_JSON))
    }

    private fun mockAzureAD() {
        val azureAdResponse = AzureAdResponse(
                "token",
                "",
                "",
                "",
                Instant.now(),
                "",
                ""
        )
        val response = ResponseEntity<Any>(azureAdResponse, HttpStatus.OK)
        Mockito.`when`(restTemplateWithProxy.exchange(
                Mockito.contains("aadaccesstoken"),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(),
                Mockito.any<Class<Any>>()
        )).thenReturn(response)
    }


    companion object {
        const val HER_ID: Int = 404
    }
}
