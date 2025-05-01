package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.EGNEDATA
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.FORELDREBARN
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.PARTNER
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.SØSKEN
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

interface KjerneRegel : Regel

@Component
@Order(HIGHEST_PRECEDENCE)
class StrengtFortroligRegel : GlobalGruppeRegel(STRENGT_FORTROLIG), KjerneRegel

@Component
@Order(HIGHEST_PRECEDENCE + 1)
class StrengtFortroligUtlandRegel : GlobalGruppeRegel(STRENGT_FORTROLIG_UTLAND), KjerneRegel

@Component
@Order(HIGHEST_PRECEDENCE + 2)
class FortroligRegel : GlobalGruppeRegel(FORTROLIG), KjerneRegel

@Component
@Order(HIGHEST_PRECEDENCE + 3)
class SkjermingRegel : GlobalGruppeRegel(SKJERMING), KjerneRegel

@Order(HIGHEST_PRECEDENCE + 4)
@Component
class EgneDataRegel(private val teller: EgneDataOppslagTeller) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { ansatt erDenSammeSom bruker }.also {
            teller.tell(it)
        }

    override val metadata = RegelMetadata(EGNEDATA)
}

@Order(HIGHEST_PRECEDENCE + 5)
@Component
class ForeldreOgBarnRegel(private val teller: ForeldreBarnOppslagTeller) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { ansatt erForeldreEllerBarnTil bruker }.also {
            teller.tell(it)
        }

    override val metadata = RegelMetadata(FORELDREBARN)
}

@Order(HIGHEST_PRECEDENCE + 6)
@Component
class PartnerRegel(private val teller: PartnerOppslagTeller) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { ansatt erNåværendeEllerTidligerePartnerMed bruker }.also {
            teller.tell(it)
        }

    override val metadata = RegelMetadata(PARTNER)
}

@Component
@Order(HIGHEST_PRECEDENCE + 7)
class SøskenRegel(private val teller: SøskenOppslagTeller) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { ansatt erSøskenTil bruker }.also {
            teller.tell(it)
        }

    override val metadata = RegelMetadata(SØSKEN)
}



