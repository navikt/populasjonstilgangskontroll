package no.nav.tilgangsmaskin.regler.motor

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.AVDØD
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.UTENLANDSK
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.år
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.AVDØD_MER_ENN_ETT_ÅR
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.VERGEMÅL

@SortertRegel(RegelRekkefølge.GEOGRAFISK)
class GeografiskRegel(private val oppfølging: OppfølgingTjeneste) : GlobalGruppeMedlemskapRegel(NASJONAL), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        godtaHvis {
            ansatt erMedlemAv NASJONAL
                    || ansatt kanBehandle bruker.geografiskTilknytning
                    || ansatt tilhører oppfølging.enhetFor(Identifikator(bruker.oppslagId))
        }
}

@SortertRegel(RegelRekkefølge.UKJENT_BOSTED)
class UkjentBostedRegel : GlobalGruppeMedlemskapRegel(UKJENT_BOSTED), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            bruker.harUkjentBosted && ansatt ikkeErMedlemAv UKJENT_BOSTED
        }
}

@SortertRegel(RegelRekkefølge.UTLAND)
class UtlandRegel : GlobalGruppeMedlemskapRegel(UTENLANDSK), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            bruker.harUtenlandskBosted && ansatt ikkeErMedlemAv UTENLANDSK
        }
}


@SortertRegel(RegelRekkefølge.AVDØD)
@ConditionalOnNotProd
class AvdødBrukerRegel : OverstyrbarRegel {

    override val metadata = RegelMetadata(AVDØD_MER_ENN_ETT_ÅR)

    override fun evaluer(ansatt: Ansatt, bruker: Bruker)  =
        avvisHvis {
            bruker harVærtDødMerEnn 1.år && ansatt ikkeErMedlemAv AVDØD
        }
}

@SortertRegel(RegelRekkefølge.VERGEMÅL)
class VergemålRegel(private val vergemål: VergemålTjeneste) : OverstyrbarRegel {

    override val metadata = RegelMetadata(VERGEMÅL)

    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            runCatching {
                bruker.brukerId in vergemål.vergemål(ansatt.ansattId)
            }.getOrDefault(false)
        }
}