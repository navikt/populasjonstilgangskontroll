package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import no.nav.boot.conditionals.Cluster.*
import no.nav.boot.conditionals.ConditionalOnClusters
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GlobalGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AvvisningTekster.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GlobalGruppe.EGEN_ANSATT_GRUPPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GlobalGruppe.FORTROLIG_GRUPPE
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.Regel.RegelBeskrivelse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Order(HIGHEST_PRECEDENCE)
class StrengtFortroligRegel(@Value("\${gruppe.strengt}") private val id: UUID) : GlobaleGrupperRegel(GlobalGruppe.STRENGT_FORTROLIG_GRUPPE, id, "Kode 6")
@Component
@Order(HIGHEST_PRECEDENCE + 1)
class FortroligRegel(@Value("\${gruppe.fortrolig}") private val id: UUID): GlobaleGrupperRegel(FORTROLIG_GRUPPE, id, "Kode 7")

@Component
@Order(HIGHEST_PRECEDENCE + 2)
class EgenAnsattRegel(@Value("\${gruppe.egenansatt}") private val id: UUID) : GlobaleGrupperRegel(EGEN_ANSATT_GRUPPE, id,"Egen ansatt")

@Order(HIGHEST_PRECEDENCE + 3)
@Component
class EgneDataRegel : KjerneRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) = bruker.brukerId != ansatt.bruker?.brukerId
    override val metadata = RegelBeskrivelse("Egne data", AVVIST_EGNE_DATA)
}

@Order(HIGHEST_PRECEDENCE + 4)
@Component
class EgenFamilieRegel : KjerneRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) =
        bruker.brukerId !in ansatt.familieMedlemmer


    override val metadata = RegelBeskrivelse("Egen familie", AVVIST_EGEN_FAMILIE)
}
abstract class GlobaleGrupperRegel(private val gruppe: GlobalGruppe, private val id: UUID, kortNavn: String):
    KjerneRegel {
    override fun test(ansatt: Ansatt,bruker: Bruker) =
        if (bruker.kreverGlobalGruppe(gruppe))  {
            ansatt.kanBehandle(id)
        } else true

    override val metadata = RegelBeskrivelse(kortNavn, gruppe.begrunnelse)
}

