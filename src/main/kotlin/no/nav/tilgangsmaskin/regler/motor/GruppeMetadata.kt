package no.nav.tilgangsmaskin.regler.motor

import io.swagger.v3.oas.annotations.media.Schema
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_AVDØD
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_FORTROLIG_ADRESSE
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_GEOGRAFISK
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_HABILITET
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_PERSON_UKJENT
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_PERSON_UTLAND
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_SKJERMING
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_STRENGT_FORTROLIG_ADRESSE
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_STRENGT_FORTROLIG_UTLAND

const val HABILITET = "Du har ikke tilgang til data om deg selv eller dine nærstående"

enum class GruppeMetadata(val meta: AvvisningsKode, val begrunnelse: String, val kortNavn: String) {

    STRENGT_FORTROLIG(AVVIST_STRENGT_FORTROLIG_ADRESSE, "Du har ikke tilgang til brukere med strengt fortrolig adresse", "Kode 6"),
    STRENGT_FORTROLIG_UTLAND(AVVIST_STRENGT_FORTROLIG_UTLAND, "Du har ikke tilgang til brukere med strengt fortrolig adresse i utlandet", "Paragraf 19"),
    FORTROLIG(AVVIST_FORTROLIG_ADRESSE, "Du har ikke tilgang til brukere med fortrolig adresse", "Kode 7"),
    SKJERMING(AVVIST_SKJERMING, "Du har ikke tilgang til Nav-ansatte og andre skjermede brukere", "Skjerming"),
    NASJONAL(AVVIST_GEOGRAFISK,"Du har ikke tilgang til brukerens geografiske område eller enhet","Geografisk tilknytning"),
    EGNEDATA(AVVIST_HABILITET, HABILITET, "Egne data"),
    FORELDREBARN(AVVIST_HABILITET, HABILITET, "Foreldre/barn"),
    PARTNER(AVVIST_HABILITET, HABILITET, "Partner"),
    SØSKEN(AVVIST_HABILITET, HABILITET, "Søsken"),
    FELLES_BARN(AVVIST_HABILITET, HABILITET, "Felles barn"),
    AVDØD(AVVIST_AVDØD, "Du har ikke tilgang til ubformasjon om avdøde brukere", "Avdød bruker"),
    UTENLANDSK(AVVIST_PERSON_UTLAND, "Du har ikke tilgang til person bosatt i utlandet", "Person bosatt utland"),
    UKJENT_BOSTED(AVVIST_PERSON_UKJENT, "Du har ikke tilgang til person uten kjent adresse", "Person bosatt ukjent bosted")
}

@Schema(description = "Avvisningskoder")
enum class AvvisningsKode {
    AVVIST_STRENGT_FORTROLIG_ADRESSE,
    AVVIST_STRENGT_FORTROLIG_UTLAND,
    AVVIST_AVDØD,
    AVVIST_PERSON_UTLAND,
    AVVIST_SKJERMING,
    AVVIST_FORTROLIG_ADRESSE,
    AVVIST_PERSON_UKJENT,
    AVVIST_GEOGRAFISK,
    AVVIST_HABILITET
}