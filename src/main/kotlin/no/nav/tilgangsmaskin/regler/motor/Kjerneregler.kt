package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.EGEN_ANSATT
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.EGNEDATA
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.FORELDREBARN
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.PARTNER
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.SØSKEN
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.util.*

interface KjerneRegel : Regel

@Component
@Order(HIGHEST_PRECEDENCE)
class StrengtFortroligRegel(@Value("\${gruppe.strengt}") private val id: UUID) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { bruker krever STRENGT_FORTROLIG && !(ansatt kanBehandle id) }

    override val metadata = Metadata(STRENGT_FORTROLIG)

}

@Component
@Order(HIGHEST_PRECEDENCE + 1)
class FortroligRegel(@Value("\${gruppe.fortrolig}") private val id: UUID) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { bruker krever FORTROLIG && !(ansatt kanBehandle id) }

    override val metadata = Metadata(FORTROLIG)
}

@Component
@Order(HIGHEST_PRECEDENCE + 2)
class EgenAnsattRegel(@Value("\${gruppe.egenansatt}") private val id: UUID) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { bruker krever EGEN_ANSATT && !(ansatt kanBehandle id) }

    override val metadata = Metadata(EGEN_ANSATT)
}

@Order(HIGHEST_PRECEDENCE + 3)
@Component
class EgneDataRegel(private val teller: EgneDataOppslagTeller) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { ansatt er bruker }.also {
            teller.increment(!it)
        }

    override val metadata = Metadata(EGNEDATA)
}

@Order(HIGHEST_PRECEDENCE + 4)
@Component
class ForeldreOgBarnRegel(private val teller: ForeldreBarnOppslagTeller) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { ansatt erForeldreEllerBarnTil bruker }

    override val metadata = Metadata(FORELDREBARN)
}

@Order(HIGHEST_PRECEDENCE + 5)
@Component
class PartnerRegel(private val teller: PartnerOppslagTeller) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { ansatt erNåværendeEllerTidligerePartnerTil bruker }.also {
            teller.increment(!it)
        }

    override val metadata = Metadata(PARTNER)
}

@Component
@Order(HIGHEST_PRECEDENCE + 6)
class SøskenRegel(private val teller: SøskenOppslagTeller) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { ansatt erSøskenTil bruker }.also {
            teller.increment(!it)
        }

    override val metadata = Metadata(SØSKEN)
}


fun Environment.id(gruppe: GlobalGruppe) = UUID.fromString(getRequiredProperty(gruppe.property))
