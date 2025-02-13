package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelBeskrivelse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

abstract class KjerneRegel(private val gruppe: GlobalGruppe, private val id: UUID, kortNavn: String): Regel {
    override fun test(bruker: Bruker, s: Ansatt) =
        if (bruker.kreverGruppe(gruppe))  {
            s.kanBehandle(id)
        } else true

    override val beskrivelse = RegelBeskrivelse(kortNavn, gruppe.begrunnelse,false)
}
/*
abstract class GeografiskKjerneRegel(private val gruppe: GlobalGruppe, private val roller: List<EntraGruppe>): Regel {
    override fun test(bruker: Bruker, s: Ansatt) =
        if (bruker.geoTilknytning == null) { // Sjekke om bruker har geografisk tilknytning eller satt til udefinert
            return s.kanBehandle(ADRolleUdefinert)
        }
         if(brukersGeografiskTilknytningErUtland()) // Validere at GT er string med 3 bokstaver
    {
       return s.kanBehandle(Utland)
    }

        if (s.harNasjonalTilgang(NasajonalTilgang)) {
            return true // har ansatt nasjonal tilgang skal vi ikke sjekke geografisk tilknytning når utlandet er sjekket
        }
        if(bruker.harGeografiskTilknytning()) {
           return  s.kanBehandle(bruker.geoTilknytning)
        }
    if(bruker.harOppfølgingskontor()
    {
       return s.kanBehandle(Kontornøkkel) // må hentes it fra ad-gruppenes navn og ikke fra id GEO_XXXX
    }


    override val beskrivelse = RegelBeskrivelse(kortNavn, gruppe.begrunnelse,false)
}
*/

@Component
@Order(HIGHEST_PRECEDENCE)
class StrengtFortroligRegel(@Value("\${gruppe.strengt}") private val id: UUID) : KjerneRegel(STRENGT_FORTROLIG_GRUPPE, id, "Kode 6")

@Component
@Order(HIGHEST_PRECEDENCE + 1)
class FortroligRegel(@Value("\${gruppe.fortrolig}") private val id: UUID): KjerneRegel(FORTROLIG_GRUPPE, id, "Kode 7")

@Component
@Order(HIGHEST_PRECEDENCE + 2)
class EgenAnsattRegel(@Value("\${gruppe.egenansatt}") private val id: UUID) : KjerneRegel(EGEN_ANSATT_GRUPPE, id,"Egen ansatt")

/*
@Component
@Order(HIGHEST_PRECEDENCE + 3)
class GeografiskRegel(@Value("\${gruppe.geografisk}") private val id: UUID) : GeografiskKjerneRegel(GEOGRAFISK_GRUPPE, id, "Geografisk tilgang")
*/
/**
 * Kjerneregel matcher for nasjonal, men får problemer med hirekarki
 *
 * Om Nasjonal blir avvist så skal det sjekkes om det er en geografisk tilknytning *
 * Om geografisk tilknytning avvises så skal det sjekkes om det er en tilknytning på oppfølgingskontor
 * om oppfølgingskontor avvises så skal det tilgangen avvises

*/
