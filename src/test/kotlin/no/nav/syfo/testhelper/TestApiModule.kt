package no.nav.syfo.testhelper

import io.ktor.server.application.*
import no.nav.syfo.application.api.apiModule
import no.nav.syfo.application.cache.RedisStore
import no.nav.syfo.fastlege.ws.adresseregister.AdresseregisterClient
import no.nav.syfo.fastlege.ws.fastlegeregister.FastlegeInformasjonClient
import redis.clients.jedis.*

fun Application.testApiModule(
    externalMockEnvironment: ExternalMockEnvironment,
) {
    val cache = RedisStore(
        JedisPool(
            JedisPoolConfig(),
            externalMockEnvironment.environment.redisHost,
            externalMockEnvironment.environment.redisPort,
            Protocol.DEFAULT_TIMEOUT,
            externalMockEnvironment.environment.redisSecret
        )
    )
    this.apiModule(
        applicationState = externalMockEnvironment.applicationState,
        environment = externalMockEnvironment.environment,
        wellKnownAzure = externalMockEnvironment.wellKnownInternalAzureAD,
        cache = cache,
        fastlegeClient = FastlegeInformasjonClient(externalMockEnvironment.fastlegeMock),
        adresseregisterClient = AdresseregisterClient(externalMockEnvironment.adresseregisterMock)
    )
}
