package no.nav.tilgangsmaskin.ansatt.graph

import io.micrometer.core.annotation.Timed
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableRestService
import org.springframework.cache.annotation.Cacheable
import java.util.*

@RetryingWhenRecoverableRestService
@Timed
class EntraTjeneste(
    private val client: EntraGrupperClient,
    private val cfg: EntraGrupperConfig
) {

    @Cacheable(cacheNames = [GRAPH], key = "#root.methodName + ':' + #ansattId.verdi")
    @WithSpan
    fun geoOgGlobaleGrupper(ansattId: AnsattId, oid: UUID) =
        grupper("$oid", true)

    @Cacheable(cacheNames = [GRAPH], key = "#root.methodName + ':' + #ansattId.verdi")
    @WithSpan
    fun geoGrupper(ansattId: AnsattId, oid: UUID) =
        grupper("$oid", false)

    private fun grupper(ansattId: String, trengerGlobaleGrupper: Boolean): Set<EntraGruppe> =
        generateSequence(client.grupper(cfg.grupperURI(ansattId, trengerGlobaleGrupper))) { bolk ->
            bolk.next?.let(client::grupper)
        }.flatMapTo(mutableSetOf()) { it.value }

    @NoCoverageAnalysis
    override fun toString() = "${javaClass.simpleName} [client=$client, config=$cfg]"
}

