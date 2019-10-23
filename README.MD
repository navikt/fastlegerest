# Fastlegerest

Fastlegerest er en backendapp som tilbyr integrasjon mot pasient- og helsejournalsystemene via dialogfordeler. 

Fastlegerest er en Spring boot-applikasjon i Nais, med hemmeligheter i vault og ressurser fra
naiserator.yaml i syfonais.

## Lokal utvikling

### Oppstart

Start opp appen fra [LocalApplication.java](../fastlegerest/src/test/java/no/nav/syfo/LocalApplication.java).

Appen kjører da på localhost:8899/


### Testing

Appen har komponenttester fra kant til kant for alle sine endepunkter.

### Bygging

Applikasjonen pakkes til en stor jar i gradle, og bygges med docker.

### Deploy

Deploy til preprod og prod gjøres på en [Jenkins-server](https://jenkins-digisyfo.adeo.no/job/digisyfo/job/fastlegerest/).
Alle brancher bygges. Deploy av branch gjøres i egen jenkins oppgave kalt deploy, merge til master deployes automatisk
til preprod.
