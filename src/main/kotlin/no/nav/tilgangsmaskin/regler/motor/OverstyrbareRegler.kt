package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.GEO_PERSON_UTLAND
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UDEFINERT_GEO
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.intervallSiden
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.AVDØD
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.GEOGRAFISK
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.PERSON_UKJENT
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.PERSON_UTLAND
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component


interface OverstyrbarRegel : Regel

@Component
@Order(LOWEST_PRECEDENCE)
class GeoNorgeRegel(private val env: Environment) :
    OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        ansatt kanBehandle NASJONAL.id(env) || ansatt harGTFor bruker

    override val metadata = RegelBeskrivelse(GEOGRAFISK)
}

@Component
@Order(LOWEST_PRECEDENCE - 1)
class UkjentBostedGeoRegel(private val env: Environment) :
    OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        sjekkGruppeRegel({ bruker.geografiskTilknytning is UkjentBosted }, ansatt, UDEFINERT_GEO.id(env))

    override val metadata = RegelBeskrivelse(PERSON_UKJENT)
}

@Component
@Order(LOWEST_PRECEDENCE - 2)
class UtlandUdefinertGeoRegel(private val env: Environment) :
    OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        sjekkGruppeRegel(
            { bruker.geografiskTilknytning is UtenlandskTilknytning },
            ansatt,
            GEO_PERSON_UTLAND.id(env)
        )

    override val metadata = RegelBeskrivelse(PERSON_UTLAND)
}

@Component
@Order(LOWEST_PRECEDENCE - 3)
class AvdødBrukerRegel(private val teller: AvdødTeller) : OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis(
            { bruker.erDød }, teller, true,
            tags = bruker.dødsdato?.let { arrayOf("months" to it.intervallSiden()) } ?: emptyArray()
        )

    override val metadata = RegelBeskrivelse(AVDØD)
}







