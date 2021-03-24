# Fastlegerest

Fastlegerest er en backendapp som tilbyr integrasjon mot pasient- og helsejournalsystemene via dialogfordeler. 

Fastlegerest er en Spring boot-applikasjon i Nais, med hemmeligheter i vault. 
## Lokal utvikling

### Oppstart

Start opp appen fra [LocalApplication.java](../fastlegerest/src/test/java/no/nav/syfo/LocalApplication.java).

Appen kjører da på localhost:8899/


### Testing

Appen har komponenttester fra kant til kant for alle sine endepunkter. Appen er satt opp til å bruke
syntetisk test miljø hos Norsk Helsenett i preprod, dvs q4 hos NAV. Kontakt med Norsk Helsenett finnes
i slack-kanalen #ext-nav-grunndata

### Bygging

Applikasjonen pakkes til en stor jar i gradle, og bygges med docker.

### Pipeline

Pipeline er på Github Action.
Commits til Master-branch deployes automatisk til dev-fss og prod-fss.
Commits til ikke-master-branch bygges uten automatisk deploy.

## Hente pakker fra Github Package Registry
Noen pakker hentes fra Github Package Registry som krever autentisering.
Pakkene kan lastes ned via build.gradle slik:
```
val githubUser: String by project
val githubPassword: String by project
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/navikt/syfotjenester")
        credentials {
            username = githubUser
            password = githubPassword
        }
    }
}
```

`githubUser` og `githubPassword` settes i `~/.gradle/gradle.properties`:

```
githubUser=x-access-token
githubPassword=<token>
```

Hvor `<token>` er et personal access token med scope `read:packages`(og SSO enabled).

Evt. kan variablene kan også konfigureres som miljøvariabler eller brukes i kommandolinjen:

* `ORG_GRADLE_PROJECT_githubUser`
* `ORG_GRADLE_PROJECT_githubPassword`

```
./gradlew -PgithubUser=x-access-token -PgithubPassword=[token]
```
