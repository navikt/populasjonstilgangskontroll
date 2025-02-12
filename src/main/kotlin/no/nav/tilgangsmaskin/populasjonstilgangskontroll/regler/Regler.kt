package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.EGEN_GRUPPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.FORTROLIG_GRUPPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.STRENGT_FORTROLIG_GRUPPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelBeskrivelse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.UUID

abstract class AbstraktRegel(private val gruppe: GlobalGruppe, private val id: UUID, private val kode: String): Regel {
    override fun test(k: Kandidat, s: Saksbehandler) = if (k.kreverGruppe(gruppe))  {
        s.kanBehandle(id)
    } else true

    override val beskrivelse = RegelBeskrivelse(javaClass.simpleName.replace(Regex("([A-Z][a-z]*)$"), "").trim(), kode)
}

@Component
@Order(HIGHEST_PRECEDENCE)
class StrengtFortroligRegel(@Value("\${gruppe.strengt}") private val id: UUID) : AbstraktRegel(STRENGT_FORTROLIG_GRUPPE, id, "6")

@Component
@Order(HIGHEST_PRECEDENCE + 1)
class FortroligRegel(@Value("\${gruppe.fortrolig}") private val id: UUID): AbstraktRegel(FORTROLIG_GRUPPE, id, "7")
@Component
@Order(HIGHEST_PRECEDENCE + 2)
class EgenAnsattRegel(@Value("\${gruppe.egenansatt}") private val id: UUID) : AbstraktRegel(EGEN_GRUPPE, id, "42")

