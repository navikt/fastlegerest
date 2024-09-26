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

* JDK 21

### Build

Run `./gradlew clean shadowJar`

### Lint (Ktlint)
##### Command line
Run checking: `./gradlew --continue ktlintCheck`

Run formatting: `./gradlew ktlintFormat`

## Contact

### For NAV employees

We are available at the Slack channel `#isyfo`.
