package no.nav.tilgangsmaskin.felles

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata
import no.nav.tilgangsmaskin.tilgang.Token

abstract class AbstractTeller(
        private val registry: MeterRegistry,
        private val token: Token,
        private val navn: String,
        private val beskrivelse: String) {

    fun tell(skalTelles: Boolean, metadata: GruppeMetadata) =
        tell(skalTelles, *arrayOf("type" to metadata.name.replace("_", "").lowercase()))

    fun tell(vararg tags: Pair<String, Any>) = tell(true, *tags)

    fun tell(skalTelles: Boolean, vararg tags: Pair<String, Any>) {
        if (!skalTelles) return

        Counter.builder(navn)
            .description(beskrivelse)
            .tag("system", token.system ?: "N/A")
            .apply { tags.forEach { tag(it.first, it.second.toString()) } }
            .register(registry)
            .increment()
    }
}