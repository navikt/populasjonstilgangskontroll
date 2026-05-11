package no.nav.tilgangsmaskin.ansatt.vergemål

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålClient.VergemålIdent
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableService
import org.springframework.cache.annotation.Cacheable

@RetryingWhenRecoverableService
class VergemålTjeneste(private val nom: NomTjeneste, private val client: VergemålClient) {

    @WithSpan
    @Cacheable(cacheNames = [VERGEMÅL], key = "#ansattId.verdi")
    fun vergemål(ansattId: AnsattId) =
        nom.fnrForAnsatt(ansattId)?.let { fnr ->
            client.vergemål(VergemålIdent(fnr.verdi))
                .mapTo(mutableSetOf()) { it.vergehaver }
                .toSet()
        } ?: emptySet()

    @Generated
    override fun toString() = "${javaClass.simpleName} [client=$client]"
}


