![Build status](https://github.com/navikt/fastlegerest/workflows/main/badge.svg?branch=master)

# Fastlegerest
Fastlegerest er en backend-app som tilbyr et rest-api for oppslag av arbeidstakers fastlege.

## Technologies used

* Docker
* Gradle
* Kafka
* Kotlin
* Ktor

##### Test Libraries:

* Kluent
* Mockk
* Spek

#### Requirements

* JDK 17

### Build

Run `./gradlew clean shadowJar`

### Lint (Ktlint)
##### Command line
Run checking: `./gradlew --continue ktlintCheck`

Run formatting: `./gradlew ktlintFormat`

## Download packages from Github Package Registry

Certain packages (tjenestespesifikasjoner) must be downloaded from Github Package Registry, which requires
authentication. The packages can be downloaded via build.gradle:

```
val githubUser: String by project
val githubPassword: String by project
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/navikt/tjenestespesifikasjoner")
        credentials {
            username = githubUser
            password = githubPassword
        }
    }
}
```

`githubUser` and `githubPassword` are properties that are set in `~/.gradle/gradle.properties`:

```
githubUser=x-access-token
githubPassword=<token>
```

Where `<token>` is a personal access token with scope `read:packages`(and SSO enabled).

The variables can alternatively be configured as environment variables or used in the command lines:

* `ORG_GRADLE_PROJECT_githubUser`
* `ORG_GRADLE_PROJECT_githubPassword`

```
./gradlew -PgithubUser=x-access-token -PgithubPassword=[token]
```

## Contact

### For NAV employees

We are available at the Slack channel `#isyfo`.
