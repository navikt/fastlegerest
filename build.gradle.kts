import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.syfo"
version = "1.0.0"

val sourceCompatibility = "1.8"
val springBootVersion = "2.3.0.RELEASE"
val cxfVersion = "3.3.6"
val kotlinLibVersion = "1.3.50"
val kotlinJacksonVersion = "2.10.0"
val navOidcVersion = "0.2.18"
val logstashVersion = "6.3"
val commonsVersion = "3.10"
val owaspHtmlSanitizerVersion = "20190325.1"
val prometheusVersion = "1.5.1"

plugins {
    kotlin("jvm") version "1.3.50"
    id("java")
    id("org.jetbrains.kotlin.plugin.allopen") version "1.3.50"
    id("com.github.johnrengelman.shadow") version "4.0.3"
    id("org.springframework.boot") version "2.3.0.RELEASE"
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.0")
        classpath("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
        classpath("org.glassfish.jaxb:jaxb-runtime:2.4.0-b180830.0438")
        classpath("com.sun.activation:javax.activation:1.2.0")
        classpath("org.springframework.boot:spring-boot-gradle-plugin:2.0.4.RELEASE")
        classpath("com.sun.xml.ws:jaxws-tools:2.3.1") {
            exclude(group = "com.sun.xml.ws", module = "policy")
        }
    }
}

allOpen {
    annotation("org.springframework.context.annotation.Configuration")
    annotation("org.springframework.stereotype.Service")
    annotation("org.springframework.stereotype.Component")
    annotation("lombok.extern.slf4j.Slf4j")
    annotation("org.springframework.boot.autoconfigure.SpringBootApplication")
    annotation("org.springframework.boot.autoconfigure.SpringBootApplication")
    annotation("lombok.AllArgsConstructor")
}

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://repo.adeo.no/repository/maven-releases/")
    maven(url = "https://repo.adeo.no/repository/maven-snapshots/")
    maven(url = "http://packages.confluent.io/maven/")
    maven(url = "https://dl.bintray.com/kotlin/kotlinx/")
}

dependencies {
    implementation( "io.swagger:swagger-annotations:1.5.21")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinLibVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinLibVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$kotlinJacksonVersion")

    implementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-logging:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-aop:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-jersey:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-cache:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-jta-atomikos:$springBootVersion")
    implementation( "org.springframework:spring-context-support:5.1.2.RELEASE")

    implementation("io.micrometer:micrometer-registry-prometheus:$prometheusVersion")

    implementation("no.nav.security:oidc-spring-support:$navOidcVersion")
    implementation("no.nav.security:oidc-support:$navOidcVersion")
    implementation("com.microsoft.azure:adal4j:1.6.4")
    implementation( "no.nav.syfo.tjenester:adresseregisteretV1-tjenestespesifikasjon:1.0.3")
    implementation( "no.nav.syfo.tjenester:partner-emottak:1.0")
    implementation( "no.nav.syfo.tjenester:fastlegeinformasjonV1-tjenestespesifikasjon:2.1.8")
    implementation( "no.nav.syfo.tjenester:brukerprofil-v3-tjenestespesifikasjon:3.0.1")

    implementation("org.apache.cxf:cxf-rt-features-logging:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-ws-security:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-ws-policy:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-transports-http:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-frontend-jaxws:$cxfVersion")

    implementation("javax.inject:javax.inject:1")
    implementation("javax.ws.rs:javax.ws.rs-api:2.0.1")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    implementation("org.apache.commons:commons-lang3:$commonsVersion")
    implementation("com.googlecode.owasp-java-html-sanitizer:owasp-java-html-sanitizer:$owaspHtmlSanitizerVersion")
    compileOnly("org.projectlombok:lombok:1.18.6")
    annotationProcessor("org.projectlombok:lombok:1.18.6")

    testCompile("no.nav.security:oidc-spring-test:0.2.5")
    testCompile("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Jar> {
        manifest.attributes["Main-Class"] = "no.nav.syfo.Application"
    }

    create("printVersion") {
        doLast {
            println(project.version)
        }
    }

    withType<ShadowJar> {
        transform(ServiceFileTransformer::class.java) {
            setPath("META-INF/cxf")
            include("bus-extensions.txt")
        }
        transform(PropertiesFileTransformer::class.java) {
            paths = listOf("META-INF/spring.factories")
            mergeStrategy = "append"
        }
        mergeServiceFiles()
    }

    named<KotlinCompile>("compileKotlin") {
        kotlinOptions.jvmTarget = "1.8"
    }

    named<KotlinCompile>("compileTestKotlin") {
        kotlinOptions.jvmTarget = "1.8"
    }
}
