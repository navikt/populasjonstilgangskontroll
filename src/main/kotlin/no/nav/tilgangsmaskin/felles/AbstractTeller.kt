package no.nav.tilgangsmaskin.felles

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.tilgang.Token

abstract class AbstractTeller(
    private val registry: MeterRegistry,
    private val token: Token,
    private val navn: String,
    private val beskrivelse: String) {

    fun tell(vararg tags: Tag, n: Int = 1) =
        tell(Tags.of(*tags), n)

    fun tell(tags: Tags = Tags.empty(), n: Int = 1) =
        Counter.builder(navn)
            .description(beskrivelse)
            .tags(tags
                .and("system", token.system)
                .and("clustersystem", token.clusterAndSystem))
            .register(registry)
            .increment(n.toDouble())
}

abstract class AbstractAsyncTeller(
    private val registry: MeterRegistry,
    private val navn: String,
    private val beskrivelse: String) {

    fun tell(n: Int = 1) =
        Counter.builder(navn)
            .description(beskrivelse)
            .register(registry)
            .increment(n.toDouble())
}

