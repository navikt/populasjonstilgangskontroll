package no.nav.tilgangsmaskin.tilgang.dev

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelConfig
import no.nav.tilgangsmaskin.felles.cache.getMany
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

private const val DEV_CACHE_CONTROLLER_TAG_DESCRIPTION = "msg:openapi.dev.cache.tag.description"

@UnprotectedRestController(value = ["/${DEV}/cache"])
@ConditionalOnNotProd
@Tag(name = "DevCacheController", description = DEV_CACHE_CONTROLLER_TAG_DESCRIPTION)
class DevCacheController(private val cacheClient: CacheClient) {

    @PostMapping("cache/skjerminger")
    @Operation(summary = SUMMARY_CACHE_SKJERMINGER, description = DESCRIPTION_CACHE_SKJERMINGER)
    fun cacheSkjerminger(@RequestBody navIds: Set<String>) = cacheClient.getMany<Boolean>(CacheNøkkelConfig(SKJERMING),
        navIds)

    @PostMapping("cache/personer")
    @Operation(summary = SUMMARY_CACHE_PERSONER, description = DESCRIPTION_CACHE_PERSONER)
    fun cachePersoner(@RequestBody navIds: Set<Identifikator>) = cacheClient.getMany<Person>(CacheNøkkelConfig(PDL),
        navIds.map { it.verdi }.toSet())

    companion object {
        private const val SUMMARY_CACHE_SKJERMINGER = "msg:openapi.dev.cache.skjerminger.summary"
        private const val DESCRIPTION_CACHE_SKJERMINGER = "msg:openapi.dev.cache.skjerminger.description"
        private const val SUMMARY_CACHE_PERSONER = "msg:openapi.dev.cache.personer.summary"
        private const val DESCRIPTION_CACHE_PERSONER = "msg:openapi.dev.cache.personer.description"
    }
}