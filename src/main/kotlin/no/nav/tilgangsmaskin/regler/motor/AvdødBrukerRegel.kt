package no.nav.tilgangsmaskin.regler.motor

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.boot.conditionals.ConditionalOnProd
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.utils.Auditor
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.intervallSiden
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.AVDØD
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(LOWEST_PRECEDENCE - 3)
@ConditionalOnProd
class AvdødBrukerRegel(private val teller: AvdødTeller, private val proxy: EntraProxyTjeneste, private val auditor: Auditor, private val token: Token) : TellendeRegel {

    override val metadata = RegelMetadata(AVDØD)

    override val skalTelle = { _: Ansatt, bruker: Bruker -> bruker.dødsdato != null }

    override fun tell(ansatt: Ansatt, bruker: Bruker) {
        val intervall = bruker.dødsdato!!.intervallSiden()
        with(enhet(intervall, ansatt)) {
            teller.tell(intervall, this)
            if (this != UTILGJENGELIG)  {
                auditor.info("Ansatt ${ansatt.ansattId.verdi} i enhet $this fikk tilgang til forlengst avdød bruker ${bruker.brukerId.verdi} fra applikasjon ${token.system}")
            }
        }
    }

    private fun enhet(intervall: Dødsperiode, ansatt: Ansatt) =
        runCatching {
            intervall.takeIf { it == Dødsperiode.MND_13_24 || it == Dødsperiode.MND_OVER_24 }
                ?.let { proxy.enhet(ansatt.ansattId).navn }
                ?: UTILGJENGELIG
        }.getOrDefault(UTILGJENGELIG)
}

@ConditionalOnNotProd
@Order(LOWEST_PRECEDENCE - 3)
@Component
class AvdødBrukerDevRegel(private val proxy: EntraProxyTjeneste, private val auditor: Auditor, private val token: Token) : GlobalGruppeRegel(
    GlobalGruppe.AVDØD)  {

    override fun evaluer(ansatt: Ansatt, bruker: Bruker) : Boolean {
        return true
        //return bruker.dødsdato?.månederSidenIdag()?.let { it > 12 } ?: false
    }



}
