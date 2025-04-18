package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.BOSTED_UTLAND
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.intervallSiden
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.AVDØD
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*


interface OverstyrbarRegel : Regel

@Component
@Order(LOWEST_PRECEDENCE)
class GeoNorgeRegel(@Value("\${gruppe.nasjonal}") private val id: UUID) :
    OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { !(ansatt kanBehandle id) && !(ansatt kanBehandle bruker.geografiskTilknytning) }

    override val metadata = Metadata(GlobalGruppe.NASJONAL)
}

@Component
@Order(LOWEST_PRECEDENCE - 1)
class UkjentBostedGeoRegel(@Value("\${gruppe.udefinert}") private val id: UUID) : OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { bruker.geografiskTilknytning is UkjentBosted && !(ansatt kanBehandle id) }

    override val metadata = Metadata(UKJENT_BOSTED)
}

@Component
@Order(LOWEST_PRECEDENCE - 2)
class UtlandGeoRegel(@Value("\${gruppe.utland}") private val id: UUID) :
    OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { bruker.geografiskTilknytning is UtenlandskTilknytning && !(ansatt kanBehandle id) }

    override val metadata = Metadata(BOSTED_UTLAND)
}

@Component
@Order(LOWEST_PRECEDENCE - 3)
class AvdødBrukerRegel(private val teller: AvdødTeller) : OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { bruker.erDød }.also {
            if (bruker.dødsdato != null) {
                val tags = arrayOf("months" to bruker.dødsdato.intervallSiden())
                teller.increment(it, *tags)
            }
        }

    override val metadata = Metadata(AVDØD)
}







