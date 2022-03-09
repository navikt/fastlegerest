package no.nav.syfo.metric

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import org.springframework.stereotype.Controller
import javax.inject.Inject

@Controller
class Metrikk @Inject constructor(
    private val registry: MeterRegistry
) {
    fun countEvent(navn: String) {
        registry.counter(
            addPrefix(navn),
            Tags.of("type", "info")
        ).increment()
    }

    fun tellHendelse(navn: String) {
        registry.counter(
            addPrefix(navn),
            Tags.of("type", "info")
        ).increment()
    }

    fun tellHttpKall(kode: Int) {
        registry.counter(
            addPrefix("httpstatus"),
            Tags.of(
                "type", "info",
                "kode", kode.toString()
            )
        ).increment()
    }

    private fun addPrefix(navn: String): String {
        val METRIKK_PREFIX = "fastlegerest_"
        return METRIKK_PREFIX + navn
    }
}
