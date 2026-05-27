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
class AvdødBrukerRegel(
    private val auditor: Auditor,
    private val teller: AvdødTeller,
    private val proxy: EntraProxyTjeneste,
    private val token: Token,
) : OverstyrbarRegel, TellendeRegel {

    override val metadata = RegelMetadata(AVDØD_MER_ENN_ETT_ÅR)

    override val skalTelle = {
        _: Ansatt, bruker: Bruker -> bruker harVærtDødMerEnn 1.år
    }

    override fun evaluer(ansatt: Ansatt, bruker: Bruker): Boolean {
        if (skalTelle(ansatt, bruker)) {
            tell(ansatt, bruker)
        }
        return avvisHvis {
            bruker harVærtDødMerEnn 1.år && ansatt ikkeErMedlemAv AVDØD
        }
    }

    override fun tell(ansatt: Ansatt, bruker: Bruker) {
        val intervall = bruker.dødsdato!!.intervallSiden()
        val enhet = enhet(ansatt)
        teller.tell(intervall, enhet)
        if (enhet != UTILGJENGELIG) {
            auditor.info("Ansatt ${ansatt.ansattId.verdi} i enhet $enhet fikk tilgang til forlengst avdød bruker ${bruker.brukerId.verdi} fra applikasjon ${token.system}")
        }
    }

    private fun enhet(ansatt: Ansatt) =
        runCatching { proxy.enhet(ansatt.ansattId).navn }.getOrDefault(UTILGJENGELIG)
}
