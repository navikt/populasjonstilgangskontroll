package no.nav.tilgangsmaskin.regler.motor

enum class BeskrivelseTekster(val kode: String, val avvisningÅrsak: String, val kortNavn: String) {
    STRENGT_FORTROLIG_ADRESSE(
        "AVVIST_STRENGT_FORTROLIG_ADRESSE",
        "Mangler tilgang til strengt fortrolig adresse",
        "Kode 6"
    ),
    AVVIST_STRENGT_FORTROLIG_UTLAND(
        "AVVIST_STRENGT_FORTROLIG_UTLAND",
        "Mangler tilgang til strengt fortrolig adresse utland",
        "Kode 17"
    ),
    FORTROLIG_ADRESSE("AVVIST_FORTROLIG_ADRESSE", "Mangler tilgang til fortrolig adresse", "Kode 7"),
    SKJERMING("AVVIST_SKJERMING", "Mangler tilgang til skjermet person", "Skjerming"),
    GEOGRAFISK("AVVIST_GEOGRAFISK", "Mangler tilgang til brukers geografiske adresse", "Geografisk tilknytning"),
    EGNEDATA("AVVIST_HABILITET0", "Avvist grunnet manglende habilitet 0", "Oppslag habilitet 0"),
    FORELDREBARN("AVVIST_HABILITET1", "Avvist grunnet manglende habilitet 1", "Oppslag habilitet 1"),
    PARTNER("AVVIST_HABILITET2", "Avvist grunnet manglende habilitet 2", "Oppslag habilitet 2"),
    SØSKEN("AVVIST_HABILITET3", "Avvist grunnet manglende habilitet 3", "Oppslag habilitet 3"),
    AVDØD("AVVIST_AVDØD", "Bruker er avdød", "Avdød bruker"),
    PERSON_UTLAND("AVVIST_PERSON_UTLAND", "Mangler tilgang til person i utlandet", "Person bosatt utland"),
    PERSON_UKJENT(
        "AVVIST_PERSON_UKJENT",
        "Mangler tilgang til person uten kjent adresse",
        "Person bosatt ukjent bosted"
    )

}