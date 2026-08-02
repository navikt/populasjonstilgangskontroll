package no.nav.tilgangsmaskin.felles.cache

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import no.nav.tilgangsmaskin.felles.cache.CacheController.Companion.DEV_CACHE_CONTROLLER_TAG_DESCRIPTION
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody


@UnprotectedRestController( ["/${DEV}/cache"])
@ConditionalOnNotProd
@Tag(name = "CacheController", description = DEV_CACHE_CONTROLLER_TAG_DESCRIPTION)
class CacheController(private val cache: CacheOperations) {

    @PostMapping("cache/skjerminger")
    @Operation(summary = SUMMARY_CACHE_SKJERMINGER, description = DESCRIPTION_CACHE_SKJERMINGER)
    fun cacheSkjerminger(@RequestBody navIds: Set<String>) =
        cache.getMany<Boolean>(CacheNøkkelConfig(SKJERMING), navIds)

    @PostMapping("cache/personer")
    @Operation(summary = SUMMARY_CACHE_PERSONER, description = DESCRIPTION_CACHE_PERSONER)
    fun cachePersoner(@RequestBody navIds: Set<Identifikator>) =
        cache.getMany<Person>(CacheNøkkelConfig(PDL), navIds.mapTo(mutableSetOf()) { it.verdi })

    companion object {
        private const val DEV_CACHE_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.dev.cache.tag.description"
        private const val SUMMARY_CACHE_SKJERMINGER = "${MSG}openapi.dev.cache.skjerminger.summary"
        private const val DESCRIPTION_CACHE_SKJERMINGER = "${MSG}openapi.dev.cache.skjerminger.description"
        private const val SUMMARY_CACHE_PERSONER = "${MSG}openapi.dev.cache.personer.summary"
        private const val DESCRIPTION_CACHE_PERSONER = "${MSG}openapi.dev.cache.personer.description"
    }
}