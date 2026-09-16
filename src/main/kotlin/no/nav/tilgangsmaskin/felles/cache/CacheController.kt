package no.nav.tilgangsmaskin.felles.cache

import io.swagger.v3.oas.annotations.Operation
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.felles.rest.DevController
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.server.ResponseStatusException

private const val DEV_CACHE_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.dev.cache.tag.description"
private const val SUMMARY_CACHE_SKJERMINGER = "${MSG}openapi.dev.cache.skjerminger.summary"
private const val DESCRIPTION_CACHE_SKJERMINGER = "${MSG}openapi.dev.cache.skjerminger.description"
private const val SUMMARY_CACHE_PERSONER = "${MSG}openapi.dev.cache.personer.summary"
private const val DESCRIPTION_CACHE_PERSONER = "${MSG}openapi.dev.cache.personer.description"
private const val SUMMARY_CACHE_FLUSH_ALL = "${MSG}openapi.dev.cache.flush.all.summary"
private const val DESCRIPTION_CACHE_FLUSH_ALL = "${MSG}openapi.dev.cache.flush.all.description"
private const val SUMMARY_CACHE_FLUSH_DB = "${MSG}openapi.dev.cache.flush.db.summary"
private const val DESCRIPTION_CACHE_FLUSH_DB = "${MSG}openapi.dev.cache.flush.db.description"


@DevController(
    value = ["/${DEV}/cache"],
    name = "CacheController",
    description = DEV_CACHE_CONTROLLER_TAG_DESCRIPTION
)
class CacheController(
    private val cache: CacheOperations,
    private val cacheConfigs: List<CachableRestConfig>,
) {

    private val log = getLogger(javaClass)
    private val alleNøkkelConfigs by lazy {
        cacheConfigs.flatMap { it.caches }.associateBy { it.fullName }
    }

    @PostMapping("cache/skjerminger")
    @Operation(summary = SUMMARY_CACHE_SKJERMINGER, description = DESCRIPTION_CACHE_SKJERMINGER)
    fun cacheSkjerminger(@RequestBody navIds: Set<String>) =
        cache.getMany<Boolean>(CacheNøkkelConfig(SKJERMING), navIds)

    @PostMapping("cache/personer")
    @Operation(summary = SUMMARY_CACHE_PERSONER, description = DESCRIPTION_CACHE_PERSONER)
    fun cachePersoner(@RequestBody navIds: Set<Identifikator>) =
        cache.getMany<Person>(CacheNøkkelConfig(PDL), navIds.mapTo(mutableSetOf()) { it.verdi })

    @DeleteMapping("flushCache/{cacheName}")
    @Operation(summary = SUMMARY_CACHE_FLUSH_ALL, description = DESCRIPTION_CACHE_FLUSH_ALL)
    fun flushCache(@PathVariable cacheName: String) =
        with(alleNøkkelConfigs[cacheName]
            ?: throw ResponseStatusException(NOT_FOUND,
                "Ukjent cache: $cacheName. Tilgjengelige cacher: ${alleNøkkelConfigs.keys}")) {
            log.info("Cache størrelse før tømming for {}: {}", cacheName, cache.size(this))
             cache.clear(this).also {
                log.info("Tømte hele cachen {} og slettet {} nøkler", cacheName, it)
            }
        }

    @DeleteMapping("flushDB")
    @Operation(summary = SUMMARY_CACHE_FLUSH_DB, description = DESCRIPTION_CACHE_FLUSH_DB)
    fun flushDB() =
        cache.clearAll().also {
            log.info("Tømte hele databasen og slettet {} nøkler", it)
        }

}