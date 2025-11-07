import com.adarshr.gradle.testlogger.theme.ThemeType
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer

group = "no.nav.syfo"
version = "0.0.1"

val commonsCollectionVersion = "3.2.2"
val commonsTextVersion = "1.14.0"
val cxfVersion = "3.6.5"
val jacksonVersion = "2.20.0"
val jacksonDataTypeVersion = "2.20.0"
val javaxActivationVersion = "1.2.0"
val javaxWsRsApiVersion = "2.1.1"
val jaxbVersion = "2.3.1"
val jaxwsVersion = "2.3.5"
val jedisVersion = "7.0.0"
val jsonVersion = "20250517"
val ktorVersion = "3.3.1"
val logbackVersion = "1.5.20"
val logstashEncoderVersion = "9.0"
val micrometerRegistryVersion = "1.12.13"
val mockkVersion = "1.14.6"
val nimbusjosejwtVersion = "10.5"
val redisEmbeddedVersion = "0.7.3"
val syfotjenesterVersion = "1.2022.09.09-14.42-5356e2174b6c"

plugins {
    kotlin("jvm") version "2.2.20"
    id("com.gradleup.shadow") version "8.3.6"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
    id("com.adarshr.test-logger") version "4.0.0"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-call-id:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")

    implementation("org.apache.commons:commons-text:$commonsTextVersion")
    implementation("org.apache.cxf:cxf-rt-features-logging:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-ws-security:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-ws-policy:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-transports-http:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-frontend-jaxws:$cxfVersion")
    implementation("javax.ws.rs:javax.ws.rs-api:$javaxWsRsApiVersion")
    implementation("com.sun.xml.ws:jaxws-ri:$jaxwsVersion")
    implementation("com.sun.xml.ws:jaxws-tools:$jaxwsVersion")
    implementation("com.sun.activation:javax.activation:$javaxActivationVersion")
    implementation("commons-collections:commons-collections") {
        version {
            strictly(commonsCollectionVersion)
        }
    }
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonDataTypeVersion")
    implementation("javax.xml.bind:jaxb-api:$jaxbVersion")
    implementation("org.glassfish.jaxb:jaxb-runtime:$jaxbVersion")

    implementation("no.nav.syfotjenester:adresseregisteretv1-tjenestespesifikasjon:$syfotjenesterVersion")
    implementation("no.nav.syfotjenester:fastlegeinformasjonv1-tjenestespesifikasjon:$syfotjenesterVersion")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashEncoderVersion")
    implementation("org.json:json:$jsonVersion")

    // Metrics and Prometheus
    implementation("io.ktor:ktor-server-metrics-micrometer:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:$micrometerRegistryVersion")

    // (De-)serialization
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    // Cache
    implementation("redis.clients:jedis:$jedisVersion")

    // Tests
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation(kotlin("test"))
    testImplementation("com.nimbusds:nimbus-jose-jwt:$nimbusjosejwtVersion")
}

kotlin {
    jvmToolchain(21)
}

tasks {
    jar {
        manifest.attributes["Main-Class"] = "no.nav.syfo.AppKt"
    }

    create("printVersion") {
        doLast {
            println(project.version)
        }
    }

    shadowJar {
        transform(ServiceFileTransformer::class.java) {
            setPath("META-INF/cxf")
            include("bus-extensions.txt")
        }
        mergeServiceFiles()
        archiveBaseName.set("app")
        archiveClassifier.set("")
        archiveVersion.set("")
    }

    test {
        useJUnitPlatform()
        testlogger {
            theme = ThemeType.STANDARD_PARALLEL
            showFullStackTraces = true
            showPassed = false
        }
    }
}
