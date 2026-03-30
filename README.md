# Populasjonstilgangskontroll
Backendsystem for å tilby tilgangskontroll basert på populasjonen, også kjent som tilgangsmaskinen.
Fagsystemer kan spørre tilgangsmaskinen om den ansatte har tilgang til brukeren, men ikke domenespesifikk tilgangskontroll (f.eks om den ansatte har tilgang til fagsystemet, eller til å godkjenne forslag til vedtak).

Dokumentasjon på Confluence: https://confluence.adeo.no/spaces/TM/pages/760466828/Tilgangsmaskinen

Slack-kanal: [team-tilgangsmaskinen-værsågod](https://nav-it.slack.com/archives/C07GGDP38S2)

## Swagger
Prod: https://tilgangsmaskin.intern.nav.no/swagger-ui/index.html#/

Dev: https://tilgangsmaskin.intern.dev.nav.no/swagger-ui/index.html#/

## Informasjon om cacher i løsningen

Løsningen har flere cacher for å optimalisere ytelse og redusere belastning på underliggende systemer:
* EntraOID til NavIdent : Informasjon caches i 365 dager.
* Ansattes AD-grupper : Informasjon caches i 3 timer. (blir oppdatert asykront ved utløp av element i cache (slett --> hent ny))
* Skjerming : Informasjon caches i 12 timer
* Nom (kobling mellom navident og personident) : Informasjon caches i 12 timer.
* PDL persondata : Informasjon caches i 12 timer.