package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.SKJERMING
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.STRENGT_FORTROLIG_UTLAND
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.EGNEDATA
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.FELLES_BARN
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.FORELDREBARN
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.PARTNER
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.SØSKEN

@SortertRegel(RegelRekkefølge.STRENGT_FORTROLIG)
class StrengtFortroligRegel : GlobalGruppeMedlemskapRegel(STRENGT_FORTROLIG), KjerneRegel

@SortertRegel(RegelRekkefølge.STRENGT_FORTROLIG_UTLAND)
class StrengtFortroligUtlandRegel : GlobalGruppeMedlemskapRegel(STRENGT_FORTROLIG_UTLAND), KjerneRegel

@SortertRegel(RegelRekkefølge.FORTROLIG)
class FortroligRegel : GlobalGruppeMedlemskapRegel(FORTROLIG), KjerneRegel

@SortertRegel(RegelRekkefølge.SKJERMING)
class SkjermingRegel : GlobalGruppeMedlemskapRegel(SKJERMING), KjerneRegel

@SortertRegel(RegelRekkefølge.EGNE_DATA)
class EgneDataRegel : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            ansatt erDenSammeSom bruker
        }

    override val metadata = RegelMetadata(EGNEDATA)
}

@SortertRegel(RegelRekkefølge.FORELDRE_BARN)
class ForeldreOgBarnRegel : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            ansatt erForeldreEllerBarnTil bruker
        }

    override val metadata = RegelMetadata(FORELDREBARN)
}

@SortertRegel(RegelRekkefølge.PARTNER)
class PartnerRegel : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            ansatt erNåværendeEllerTidligerePartnerMed bruker
        }

    override val metadata = RegelMetadata(PARTNER)
}

@SortertRegel(RegelRekkefølge.SØSKEN)
class SøskenRegel : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            ansatt erSøskenTil bruker
        }

    override val metadata = RegelMetadata(SØSKEN)
}

@SortertRegel(RegelRekkefølge.FELLES_BARN)
class FellesBarnRegel : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            ansatt harFellesBarnMed bruker
        }

    override val metadata = RegelMetadata(FELLES_BARN)
}