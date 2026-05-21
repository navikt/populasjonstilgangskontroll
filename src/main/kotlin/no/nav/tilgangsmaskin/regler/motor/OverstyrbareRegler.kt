package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Tags
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.AVDØD
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UTENLANDSK
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.år
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.AVDØD_MER_ENN_ETT_ÅR
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.VERGEMÅL
import org.springframework.core.Ordered.LOWEST_PRECEDENCE


@SortertRegel(LOWEST_PRECEDENCE)
class GeografiskRegel(private val oppfølging: OppfølgingTjeneste,private val teller: OppfølgingkontorTeller) : GlobalGruppeMedlemskapRegel(NASJONAL), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        godtaHvis {
            ansatt erMedlemAv NASJONAL
                    || ansatt kanBehandle bruker.geografiskTilknytning
                    || (ansatt tilhører oppfølging.enhetFor(Identifikator(bruker.oppslagId))).also {
                teller.tell(Tags.of("resultat", "$it"))
            }
        }
}

@SortertRegel(LOWEST_PRECEDENCE - 1)
class UkjentBostedRegel : GlobalGruppeMedlemskapRegel(UKJENT_BOSTED), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            bruker.harUkjentBosted && ansatt ikkeErMedlemAv UKJENT_BOSTED
        }
}

@SortertRegel(LOWEST_PRECEDENCE - 2)
class UtlandRegel : GlobalGruppeMedlemskapRegel(UTENLANDSK), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            bruker.harUtenlandskBosted && ansatt ikkeErMedlemAv UTENLANDSK
        }
}

@SortertRegel(LOWEST_PRECEDENCE - 3)
@ConditionalOnNotProd
class AvdødBrukerRegel : OverstyrbarRegel {

    override val metadata = RegelMetadata(AVDØD_MER_ENN_ETT_ÅR)

    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            bruker harVærtDødMerEnn 1.år && ansatt ikkeErMedlemAv AVDØD
        }
}


@SortertRegel(LOWEST_PRECEDENCE - 4)
@ConditionalOnNotProd
class VergemålRegel(private val vergemål: VergemålTjeneste) : OverstyrbarRegel {

    override val metadata = RegelMetadata(VERGEMÅL)

    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            vergemål.vergemål(ansatt.ansattId).contains(bruker.brukerId)
        }
}
