package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.EGNEDATA
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.FELLES_BARN
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
class EgneDataRegel(private val teller: HabilitetTeller) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { ansatt erDenSammeSom bruker }.also {
            teller.tell(it, EGNEDATA)
        }

    override val metadata = RegelMetadata(EGNEDATA)
}

@Order(HIGHEST_PRECEDENCE + 5)
@Component
class ForeldreOgBarnRegel(private val teller: HabilitetTeller) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { ansatt erForeldreEllerBarnTil bruker }.also {
            teller.tell(it, FORELDREBARN)
        }

    override val metadata = RegelMetadata(FORELDREBARN)
}

@Order(HIGHEST_PRECEDENCE + 6)
@Component
class PartnerRegel(private val teller: HabilitetTeller) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { ansatt erNåværendeEllerTidligerePartnerMed bruker }.also {
            teller.tell(it, PARTNER)
        }

    override val metadata = RegelMetadata(PARTNER)
}

@Component
@Order(HIGHEST_PRECEDENCE + 7)
class SøskenRegel(private val teller: HabilitetTeller) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { ansatt erSøskenTil bruker }.also {
            teller.tell(it, SØSKEN)
        }

    override val metadata = RegelMetadata(SØSKEN)
}

@Component
@Order(HIGHEST_PRECEDENCE + 8)
class FellesBarnRegel(private val teller: HabilitetTeller) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { ansatt harFellesBarnMed bruker }.also {
            teller.tell(it, FELLES_BARN)
        }

    override val metadata = RegelMetadata(FELLES_BARN)
}


