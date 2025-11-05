# Populasjonstilgangskontroll
Backendsystem for å tilby tilgangskontroll basert på populasjonen.


Informasjon om cacher i løsningen
Løsningen har flere cacher for å optimalisere ytelse og redusere belastning på underliggende systemer: 
* EntraOID til NavIdent : Informasjon caches i 365 dager.
* Ansattes AD-grupper : Informasjon caches i 3 timer. (blir oppdatert asykront ved utløp av element i cache (slett --> hent ny))
* Skjerming : Informasjon caches i 12 timer
* Nom (kobling mellom navident og personident) : Informasjon caches i 12 timer.
* PDL persondata : Informasjon caches i 12 timer.




