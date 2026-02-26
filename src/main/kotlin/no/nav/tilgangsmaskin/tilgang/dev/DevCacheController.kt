package no.nav.tilgangsmaskin.populasjonstilgangskontroll.Tilgang

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.cache.Caches
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody


@UnprotectedRestController(value = ["/${DEV}/cache"])
@ConditionalOnNotProd
@Tag(name = "DevCacheController", description = "Denne kontrolleren skal kun brukes til testing")
class DevCacheController(
    private val cacheClient: CacheClient) {



    @PostMapping("cache/skjerminger")
    fun cacheSkjerminger(@RequestBody  navIds: Set<String>) = cacheClient.getMany(CachableConfig(SKJERMING),
        navIds, Boolean::class)


    @PostMapping("cache/personer")
    fun cachePersoner(@RequestBody  navIds: Set<Identifikator>) = cacheClient.getMany(CachableConfig(PDL),
        navIds.map { it.verdi }.toSet(), Person::class)

    @GetMapping("cache/keys/{cache}")
    fun keys(@PathVariable @Schema(description = "Cache navn", enumAsRef = true)
             cache: Caches) =
        Caches.forNavn(cache.name).flatMap {
            cacheClient.getAllKeys(it)
        }.toSortedSet()
}