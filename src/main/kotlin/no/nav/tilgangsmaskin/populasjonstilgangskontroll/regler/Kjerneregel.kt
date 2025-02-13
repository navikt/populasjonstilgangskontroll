package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.EGEN_ANSATT_GRUPPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.FORTROLIG_GRUPPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.STRENGT_FORTROLIG_GRUPPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelBeskrivelse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.UUID


abstract class KjerneRegel(private val gruppe: GlobalGruppe, private val id: UUID, kortNavn: String): Regel {
    override fun test(bruker: Bruker, s: Ansatt) =
        if (bruker.kreverGruppe(gruppe))  {
            s.kanBehandle(id)
        } else true

    override val beskrivelse = RegelBeskrivelse(kortNavn, gruppe.begrunnelse,false)
}

@Component
@Order(HIGHEST_PRECEDENCE)
class StrengtFortroligRegel(@Value("\${gruppe.strengt}") private val id: UUID) : KjerneRegel(STRENGT_FORTROLIG_GRUPPE, id, "Kode 6")

@Component
@Order(HIGHEST_PRECEDENCE + 1)
class FortroligRegel(@Value("\${gruppe.fortrolig}") private val id: UUID): KjerneRegel(FORTROLIG_GRUPPE, id, "Kode 7")

@Component
@Order(HIGHEST_PRECEDENCE + 2)
class EgenAnsattRegel(@Value("\${gruppe.egenansatt}") private val id: UUID) : KjerneRegel(EGEN_ANSATT_GRUPPE, id,"Egen ansatt")

