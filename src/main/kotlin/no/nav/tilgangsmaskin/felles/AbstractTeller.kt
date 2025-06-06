package no.nav.tilgangsmaskin.felles

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.tilgang.Token

abstract class AbstractTeller(
        private val registry: MeterRegistry,
        private val token: Token,
        private val navn: String,
        private val beskrivelse: String) {


    open fun tell(tags: Tags) =
        with(tags.and("system", token.system)) {
            Counter.builder(navn)
                .description(beskrivelse)
                .tags(this)
                .register(registry)
                .increment()
        }
}
