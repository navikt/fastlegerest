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
    val redisConfig = externalMockEnvironment.environment.redisConfig
    val cache = RedisStore(
        JedisPool(
            JedisPoolConfig(),
            HostAndPort(redisConfig.host, redisConfig.port),
            DefaultJedisClientConfig.builder()
                .ssl(redisConfig.ssl)
                .password(redisConfig.redisPassword)
                .build()
        )
    )
    externalMockEnvironment.redisCache = cache

    this.apiModule(
        applicationState = externalMockEnvironment.applicationState,
        environment = externalMockEnvironment.environment,
        wellKnownAzure = externalMockEnvironment.wellKnownInternalAzureAD,
        cache = externalMockEnvironment.redisCache,
        fastlegeClient = FastlegeInformasjonClient(externalMockEnvironment.fastlegeMock),
        adresseregisterClient = AdresseregisterClient(externalMockEnvironment.adresseregisterMock)
    )
}
