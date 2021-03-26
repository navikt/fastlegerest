package no.nav.syfo.config

import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.boot.test.context.SpringBootTest
import no.nav.syfo.LocalApplication
import org.junit.Test
import org.springframework.test.annotation.DirtiesContext

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
class ApplicationConfigTest {
    @Test
    fun test() {
    }
}
