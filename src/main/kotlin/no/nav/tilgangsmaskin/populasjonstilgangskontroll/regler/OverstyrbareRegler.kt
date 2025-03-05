package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.AvvisningBegrunnelse.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelBeskrivelse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Component
@Order(LOWEST_PRECEDENCE)
class GeoNorgeRegel(@Value("\${gruppe.nasjonal}") private val id: UUID) : Regel {
    override fun test(ansatt: Ansatt,bruker: Bruker) =
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
class UkjentBostedGeoRegel(@Value("\${gruppe.udefinert}") private val id: UUID) : Regel {
    override fun test(ansatt: Ansatt,bruker: Bruker) =
        if (bruker.geoTilknytning is UkjentBosted) {
            ansatt.kanBehandle(id)
        } else true

    override val metadata = RegelBeskrivelse("Person bosatt ukjent bosted", AVVIST_PERSON_UKJENT)
}


@Component
@Order(LOWEST_PRECEDENCE - 2)
class UtlandUdefinertGeoRegel(@Value("\${gruppe.utland}") private val id: UUID) : Regel {
    override fun test(ansatt: Ansatt,bruker: Bruker) =
        if (bruker.geoTilknytning is UtenlandskTilknytning) {
            ansatt.kanBehandle(id)
        } else true

    override val metadata = RegelBeskrivelse("Person bosatt utland", AVVIST_PERSON_UTLAND)
}





