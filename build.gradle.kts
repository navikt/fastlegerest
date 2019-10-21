group = "no.nav.syfo"
version = "1.0-SNAPSHOT"
description = "fastlegerest"

val sourceCompatibility = "1.8"
val springBootVersion = "2.1.1.RELEASE"

plugins {
    kotlin("jvm") version "1.3.31"
    id("java")
    id("maven-publish")
    id("com.diffplug.gradle.spotless") version "3.18.0"
    id("com.github.johnrengelman.shadow") version "4.0.3"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.3.50"
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-allopen:1.3.50")
    }
}

allOpen {
    annotation("org.springframework.context.annotation.Configuration")
    annotation("org.springframework.stereotype.Service")
    annotation("org.springframework.stereotype.Component")
    annotation("org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc")
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
    implementation( "org.projectlombok:lombok:1.18.2")
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
    implementation( "org.apache.cxf:cxf-spring-boot-starter-jaxws:3.3.3")
    implementation( "org.apache.cxf:cxf-rt-features-logging:3.3.3")
    implementation( "org.apache.cxf:cxf-rt-ws-security:3.3.3")
    implementation( "org.apache.cxf:cxf-rt-ws-policy:3.3.3")
    annotationProcessor("org.projectlombok:lombok:1.18.6")
    testCompile( "org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testCompile( "no.nav.security:oidc-spring-test:0.2.5")
}



tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    create("printVersion") {
        doLast {
            println(project.version)
        }
    }
}
