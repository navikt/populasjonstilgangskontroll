package no.nav.tilgangsmaskin.bruker.kodeverk

import no.nav.boot.conditionals.ConditionalOnGCP
import no.nav.sikkerhetstjenesten.felles.NoCoverageAnalysis
import no.nav.sikkerhetstjenesten.felles.cache.CacheOperations
import no.nav.sikkerhetstjenesten.felles.leder.LeaderAware
import no.nav.sikkerhetstjenesten.felles.rest.RestRetryingWhenRecoverableService
import no.nav.tilgangsmaskin.bruker.kodeverk.KodeverkConfig.Companion.KODEVERK
import no.nav.tilgangsmaskin.bruker.kodeverk.KodeverkConfig.Companion.KODEVERK_CACHE
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.service.registry.ImportHttpServices
import java.util.UUID
import java.util.concurrent.TimeUnit.MINUTES
import kotlin.time.measureTimedValue

@RestRetryingWhenRecoverableService
@ImportHttpServices(types = [KodeverkClient::class], group = KODEVERK)
class KodeverkTjeneste(private val client: KodeverkClient) {

    @Cacheable(cacheNames = [KODEVERK])
    fun navn(kode: String) = koderOgNavn()[kode]

    fun koderOgNavn(): Map<String, String> =
        runCatching {
            (client.bydeler().kodeOgNavn() + client.kommuner().kodeOgNavn())
                .associate { it.kode to it.tekst }
        }.getOrDefault(emptyMap())

    @NoCoverageAnalysis
    override fun toString() = "${javaClass.simpleName} [client=$client]"
}