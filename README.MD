# Fastlegerest

Fastlegerest er en backendapp som tilbyr integrasjon mot pasient- og helsejournalsystemene via dialogfordeler. 

Fastlegerest er en Spring boot-applikasjon i Nais.

## Lokal utvikling

### Oppstart

Start opp appen fra [LocalApplication.java](../fastlegerest/src/test/java/no/nav/syfo/LocalApplication.java).

Appen kjører da på localhost:8899/


### Testing

Appen har komponenttester fra kant til kant for alle sine endepunkter.


### Bygging

Applikasjonen pakkes til en stor jar vha spring-boot-maven-plugin, og bygges med docker.

### Deploy

Deploy til preprod med fasitressurser fra q1 gjøres av alle brancher pushet til stash. Dette er satt opp på en 
[Jenkins-server](https://ci.adeo.no/job/digisyfo/job/fastlegerest/).