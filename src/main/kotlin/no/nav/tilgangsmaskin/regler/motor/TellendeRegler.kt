package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.utils.Auditor
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_13_24
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode.MND_OVER_24
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.intervallSiden
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.AVDØD
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

interface TellendeRegel : Regel {
    val predikat: (Ansatt, Bruker) -> Boolean
        get() = { _, _ -> false }
    fun tell(ansatt: Ansatt, bruker: Bruker) = Unit

    override fun evaluer(ansatt: Ansatt, bruker: Bruker): Boolean {
        if (predikat(ansatt, bruker)) {
            tell(ansatt, bruker)
        }
        return true
    }
}

@Component
@Order(LOWEST_PRECEDENCE - 3)
class AvdødBrukerRegel(private val teller: AvdødTeller, private val proxy: EntraProxyTjeneste, private val auditor: Auditor = Auditor()) : TellendeRegel {

    private val log = getLogger(javaClass)
    override val predikat = { _: Ansatt, bruker: Bruker -> bruker.dødsdato != null }

    override fun tell(ansatt: Ansatt, bruker: Bruker) {
        val intervall = bruker.dødsdato!!.intervallSiden()
        val enhet = enhet(intervall, ansatt)
        teller.tell(Tags.of("months", intervall.tekst, "enhet", enhet))
        if (enhet != UTILGJENGELIG)  {
            auditor.info("Ansatt ${ansatt.ansattId.verdi} i enhet $enhet fikk tilgang til forlengst avdød bruker ${bruker.brukerId.verdi}")
        }
    }

    private fun enhet(intervall: TimeExtensions.Dødsperiode,
                      ansatt: Ansatt): String {
        val enhet =
            if (intervall == MND_13_24 || intervall == MND_OVER_24) {
                proxy.enhet(ansatt.ansattId).enhetnummer.verdi
            } else {
                UTILGJENGELIG
            }
        return enhet
    }

    override val metadata = RegelMetadata(AVDØD)
}