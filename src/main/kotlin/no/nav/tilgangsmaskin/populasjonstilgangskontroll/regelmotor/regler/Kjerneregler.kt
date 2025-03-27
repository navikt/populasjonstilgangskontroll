package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AvvisningKode.AVVIST_EGEN_FAMILIE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AvvisningKode.AVVIST_EGNE_DATA
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe.EGEN_ANSATT_GRUPPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe.FORTROLIG_GRUPPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.Regel.RegelBeskrivelse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Component
@Order(HIGHEST_PRECEDENCE)
class StrengtFortroligRegel(@Value("\${gruppe.strengt}") private val id: UUID) : GlobalGruppeRegel(GlobalGruppe.STRENGT_FORTROLIG_GRUPPE, id, "Kode 6")
@Component
@Order(HIGHEST_PRECEDENCE + 1)
class FortroligRegel(@Value("\${gruppe.fortrolig}") private val id: UUID): GlobalGruppeRegel(FORTROLIG_GRUPPE, id, "Kode 7")

@Component
@Order(HIGHEST_PRECEDENCE + 2)
class EgenAnsattRegel(@Value("\${gruppe.egenansatt}") private val id: UUID) : GlobalGruppeRegel(EGEN_ANSATT_GRUPPE, id,"Egen ansatt")

@Order(HIGHEST_PRECEDENCE + 3)
@Component
class EgneDataRegel : KjerneRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) =
        bruker.brukerId != ansatt.brukerId
    override val metadata = RegelBeskrivelse("Egne data", AVVIST_EGNE_DATA)
}

@Order(HIGHEST_PRECEDENCE + 4)
@Component
class ForeldreOgBarnRegel : KjerneRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) =
        bruker.brukerId !in (ansatt.foreldreOgBarn)
    override val metadata = RegelBeskrivelse("Egen familie", AVVIST_EGEN_FAMILIE)
}

@Component
@Order(HIGHEST_PRECEDENCE + 5)
class SøskenRegel(private val teller: SøskenAksessTeller) : KjerneRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) =
        if (bruker.brukerId in ansatt.søsken) {
            teller.registrerAksess(ansatt.ansattId, bruker.brukerId)
        } else true

    override val metadata = RegelBeskrivelse("Oppslag søsken", AVVIST_EGEN_FAMILIE)
}