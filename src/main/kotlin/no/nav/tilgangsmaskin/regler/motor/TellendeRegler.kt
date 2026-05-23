package no.nav.tilgangsmaskin.regler.motor

import no.nav.boot.conditionals.ConditionalOnProd
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.utils.Auditor
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.intervallSiden
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.AVDØD_MER_ENN_ETT_ÅR
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.VERGEMÅL
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.Ordered.LOWEST_PRECEDENCE

@SortertRegel(LOWEST_PRECEDENCE - 3)
@ConditionalOnProd
class VergemålTellendeRegel(private val vergemål: VergemålTjeneste, private val auditor: Auditor, private val teller: VergemålTeller) : TellendeRegel {

    private val log = getLogger(javaClass)

    override val metadata = RegelMetadata(VERGEMÅL)

    override val skalTelle = { ansatt: Ansatt, bruker: Bruker ->
        runCatching {
            vergemål.vergemål(ansatt.ansattId).contains(bruker.brukerId).also {
                if (it) {
                    auditor.info("Ansatt ${ansatt.ansattId.verdi} har vergemål for bruker ${bruker.brukerId.verdi}")
                }
            }
        }.getOrElse {
            log.error("Feil ved sjekk av vergemål for ansatt ${ansatt.ansattId.verdi}", it)
            false
        }
    }

    override fun tell(ansatt: Ansatt, bruker: Bruker) =
        teller.tell()
}

@SortertRegel(LOWEST_PRECEDENCE - 4)
@ConditionalOnProd
class AvdødBrukerTellendeRegel(private val teller: AvdødTeller, private val proxy: EntraProxyTjeneste, private val auditor: Auditor, private val token: Token) : TellendeRegel {

    override val metadata = RegelMetadata(AVDØD_MER_ENN_ETT_ÅR)

    override val skalTelle = { _: Ansatt, bruker: Bruker -> bruker.dødsdato != null }

    override fun tell(ansatt: Ansatt, bruker: Bruker) {
        val intervall = bruker.dødsdato!!.intervallSiden()
        with(enhet(intervall, ansatt)) {
            teller.tell(intervall, this)
            if (this != UTILGJENGELIG) {
                auditor.info("Ansatt ${ansatt.ansattId.verdi} i enhet $this fikk tilgang til forlengst avdød bruker ${bruker.brukerId.verdi} fra applikasjon ${token.system}")
            }
        }
    }

    private fun enhet(intervall: Dødsperiode, ansatt: Ansatt) =
        runCatching {
            intervall.takeIf { it >= Dødsperiode.MND_13_24 }
                ?.let { proxy.enhet(ansatt.ansattId).navn }
                ?: UTILGJENGELIG
        }.getOrDefault(UTILGJENGELIG)
}
