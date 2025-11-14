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

@OrderedComponent(HIGHEST_PRECEDENCE)
class StrengtFortroligRegel : GlobalGruppeRegel(STRENGT_FORTROLIG), KjerneRegel

@OrderedComponent(HIGHEST_PRECEDENCE + 1)
class StrengtFortroligUtlandRegel : GlobalGruppeRegel(STRENGT_FORTROLIG_UTLAND), KjerneRegel

@OrderedComponent(HIGHEST_PRECEDENCE + 2)
class FortroligRegel : GlobalGruppeRegel(FORTROLIG), KjerneRegel

@OrderedComponent(HIGHEST_PRECEDENCE + 3)
class SkjermingRegel : GlobalGruppeRegel(SKJERMING), KjerneRegel

@OrderedComponent(HIGHEST_PRECEDENCE + 4)
class EgneDataRegel : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { ansatt erDenSammeSom bruker }

    override val metadata = RegelMetadata(EGNEDATA)
}

@OrderedComponent(HIGHEST_PRECEDENCE + 5)
class ForeldreOgBarnRegel : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { ansatt erForeldreEllerBarnTil bruker }

    override val metadata = RegelMetadata(FORELDREBARN)
}

@OrderedComponent(HIGHEST_PRECEDENCE + 6)
class PartnerRegel : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { ansatt erNåværendeEllerTidligerePartnerMed bruker }

    override val metadata = RegelMetadata(PARTNER)
}

@OrderedComponent(HIGHEST_PRECEDENCE + 7)
class SøskenRegel : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { ansatt erSøskenTil bruker }

    override val metadata = RegelMetadata(SØSKEN)
}


@OrderedComponent(HIGHEST_PRECEDENCE + 8)
class FellesBarnRegel : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { ansatt harFellesBarnMed bruker }

    override val metadata = RegelMetadata(FELLES_BARN)
}