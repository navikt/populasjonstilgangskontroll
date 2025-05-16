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

        log.trace("Registering counter with name: {} and tags: {}", navn, tags)

        Counter.builder(navn)
            .description(beskrivelse)
            .tags(tags.and("system", token.system))
            .register(registry)
           .increment()
    }
}

open class HabilitetTeller(registry: MeterRegistry, token: Token, navn: String, beskrivelse: String): AbstractTeller(registry, token, navn, beskrivelse) {

    override fun tell(godkjentTilgang: Boolean, metadata: GruppeMetadata) =
       super.tell(!godkjentTilgang, metadata)
    override fun tell(godkjentTilgang: Boolean, tags: Tags) {
       if (!godkjentTilgang) tell(tags.and("tilgang", "Avvist"))
    }
}