package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AvvisningKode.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning.*
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
            when (bruker.geografiskTilknytning) {
                is KommuneTilknytning -> bruker.geografiskTilknytning.kommune.verdi
                is BydelTilknytning -> bruker.geografiskTilknytning.bydel.verdi
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
        if (bruker.geografiskTilknytning is UkjentBosted) {
            ansatt.kanBehandle(id)
        } else true

    override val metadata = RegelBeskrivelse("Person bosatt ukjent bosted", AVVIST_PERSON_UKJENT)
}


@Component
@Order(LOWEST_PRECEDENCE - 2)
class UtlandUdefinertGeoRegel(@Value("\${gruppe.utland}") private val id: UUID) : OverstyrbarRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) =
        if (bruker.geografiskTilknytning is UtenlandskTilknytning) {
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







