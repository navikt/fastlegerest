package no.nav.syfo.fastlege

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Counter.builder
import no.nav.syfo.application.metric.METRICS_NS
import no.nav.syfo.application.metric.METRICS_REGISTRY

const val CALL_FASTLEGER = "${METRICS_NS}_fastleger"
const val CALL_FASTLEGER_CACHE_HIT = "${CALL_FASTLEGER}_cache_hit_count"
const val CALL_FASTLEGER_CACHE_MISS = "${CALL_FASTLEGER}_cache_miss_count"

val COUNT_CALL_FASTLEGER_CACHE_HIT: Counter = builder(CALL_FASTLEGER_CACHE_HIT)
    .description("Counts the number of cache hits when looking up fastleger")
    .register(METRICS_REGISTRY)

val COUNT_CALL_FASTLEGER_CACHE_MISS: Counter = builder(CALL_FASTLEGER_CACHE_MISS)
    .description("Counts the number of cache misses when looking up fastleger")
    .register(METRICS_REGISTRY)
