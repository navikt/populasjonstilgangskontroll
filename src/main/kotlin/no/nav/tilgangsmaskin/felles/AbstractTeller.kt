package no.nav.tilgangsmaskin.felles

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata
import no.nav.tilgangsmaskin.tilgang.Token

abstract class AbstractTeller(
        private val registry: MeterRegistry,
        private val token: Token,
        private val navn: String,
        private val beskrivelse: String) {

    fun tell(skalTelles: Boolean, metadata: GruppeMetadata) =
        tell(skalTelles, Tags.of("navn", metadata.name.replace("_", "").lowercase()))

    fun tell(tags: Tags) = tell(true, tags)

    fun tell(skalTelles: Boolean, tags: Tags) {
        if (!skalTelles) return

        Counter.builder(navn)
            .description(beskrivelse)
            .tag("system", token.system ?: "N/A")
            .tags(tags)
            .register(registry)
            .increment()
    }
}