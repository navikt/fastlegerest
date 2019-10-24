import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "no.nav.syfo"
version = "1.0-SNAPSHOT"
description = "fastlegerest"

val sourceCompatibility = "1.8"
val springBootVersion = "2.1.1.RELEASE"
val cxfVersion = "3.2.7"

plugins {
    kotlin("jvm") version "1.3.31"
    id("java")
    id("com.diffplug.gradle.spotless") version "3.18.0"
    id("com.github.johnrengelman.shadow") version "4.0.3"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven(url = "https://repo.adeo.no/repository/maven-releases")
    maven(url = "https://repo.adeo.no/repository/maven-snapshots/")
    maven(url = "http://packages.confluent.io/maven/")
    maven(url = "http://repo.maven.apache.org/maven2")
}

dependencies {
    implementation( "javax.inject:javax.inject:1")
    implementation( "javax.ws.rs:javax.ws.rs-api:2.0.1")
    implementation( "io.swagger:swagger-annotations:1.5.21")
    compileOnly( "org.projectlombok:lombok:1.18.2")
    implementation( "io.micrometer:micrometer-registry-prometheus:1.0.6")
    implementation( "org.springframework.boot:spring-boot-starter-web:$springBootVersion")
    implementation( "org.springframework.boot:spring-boot-starter-actuator:$springBootVersion")
    implementation( "org.springframework:spring-context-support:5.1.2.RELEASE")
    implementation( "org.springframework.boot:spring-boot-starter-cache:$springBootVersion")
    implementation( "org.springframework.boot:spring-boot-starter-aop:$springBootVersion")
    implementation( "org.springframework.boot:spring-boot-starter-jersey:$springBootVersion")
    implementation( "no.nav.syfo.tjenester:adresseregisteretV1-tjenestespesifikasjon:1.0.2")
    implementation( "no.nav.syfo.tjenester:partner-emottak:1.0")
    implementation( "no.nav.syfo.tjenester:fastlegeinformasjonV1-tjenestespesifikasjon:2.1.8")
    implementation( "no.nav.syfo.tjenester:brukerprofil-v3-tjenestespesifikasjon:3.0.1")
    implementation( "net.logstash.logback:logstash-logback-encoder:4.10")
    implementation( "no.nav.security:oidc-spring-support:0.2.15")
    implementation( "no.nav.security:oidc-support:0.2.15")
    implementation( "no.nav.common:auth:2018.11.20.15.56")
    implementation("org.apache.cxf:cxf-rt-features-logging:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-ws-security:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-ws-policy:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-transports-http:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-frontend-jaxws:$cxfVersion")
    annotationProcessor("org.projectlombok:lombok:1.18.6")
    testCompile( "org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testCompile( "no.nav.security:oidc-spring-test:0.2.5")
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
}
