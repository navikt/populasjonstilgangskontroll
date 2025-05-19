package no.nav.tilgangsmaskin.felles

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger

abstract class AbstractTeller(
        private val registry: MeterRegistry,
        private val token: Token,
        private val navn: String,
        private val beskrivelse: String) {

    private val log = getLogger(javaClass)

    open fun tell(tags: Tags) =
        with(tags.and("system", token.system)) {
           // log.info("Registrerer teller med navn: {} og tags: {}", navn, this)
            Counter.builder(navn)
                .description(beskrivelse)
                .tags(this)
                .register(registry)
                .increment()
        }
}
