package no.nav.tilgangsmaskin.ansatt.vergemål

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålClient.VergemålIdent
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableRestService
import org.springframework.cache.annotation.Cacheable

@RetryingWhenRecoverableRestService
class VergemålTjeneste(private val nom: NomTjeneste, private val client: VergemålClient) {

    @WithSpan
    @Cacheable(cacheNames = [VERGEMÅL], key = "#ansattId.verdi")
    fun vergemål(ansattId: AnsattId): Set<BrukerId> =
        nom.fnrForAnsatt(ansattId)?.let { fnr ->
            client.vergemål(VergemålIdent(fnr.verdi))
                .map { it.vergehaver }
                .toSortedSet(compareBy { it.verdi })
        } ?: emptySet()


    @NoCoverageAnalysis
    override fun toString() =
        "${javaClass.simpleName} [client=$client]"
}


