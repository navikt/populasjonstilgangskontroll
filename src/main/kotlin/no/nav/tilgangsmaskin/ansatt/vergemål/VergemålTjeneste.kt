package no.nav.tilgangsmaskin.ansatt.vergemål

import io.micrometer.observation.annotation.Observed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålClient.VergemålIdent
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.rest.RestRetryingWhenRecoverableService
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.service.registry.ImportHttpServices

@Observed
@RestRetryingWhenRecoverableService
@ImportHttpServices(types = [VergemålClient::class], group = VERGEMÅL)
class VergemålTjeneste(private val nom: NomTjeneste, private val client: VergemålClient) {

    @Cacheable(cacheNames = [VERGEMÅL], key = "#ansattId.verdi")
    fun alle(ansattId: AnsattId): Set<BrukerId> =
        nom.fnrForAnsatt(ansattId)?.let { fnr ->
            client.vergemål(VergemålIdent(fnr.verdi))
                .mapTo(sortedSetOf(compareBy { it.verdi })) { it.vergehaver }
        }.orEmpty()


    @NoCoverageAnalysis
    override fun toString() =
        "${javaClass.simpleName} [client=$client]"
}


