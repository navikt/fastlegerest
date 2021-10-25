package no.nav.syfo.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenResponse
import no.nav.syfo.consumer.syfopartnerinfo.PartnerInfoResponse
import no.nav.syfo.consumer.syfopartnerinfo.SyfoPartnerInfoConsumer
import no.nav.syfo.metric.Metrikk
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate
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
    lateinit var azureAdV2TokenConsumer: AzureAdV2TokenConsumer

    @MockBean
    lateinit var metrikk: Metrikk

    @Value("\${syfopartnerinfo.url}")
    private lateinit var syfopartnerinfoUrl: String

    @Inject
    @Qualifier(value = "default")
    lateinit var restTemplate: RestTemplate

    @Before
    fun setUp() {
        mockRestServiceServer = MockRestServiceServer
            .bindTo(restTemplate)
            .build()

        mockSyfopartnerinfo()

        syfoPartnerInfoConsumer = SyfoPartnerInfoConsumer(
            azureAdV2TokenConsumer = azureAdV2TokenConsumer,
            metrikk = metrikk,
            restTemplate = restTemplate,
            syfopartnerinfoClientId = "",
            syfopartnerinfoUrl = syfopartnerinfoUrl
        )
    }

    @Test
    fun empty() {
        syfoPartnerInfoConsumer.getPartnerId(HER_ID.toString())
    }

    @After
    fun cleanUp() {
        mockRestServiceServer.reset()
    }

    private fun mockSyfopartnerinfo() {
        val url = "$syfopartnerinfoUrl/api/v2/behandler?herid=$HER_ID"
        val infoResponse: MutableList<PartnerInfoResponse>? = null

        mockRestServiceServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(url))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(objectMapper.writeValueAsString(infoResponse), MediaType.APPLICATION_JSON))
    }

    companion object {
        const val HER_ID: Int = 404
    }
}
