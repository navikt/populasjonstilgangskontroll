package no.nav.tilgangsmaskin.ansatt.kodeverk

import io.micrometer.observation.annotation.Observed
import no.nav.tilgangsmaskin.ansatt.kodeverk.KodeverkConfig.Companion.KODEVERK
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.rest.RestRetryingWhenRecoverableService
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.service.registry.ImportHttpServices

@Observed
@RestRetryingWhenRecoverableService
@ImportHttpServices(types = [KodeverkClient::class], group = KODEVERK)
class KodeverkTjeneste(private val client: KodeverkClient) {

    private fun koderOgNavn() =
        runCatching {
            client.bydeler().kodeOgNavn() + client.kommuner().kodeOgNavn()
        }.getOrElse {
            emptySet()
        }

    @Cacheable(cacheNames = [KODEVERK], key = "#kode")
    fun navn(kode: String) =
         runCatching {
             koderOgNavn().singleOrNull {
                 it.kode == kode
             }?.tekst
         }.getOrNull()

    @NoCoverageAnalysis
    override fun toString() = "${javaClass.simpleName} [client=$client]"
}

