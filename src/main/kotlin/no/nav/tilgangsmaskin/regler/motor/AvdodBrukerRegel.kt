package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.intervallSiden
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 3)
class AvdodBrukerRegel(private val teller: AvdodTeller) : OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        true.also {  // ikke feile
            if (bruker.dødsdato != null) {
                teller.tell(Tags.of("months", bruker.dødsdato.intervallSiden()))
            }
        }

    override val metadata = RegelMetadata(GruppeMetadata.AVDØD)
}