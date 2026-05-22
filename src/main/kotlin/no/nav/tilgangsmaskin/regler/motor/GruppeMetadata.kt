package no.nav.tilgangsmaskin.regler.motor

import io.swagger.v3.oas.annotations.media.Schema
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_AVDØD
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_FORTROLIG_ADRESSE
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_GEOGRAFISK
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_HABILITET
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_PERSON_UTLAND
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_SKJERMING
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_STRENGT_FORTROLIG_ADRESSE
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_UKJENT_BOSTED
import no.nav.tilgangsmaskin.regler.motor.AvvisningsKode.AVVIST_VERGEMÅL

enum class GruppeMetadata(val meta: AvvisningsKode, val meldingsnøkkel: String) {

    STRENGT_FORTROLIG(AVVIST_STRENGT_FORTROLIG_ADRESSE, "regel.strengt-fortrolig"),
    STRENGT_FORTROLIG_UTLAND(AVVIST_STRENGT_FORTROLIG_UTLAND, "regel.strengt-fortrolig-utland"),
    FORTROLIG(AVVIST_FORTROLIG_ADRESSE, "regel.fortrolig"),
    SKJERMING(AVVIST_SKJERMING, "regel.skjerming"),
    NASJONAL(AVVIST_GEOGRAFISK, "regel.geografisk"),
    EGNEDATA(AVVIST_HABILITET, "regel.egnedata"),
    FORELDREBARN(AVVIST_HABILITET, "regel.foreldrebarn"),
    PARTNER(AVVIST_HABILITET, "regel.partner"),
    SØSKEN(AVVIST_HABILITET, "regel.søsken"),
    FELLES_BARN(AVVIST_HABILITET, "regel.felles-barn"),
    AVDØD_MER_ENN_ETT_ÅR(AVVIST_AVDØD, "regel.avdød"),
    VERGEMÅL(AVVIST_VERGEMÅL, "regel.vergemål"),
    UTENLANDSK(AVVIST_PERSON_UTLAND, "regel.utenlandsk"),
    UKJENT_BOSTED(AVVIST_UKJENT_BOSTED, "regel.ukjent-bosted")
}

@Schema(description = "Avvisningskoder")
enum class AvvisningsKode {
    AVVIST_STRENGT_FORTROLIG_ADRESSE,
    AVVIST_STRENGT_FORTROLIG_UTLAND,
    AVVIST_AVDØD,
    AVVIST_VERGEMÅL,
    AVVIST_PERSON_UTLAND,
    AVVIST_SKJERMING,
    AVVIST_FORTROLIG_ADRESSE,
    AVVIST_UKJENT_BOSTED,
    AVVIST_GEOGRAFISK,
    AVVIST_HABILITET
}