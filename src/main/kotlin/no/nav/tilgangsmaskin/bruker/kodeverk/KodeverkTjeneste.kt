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

@ConditionalOnGCP
class CacheKodeverk(private val kodeverk: KodeverkTjeneste, private val cache: CacheOperations) : LeaderAware(true) {

    private val log = getLogger(javaClass)

    @Scheduled(fixedRate = INTERVAL_MINUTES, timeUnit = MINUTES, initialDelay = 1)
    fun oppdaterCache() =
        somLeder {
            val måling = measureTimedValue {
                runCatching {
                    val koder = kodeverk.koderOgNavn()
                    cache.putMany(KODEVERK_CACHE, koder)
                    koder
                }
            }
            måling.value.onSuccess {
                log.info(
                    "Periodisk cache-oppdatering OK, la til {} koder i cache på {}ms",
                    it.size,
                    måling.duration.inWholeMilliseconds
                )
            }.onFailure {
                log.warn("Periodisk cache-oppdatering feilet", it)
            }
        }

    companion object {
        const val KODER = "koder"
        private const val INTERVAL_MINUTES = 15L
    }
}