import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.syfo"
version = "1.0.0"

val apacheHttpClientVersion = "4.5.13"
val kotlinJacksonVersion = "2.13.4"
val logstashVersion = "7.2"
val prometheusVersion = "1.9.5"
val slf4jVersion = "1.7.36"
val swaggerVersion = "1.6.7"
val tokenValidationSpringSupportVersion = "1.3.9"

plugins {
    kotlin("jvm") version "1.7.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.7.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    id("org.springframework.boot") version "2.6.12"
    id("io.spring.dependency-management") version "1.0.14.RELEASE"
}

allOpen {
    annotation("org.springframework.context.annotation.Configuration")
    annotation("org.springframework.stereotype.Service")
    annotation("org.springframework.stereotype.Component")
    annotation("org.springframework.boot.autoconfigure.SpringBootApplication")
}

val githubUser: String by project
val githubPassword: String by project
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/navikt/syfotjenester")
        credentials {
            username = githubUser
            password = githubPassword
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/navikt/tjenestespesifikasjoner")
        credentials {
            username = githubUser
            password = githubPassword
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("io.swagger:swagger-annotations:$swaggerVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$kotlinJacksonVersion")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-jersey")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-jta-atomikos")

    implementation("org.apache.httpcomponents:httpclient:$apacheHttpClientVersion")

    implementation("io.micrometer:micrometer-registry-prometheus:$prometheusVersion")

    implementation("no.nav.security:token-validation-spring:$tokenValidationSpringSupportVersion")

    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")

    testImplementation("no.nav.security:token-validation-test-support:$tokenValidationSpringSupportVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    withType<Jar> {
        manifest.attributes["Main-Class"] = "no.nav.syfo.ApplicationKt"
    }

    withType<Test> {
        useJUnitPlatform()
    }

    create("printVersion") {
        doLast {
            println(project.version)
        }
    }

    withType<ShadowJar> {
        transform(PropertiesFileTransformer::class.java) {
            paths = listOf("META-INF/spring.factories")
            mergeStrategy = "append"
        }
        mergeServiceFiles()
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}
