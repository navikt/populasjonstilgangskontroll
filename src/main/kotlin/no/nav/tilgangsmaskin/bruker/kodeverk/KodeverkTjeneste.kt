package no.nav.tilgangsmaskin.bruker.kodeverk

import no.nav.sikkerhetstjenesten.felles.NoCoverageAnalysis
import no.nav.sikkerhetstjenesten.felles.rest.RestRetryingWhenRecoverableService
import no.nav.tilgangsmaskin.bruker.kodeverk.KodeverkConfig.Companion.KODEVERK
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.service.registry.ImportHttpServices

@RestRetryingWhenRecoverableService
@ImportHttpServices(types = [KodeverkClient::class], group = KODEVERK)
class KodeverkTjeneste(private val client: KodeverkClient) {

    @Cacheable(cacheNames = [KODEVERK])
    fun navn(kode: String) =
         runCatching {
             koderOgNavn().singleOrNull {
                 it.kode == kode
             }?.tekst
         }.getOrNull()

    private fun koderOgNavn() =
        runCatching {
            client.bydeler().kodeOgNavn() + client.kommuner().kodeOgNavn()
        }.getOrElse {
            emptySet()
        }

    @NoCoverageAnalysis
    override fun toString() = "${javaClass.simpleName} [client=$client]"
}
