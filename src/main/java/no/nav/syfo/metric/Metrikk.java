package no.nav.syfo.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller
public class Metrikk {

    private final MeterRegistry registry;

    @Inject
    public Metrikk(MeterRegistry registry) {
        this.registry = registry;
    }

    public void countEvent(String navn) {
        registry.counter(
                addPrefix(navn),
                Tags.of("type", "info")
        ).increment();
    }


    public void tellHendelse(String navn) {
        registry.counter(
                addPrefix(navn),
                Tags.of("type", "info")
        ).increment();
    }

    public void tellHttpKall(int kode) {
        registry.counter(
                addPrefix("httpstatus"),
                Tags.of(
                        "type", "info",
                        "kode", String.valueOf(kode)
                )
        ).increment();
    }

    private String addPrefix(String navn) {
        String METRIKK_PREFIX = "fastlegerest_";
        return METRIKK_PREFIX + navn;
    }
}
