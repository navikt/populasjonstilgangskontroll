package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.intervallSiden
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.AVDØD
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
class AvdødBrukerRegel(private val teller: AvdødTeller) : TellendeRegel {
    override val predikat = { _: Ansatt, bruker: Bruker -> bruker.dødsdato != null }

    override fun tell(ansatt: Ansatt, bruker: Bruker) =
        teller.tell(Tags.of("months", bruker.dødsdato!!.intervallSiden()))
    override val metadata = RegelMetadata(AVDØD)
}