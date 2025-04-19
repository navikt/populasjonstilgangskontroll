package no.nav.tilgangsmaskin.regler.motor

const val INTRO1 = "Avvist grunnet manglende"
private const val INTRO2 = "tilgang til "
private const val INTRO3 = "$INTRO1 $INTRO2"

enum class BeskrivelseTekster(val kode: String, val avvisningÅrsak: String, val kortNavn: String) {

    STRENGT_FORTROLIG("AVVIST_STRENGT_FORTROLIG_ADRESSE", "${INTRO3}strengt fortrolig adresse", "Kode 6"),
    AVVIST_STRENGT_FORTROLIG_UTLAND(
            "AVVIST_STRENGT_FORTROLIG_UTLAND",
            "${INTRO3}strengt fortrolig adresse utland",
            "Kode 17"),
    FORTROLIG("AVVIST_FORTROLIG_ADRESSE", "${INTRO3}fortrolig adresse", "Kode 7"),
    SKJERMING("AVVIST_SKJERMING", "${INTRO3}skjermet person", "Skjerming"),
    NASJONAL("AVVIST_GEOGRAFISK", "${INTRO3}brukers geografiske adresse", "Geografisk tilknytning"),
    EGNEDATA("AVVIST_HABILITET0", "${INTRO1}habilitet 0", "Oppslag habilitet 0"),
    FORELDREBARN("AVVIST_HABILITET1", "${INTRO1}habilitet 1", "Oppslag habilitet 1"),
    PARTNER("AVVIST_HABILITET2", "${INTRO1}habilitet 2", "Oppslag habilitet 2"),
    SØSKEN("AVVIST_HABILITET3", "${INTRO1}habilitet 3", "Oppslag habilitet 3"),
    AVDØD("AVVIST_AVDØD", "${INTRO3}avdød bruker", "Avdød bruker"),
    UTENLANDSK("AVVIST_PERSON_UTLAND", "${INTRO3}person i utlandet", "Person bosatt utland"),
    UKJENT_BOSTED("AVVIST_PERSON_UKJENT", "${INTRO3}person uten kjent adresse", "Person bosatt ukjent bosted")

}