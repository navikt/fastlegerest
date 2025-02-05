package no.nav.syfo.testhelper

import io.ktor.server.application.*
import no.nav.syfo.application.api.apiModule
import no.nav.syfo.application.cache.ValkeyStore
import no.nav.syfo.fastlege.ws.adresseregister.AdresseregisterClient
import no.nav.syfo.fastlege.ws.fastlegeregister.FastlegeInformasjonClient
import redis.clients.jedis.*

fun Application.testApiModule(
    externalMockEnvironment: ExternalMockEnvironment,
) {
    val valkeyConfig = externalMockEnvironment.environment.valkeyConfig
    val cache = ValkeyStore(
        JedisPool(
            JedisPoolConfig(),
            HostAndPort(valkeyConfig.host, valkeyConfig.port),
            DefaultJedisClientConfig.builder()
                .ssl(valkeyConfig.ssl)
                .password(valkeyConfig.valkeyPassword)
                .build()
        )
    )
    externalMockEnvironment.valkeyCache = cache

    this.apiModule(
        applicationState = externalMockEnvironment.applicationState,
        environment = externalMockEnvironment.environment,
        wellKnownAzure = externalMockEnvironment.wellKnownInternalAzureAD,
        cache = externalMockEnvironment.valkeyCache,
        fastlegeClient = FastlegeInformasjonClient(externalMockEnvironment.fastlegeMock),
        adresseregisterClient = AdresseregisterClient(externalMockEnvironment.adresseregisterMock)
    )
}
