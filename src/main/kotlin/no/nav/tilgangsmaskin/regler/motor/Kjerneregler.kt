package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AvvisningKode.AVVIST_HABILITET0
import no.nav.tilgangsmaskin.ansatt.AvvisningKode.AVVIST_HABILITET1
import no.nav.tilgangsmaskin.ansatt.AvvisningKode.AVVIST_HABILITET2
import no.nav.tilgangsmaskin.ansatt.AvvisningKode.AVVIST_HABILITET3
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.EGEN_ANSATT_GRUPPE
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG_GRUPPE
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG_GRUPPE
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.regler.motor.AvdødOppslagTeller.PartnerOppslagTeller
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

interface KjerneRegel : Regel


@Component
@Order(HIGHEST_PRECEDENCE)
class StrengtFortroligRegel(@Value("\${gruppe.strengt}") private val id: UUID) : KjerneRegel {
    override fun erOK(ansatt: Ansatt, bruker: Bruker) =
        sjekkRegel({ bruker.kreverGlobalGruppe(STRENGT_FORTROLIG_GRUPPE) }, bruker, ansatt, id)

    override val metadata = RegelBeskrivelse("Kode 6", STRENGT_FORTROLIG_GRUPPE.kode)
}

@Component
@Order(HIGHEST_PRECEDENCE + 1)
class FortroligRegel(@Value("\${gruppe.fortrolig}") private val id: UUID) : KjerneRegel {
    override fun erOK(ansatt: Ansatt, bruker: Bruker) =
        sjekkRegel({ bruker.kreverGlobalGruppe(FORTROLIG_GRUPPE) }, bruker, ansatt, id)

    override val metadata = RegelBeskrivelse("Kode 7", FORTROLIG_GRUPPE.kode)
}

@Component
@Order(HIGHEST_PRECEDENCE + 2)
class EgenAnsattRegel(@Value("\${gruppe.egenansatt}") private val id: UUID) : KjerneRegel {
    override fun erOK(ansatt: Ansatt, bruker: Bruker) =
        sjekkRegel({ bruker.kreverGlobalGruppe(EGEN_ANSATT_GRUPPE) }, bruker, ansatt, id)

    override val metadata = RegelBeskrivelse("Kode 8", EGEN_ANSATT_GRUPPE.kode)
}

@Order(HIGHEST_PRECEDENCE + 3)
@Component
class EgneDataRegel : KjerneRegel {
    override fun erOK(ansatt: Ansatt, bruker: Bruker) =
        bruker.brukerId != ansatt.brukerId

    override val metadata = RegelBeskrivelse("Oppslag med manglende habilitet 0", AVVIST_HABILITET0)
}

@Order(HIGHEST_PRECEDENCE + 4)
@Component
class ForeldreOgBarnRegel : KjerneRegel {
    override fun erOK(ansatt: Ansatt, bruker: Bruker) =
        !(ansatt erForeldreEllerBarnTil bruker)

    override val metadata = RegelBeskrivelse("Oppslag med manglende habilitet 1", AVVIST_HABILITET1)
}

@Order(HIGHEST_PRECEDENCE + 5)
@Component
class PartnerRegel(private val teller: PartnerOppslagTeller) : KjerneRegel {
    override fun erOK(ansatt: Ansatt, bruker: Bruker) =
        (ansatt nåværendeEllerTidligerePartner bruker)?.let { partner ->
            teller.registrerOppslag(ansatt.ansattId, bruker.brukerId, partner.relasjon)
        } ?: true

    override val metadata = RegelBeskrivelse("Oppslag med manglende habilitet 2", AVVIST_HABILITET2)
}

@Component
@Order(HIGHEST_PRECEDENCE + 6)
class SøskenRegel(private val teller: SøskenOppslagTeller) :
    KjerneRegel {
    override fun erOK(ansatt: Ansatt, bruker: Bruker) =
        if (ansatt erSøskenTil bruker) {
            teller.registrerOppslag(ansatt.ansattId, bruker.brukerId)
        } else true

    override val metadata = RegelBeskrivelse("Oppslag med manglende habilitet 3", AVVIST_HABILITET3)
}