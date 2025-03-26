package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AvvisningKode.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeoTilknytning.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.Regel.RegelBeskrivelse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Component
@Order(LOWEST_PRECEDENCE)
class GeoNorgeRegel(@Value("\${gruppe.nasjonal}") private val id: UUID) : OverstyrbarRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) =
        ansatt.kanBehandle(id) || ansatt.grupper.any { it.displayName.endsWith("GEO_${
            when (bruker.geoTilknytning) {
                is KommuneTilknytning -> bruker.geoTilknytning.kommune.verdi
                is BydelTilknytning -> bruker.geoTilknytning.bydel.verdi
                else -> return true
            }
        }")
        }

    override val metadata = RegelBeskrivelse("Geografisk tilknytning", AVVIST_GEOGRAFISK)
}

@Component
@Order(LOWEST_PRECEDENCE - 1)
class UkjentBostedGeoRegel(@Value("\${gruppe.udefinert}") private val id: UUID) : OverstyrbarRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) =
        if (bruker.geoTilknytning is UkjentBosted) {
            ansatt.kanBehandle(id)
        } else true

    override val metadata = RegelBeskrivelse("Person bosatt ukjent bosted", AVVIST_PERSON_UKJENT)
}


@Component
@Order(LOWEST_PRECEDENCE - 2)
class UtlandUdefinertGeoRegel(@Value("\${gruppe.utland}") private val id: UUID) : OverstyrbarRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) =
        if (bruker.geoTilknytning is UtenlandskTilknytning) {
            ansatt.kanBehandle(id)
        } else true

    override val metadata = RegelBeskrivelse("Person bosatt utland", AVVIST_PERSON_UTLAND)
}

@Component
@Order(LOWEST_PRECEDENCE - 3)
class AvdødBrukerRegel(private val teller: AvdødAksessTeller) : OverstyrbarRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) =
        bruker.dødsdato?.let {
            teller.registrerAksess(ansatt.ansattId, bruker.brukerId, it)
        } ?: true
    override val metadata = RegelBeskrivelse("Avdød bruker", AVVIST_AVDØD)
}

@Component
@Order(LOWEST_PRECEDENCE - 4)
class SøskenRegel(private val teller: SøskenAksessTeller) : OverstyrbarRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) =
        if (bruker.brukerId in ansatt.søsken) {
            teller.registrerAksess(ansatt.ansattId, bruker.brukerId)
        } else true

    override val metadata = RegelBeskrivelse("Oppslag søsken", AVVIST_EGEN_FAMILIE)
}






