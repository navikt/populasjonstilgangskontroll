package no.nav.tilgangsmaskin.felles

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger

abstract class AbstractTeller(
        private val registry: MeterRegistry,
        private val token: Token,
        private val navn: String,
        private val beskrivelse: String) {

    private val log = getLogger(javaClass)

   open fun tell(skalTelles: Boolean, metadata: GruppeMetadata) =
        tell(skalTelles, Tags.of("navn", metadata.name.replace("_", "").lowercase()))

    fun tell(tags: Tags) = tell(true, tags)

    open fun tell(skalTelles: Boolean, tags: Tags) {
       if(!skalTelles) return

        log.debug("Registering counter with name: {} and tags: {}", navn, tags)

        Counter.builder(navn)
            .description(beskrivelse)
            .tags(tags.and("system", token.system ?: "N/A"))
            .register(registry)
            .increment()
    }
}

open class HabilitetTeller(
    private val registry: MeterRegistry,
    private val token: Token,
    private val navn: String,
    private val beskrivelse: String): AbstractTeller(registry, token, navn, beskrivelse) {

    private val log = getLogger(javaClass)

    override fun tell(godkjentTilgang: Boolean, metadata: GruppeMetadata) =
        tell(godkjentTilgang, Tags.of("navn", metadata.name.replace("_", "").lowercase()))

    override fun tell(godkjentTilgang: Boolean, tags: Tags) {
        if (godkjentTilgang) return

        log.debug("Registering counter with name: {} and tags: {}", navn, tags)

        Counter.builder(navn)
            .description(beskrivelse)
            .tags(tags.and("system", token.system ?: "N/A"))
            .register(registry)
            .increment()
    }
}