import no.nav.syfo.LocalApplication
import no.nav.syfo.rest.ressurser.DialogRessurs
import no.nav.syfo.rest.ressurser.MockUtils
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)



class DialogRessursTest {
    @MockBean
    lateinit var dialogRessurs: DialogRessurs

    @Before
    fun setUp() {
        mockPartnerResource();
        mockAdresseRegisteret();
        mockAzureAD();
        mockSyfopartnerinfo();
        MockUtils.mockBrukerProfil(brukerprofilV3);
        MockUtils.mockHarFastlege(fastlegeSoapClient);
        mockDialogfordeler();
        mockTokenService();
    }
}
