package no.nav.tilgangsmaskin.ansatt.graph

import io.micrometer.observation.annotation.Observed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.rest.RestRetryingWhenRecoverableService
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.service.registry.ImportHttpServices
import java.util.*

@Observed
@RestRetryingWhenRecoverableService
@ImportHttpServices(types = [EntraGrupperClient::class], group = GRAPH)
class EntraTjeneste(
    private val client: EntraGrupperClient,
    private val cfg: EntraGrupperConfig
) {

    @Cacheable(cacheNames = [GRAPH], key = "#root.methodName + ':' + #ansattId.verdi")
    fun geoOgGlobaleGrupper(ansattId: AnsattId, oid: UUID) =
        grupper("$oid", true)

    @Cacheable(cacheNames = [GRAPH], key = "#root.methodName + ':' + #ansattId.verdi")
    fun geoGrupper(ansattId: AnsattId, oid: UUID) =
        grupper("$oid", false)

    private fun grupper(ansattId: String, trengerGlobaleGrupper: Boolean): Set<EntraGruppe> =
        generateSequence(client.grupper(cfg.grupperURI(ansattId, trengerGlobaleGrupper))) { bolk ->
            bolk.next?.let(client::grupper)
        }.flatMapTo(mutableSetOf()) { it.value }

    @NoCoverageAnalysis
    override fun toString() = "${javaClass.simpleName} [client=$client, config=$cfg]"
}

