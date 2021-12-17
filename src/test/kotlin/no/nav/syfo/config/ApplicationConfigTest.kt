package no.nav.syfo.config

import org.springframework.boot.test.context.SpringBootTest
import no.nav.syfo.LocalApplication
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
class ApplicationConfigTest {
    @Test
    fun test() {
    }
}
