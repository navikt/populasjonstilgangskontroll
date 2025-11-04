# Populasjonstilgangskontroll
Backendsystem for å tilby tilgangskontroll basert på populasjonen.


Informasjon om cacher i løsningen
Løsningen har flere cacher for å optimalisere ytelse og redusere belastning på underliggende systemer: 
EntraOID til NavIdent Mapping Cache : Informasjon caches i 365 dager.
Ansattes AD-grupper Cache : Informasjon caches i 3 timer. (blir oppdatert asykront ved utløp av element i cache (slett --> hent ny))
Skjerming Cache : Informasjon caches i 12 timer
Nom (kobling mellom navident og personident) Cache : Informasjon caches i 12 timer.
PDL persondata Cache : Informasjon caches i 12 timer.




