package no.nav.syfo

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.Environment
import no.nav.syfo.application.api.apiModule
import no.nav.syfo.application.api.authentication.getWellKnown
import no.nav.syfo.application.cache.ValkeyStore
import no.nav.syfo.fastlege.ws.adresseregister.AdresseregisterClient
import no.nav.syfo.fastlege.ws.adresseregister.adresseregisterSoapClient
import no.nav.syfo.fastlege.ws.fastlegeregister.FastlegeInformasjonClient
import no.nav.syfo.fastlege.ws.fastlegeregister.fastlegeSoapClient
import org.slf4j.LoggerFactory
import redis.clients.jedis.*
import java.util.concurrent.TimeUnit

const val applicationPort = 8080

fun main() {
    val applicationState = ApplicationState()
    val logger = LoggerFactory.getLogger("ktor.application")
    val environment = Environment()
    val valkeyConfig = environment.valkeyConfig
    val cache = ValkeyStore(
        JedisPool(
            JedisPoolConfig(),
            HostAndPort(valkeyConfig.host, valkeyConfig.port),
            DefaultJedisClientConfig.builder()
                .ssl(valkeyConfig.ssl)
                .user(valkeyConfig.valkeyUsername)
                .password(valkeyConfig.valkeyPassword)
                .database(valkeyConfig.valkeyDB)
                .build()
        )
    )

    val applicationEnvironment = applicationEnvironment {
        log = logger
        config = HoconApplicationConfig(ConfigFactory.load())
    }

    val server = embeddedServer(
        Netty,
        environment = applicationEnvironment,
        configure = {
            connector {
                port = applicationPort
            }
            connectionGroupSize = 8
            workerGroupSize = 8
            callGroupSize = 16
        },
        module = {
            val fastlegeClient = FastlegeInformasjonClient(
                fastlegeSoapClient = fastlegeSoapClient(
                    serviceUrl = environment.fastlegeUrl,
                    username = environment.nhnUsername,
                    password = environment.nhnPassword,
                ),
            )
            val adresseregisterClient = AdresseregisterClient(
                adresseregisterSoapClient = adresseregisterSoapClient(
                    serviceUrl = environment.adresseregisterUrl,
                    username = environment.nhnUsername,
                    password = environment.nhnPassword,
                ),
            )
            apiModule(
                applicationState = applicationState,
                environment = environment,
                wellKnownAzure = getWellKnown(environment.azure.appWellKnownUrl),
                cache = cache,
                fastlegeClient = fastlegeClient,
                adresseregisterClient = adresseregisterClient,
            )
            monitor.subscribe(ApplicationStarted) {
                applicationState.ready = true
                logger.info("Application is ready, running Java VM ${Runtime.version()}")
            }
        }
    )

    Runtime.getRuntime().addShutdownHook(
        Thread {
            server.stop(10, 10, TimeUnit.SECONDS)
        }
    )

    server.start(wait = true)
}
