package no.nav.tilgangsmaskin.felles

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.tilgang.Token

abstract class AbstractTeller(
        private val registry: MeterRegistry,
        private val token: Token,
        private val tellerNavn: String,
        private val beskrivelse: String) {

    fun tell(vararg tags: Pair<String, Any>) = tell(true, *tags)

    fun tell(avslått: Boolean, vararg tags: Pair<String, Any>) {
        if (!avslått) return

        Counter.builder(tellerNavn)
            .description(beskrivelse)
            .tag("system", token.system ?: "N/A")
            .apply { tags.forEach { tag(it.first, it.second.toString()) } }
            .register(registry)
            .increment()
    }
}