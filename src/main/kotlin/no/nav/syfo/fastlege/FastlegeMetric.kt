package no.nav.syfo.fastlege

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Counter.builder
import no.nav.syfo.application.metric.METRICS_NS
import no.nav.syfo.application.metric.METRICS_REGISTRY

const val CALL_FASTLEGER = "${METRICS_NS}_fastleger"
const val CALL_FASTLEGER_CACHE_HIT = "${CALL_FASTLEGER}_cache_hit_count"
const val CALL_FASTLEGER_CACHE_MISS = "${CALL_FASTLEGER}_cache_miss_count"
const val CALL_FASTLEGE_SUCCESS = "${CALL_FASTLEGER}_success_count"
const val CALL_FASTLEGE_NOT_FOUND = "${CALL_FASTLEGER}_not_found_count"
const val CALL_FASTLEGE_FAIL = "${CALL_FASTLEGER}_fail_count"

const val CALL_ADRESSEREGISTER = "${METRICS_NS}_call_adresseregister"
const val CALL_ADRESSEREGISTER_SUCCESS = "${CALL_ADRESSEREGISTER}_success_count"
const val CALL_ADRESSEREGISTER_NOT_FOUND = "${CALL_ADRESSEREGISTER}_not_found_count"
const val CALL_ADRESSEREGISTER_FAIL = "${CALL_ADRESSEREGISTER}_fail_count"

const val CALL_ADRESSEREGISTER_BEHANDLERE = "${METRICS_NS}_call_adresseregister_behandlere"
const val CALL_ADRESSEREGISTER_BEHANDLERE_SUCCESS = "${CALL_ADRESSEREGISTER_BEHANDLERE}_success_count"
const val CALL_ADRESSEREGISTER_BEHANDLERE_NOT_FOUND = "${CALL_ADRESSEREGISTER_BEHANDLERE}_not_found_count"
const val CALL_ADRESSEREGISTER_BEHANDLERE_FAIL = "${CALL_ADRESSEREGISTER_BEHANDLERE}_fail_count"

val COUNT_FASTLEGE_SUCCESS: Counter = builder(CALL_FASTLEGE_SUCCESS)
    .description("Counts the number of successful calls to Fastlegeregisteret")
    .register(METRICS_REGISTRY)
val COUNT_FASTLEGE_NOT_FOUND: Counter = builder(CALL_FASTLEGE_NOT_FOUND)
    .description("Counts the number of calls to Fastlegeregisteret where result is not found")
    .register(METRICS_REGISTRY)
val COUNT_FASTLEGE_FAIL: Counter = builder(CALL_FASTLEGE_FAIL)
    .description("Counts the number of failed calls to Fastlegeregisteret")
    .register(METRICS_REGISTRY)
val COUNT_CALL_FASTLEGER_CACHE_HIT: Counter = builder(CALL_FASTLEGER_CACHE_HIT)
    .description("Counts the number of cache hits when looking up fastleger")
    .register(METRICS_REGISTRY)
val COUNT_CALL_FASTLEGER_CACHE_MISS: Counter = builder(CALL_FASTLEGER_CACHE_MISS)
    .description("Counts the number of cache misses when looking up fastleger")
    .register(METRICS_REGISTRY)
val COUNT_ADRESSEREGISTER_SUCCESS: Counter = builder(CALL_ADRESSEREGISTER_SUCCESS)
    .description("Counts the number of successful calls to adresseregisteret")
    .register(METRICS_REGISTRY)
val COUNT_ADRESSEREGISTER_NOT_FOUND: Counter = builder(CALL_ADRESSEREGISTER_NOT_FOUND)
    .description("Counts the number of calls to adresseregisteret where result is not found")
    .register(METRICS_REGISTRY)
val COUNT_ADRESSEREGISTER_FAIL: Counter = builder(CALL_ADRESSEREGISTER_FAIL)
    .description("Counts the number of failed calls to adresseregisteret behandlere")
    .register(METRICS_REGISTRY)
val COUNT_ADRESSEREGISTER_BEHANDLERE_SUCCESS: Counter = builder(CALL_ADRESSEREGISTER_BEHANDLERE_SUCCESS)
    .description("Counts the number of successful calls to adresseregisteret behandlere")
    .register(METRICS_REGISTRY)
val COUNT_ADRESSEREGISTER_BEHANDLERE_NOT_FOUND: Counter = builder(CALL_ADRESSEREGISTER_BEHANDLERE_NOT_FOUND)
    .description("Counts the number of calls to adresseregisteret behandlere where result is not found")
    .register(METRICS_REGISTRY)
val COUNT_ADRESSEREGISTER_BEHANDLERE_FAIL: Counter = builder(CALL_ADRESSEREGISTER_BEHANDLERE_FAIL)
    .description("Counts the number of failed calls to adresseregisteret behandlere")
    .register(METRICS_REGISTRY)
