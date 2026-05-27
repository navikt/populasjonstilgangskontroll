package no.nav.tilgangsmaskin.regler.motor

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe.AVDØD
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.utils.Auditor
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.intervallSiden
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.år
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.AVDØD_MER_ENN_ETT_ÅR
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.core.Ordered.LOWEST_PRECEDENCE

@SortertRegel(LOWEST_PRECEDENCE - 3)
@ConditionalOnNotProd
class AvdødBrukerRegel : OverstyrbarRegel {

    override val metadata = RegelMetadata(AVDØD_MER_ENN_ETT_ÅR)

    override fun evaluer(ansatt: Ansatt, bruker: Bruker)  =
         avvisHvis {
            bruker harVærtDødMerEnn 1.år && ansatt ikkeErMedlemAv AVDØD
        }
}
