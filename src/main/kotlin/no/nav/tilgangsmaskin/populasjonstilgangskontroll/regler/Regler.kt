package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.AvvisningBegrunnelse.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelBeskrivelse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Component
@Order(HIGHEST_PRECEDENCE)
class StrengtFortroligRegel(@Value("\${gruppe.strengt}") private val id: UUID) : KjerneRegel(STRENGT_FORTROLIG_GRUPPE, id, "Kode 6")

@Component
@Order(HIGHEST_PRECEDENCE + 1)
class FortroligRegel(@Value("\${gruppe.fortrolig}") private val id: UUID): KjerneRegel(FORTROLIG_GRUPPE, id, "Kode 7")

@Component
@Order(HIGHEST_PRECEDENCE + 2)
class EgenAnsattRegel(@Value("\${gruppe.egenansatt}") private val id: UUID) : KjerneRegel(EGEN_ANSATT_GRUPPE, id,"Egen ansatt")


@Component
@Order(HIGHEST_PRECEDENCE + 3)
class UtlandUdefinertGeoRegel(@Value("\${gruppe.utland}") private val id: UUID) : Regel {
    override fun test(ansatt: Ansatt,bruker: Bruker) =
        if (bruker.geoTilknytning is UtenlandskTilknytning) {
            ansatt.kanBehandle(id)
        } else true

    override val beskrivelse = RegelBeskrivelse("Person bosatt utland", AVVIST_PERSON_UTLAND)
}

@Component
@Order(HIGHEST_PRECEDENCE + 4)
class UkjentBostedGeoRegel(@Value("\${gruppe.udefinert}") private val id: UUID) : Regel {
    override fun test(ansatt: Ansatt,bruker: Bruker) =
        if (bruker.geoTilknytning is UkjentBosted) {
            ansatt.kanBehandle(id)
        } else true

    override val beskrivelse = RegelBeskrivelse("Person bosatt ukjent bosted", AVVIST_PERSON_UKJENT)
}

@Component
@Order(HIGHEST_PRECEDENCE + 5)
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

    override val beskrivelse = RegelBeskrivelse("Geografisk tilknytning", AVVIST_GEOGRAFISK)

}
