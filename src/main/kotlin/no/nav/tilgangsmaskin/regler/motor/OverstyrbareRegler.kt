package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.BOSTED_UTLAND
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.intervallSiden
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.AVDØD
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component


interface OverstyrbarRegel : Regel

@Component
@Order(LOWEST_PRECEDENCE)
class GeoNorgeRegel :
    OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { !(ansatt kanBehandle NASJONAL) && !(ansatt kanBehandle bruker.geografiskTilknytning) }

    override val metadata = Metadata(NASJONAL)
}

@Component
@Order(LOWEST_PRECEDENCE - 1)
class UkjentBostedGeoRegel : OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { bruker.geografiskTilknytning is UkjentBosted && !(ansatt kanBehandle UKJENT_BOSTED) }

    override val metadata = Metadata(UKJENT_BOSTED)
}

@Component
@Order(LOWEST_PRECEDENCE - 2)
class UtlandGeoRegel : OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { bruker.geografiskTilknytning is UtenlandskTilknytning && !(ansatt kanBehandle BOSTED_UTLAND) }

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







