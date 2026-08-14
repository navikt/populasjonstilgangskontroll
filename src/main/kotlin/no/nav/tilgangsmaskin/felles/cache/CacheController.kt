package no.nav.tilgangsmaskin.felles.cache

import io.swagger.v3.oas.annotations.Operation
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GEO_CACHE
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.GEO_OG_GLOBALE_CACHE
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.felles.rest.DevController
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.getLogger
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

private const val DEV_CACHE_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.dev.cache.tag.description"
private const val SUMMARY_CACHE_SKJERMINGER = "${MSG}openapi.dev.cache.skjerminger.summary"
private const val DESCRIPTION_CACHE_SKJERMINGER = "${MSG}openapi.dev.cache.skjerminger.description"
private const val SUMMARY_CACHE_PERSONER = "${MSG}openapi.dev.cache.personer.summary"
private const val DESCRIPTION_CACHE_PERSONER = "${MSG}openapi.dev.cache.personer.description"


@DevController(
    value = ["/${DEV}/cache"],
    name = "CacheController",
    description = DEV_CACHE_CONTROLLER_TAG_DESCRIPTION
)
class CacheController(private val cache: CacheOperations) {

    private val log = getLogger(javaClass)

    @PostMapping("/skjerminger")
    @Operation(summary = SUMMARY_CACHE_SKJERMINGER, description = DESCRIPTION_CACHE_SKJERMINGER)
    fun cacheSkjerminger(@RequestBody navIds: Set<String>) =
        cache.getMany<Boolean>(CacheNøkkelConfig(SKJERMING), navIds)

    @PostMapping("/personer")
    @Operation(summary = SUMMARY_CACHE_PERSONER, description = DESCRIPTION_CACHE_PERSONER)
    fun cachePersoner(@RequestBody navIds: Set<Identifikator>) =
        cache.getMany<Person>(CacheNøkkelConfig(PDL), navIds.mapTo(mutableSetOf()) { it.verdi })

    @DeleteMapping("/flush/{id}")
    //@Operation(summary = SUMMARY_CACHE_FLUSH, description = DESCRIPTION_CACHE_FLUSH)
    fun cacheFlush(@RequestBody id: AnsattId) =
        setOf(GEO_CACHE, GEO_OG_GLOBALE_CACHE, OID_CACHE).forEach { flush(it, id) }

    private fun flush(cfg: CacheNøkkelConfig, id: AnsattId) {
        cache.delete(cfg, id.verdi).also {
            if (it) log.info("Slettet cache innslag i cache ${cfg.fullName} for ${id.verdi}")
            else log.trace("Fant ikke cache innslag i cache ${cfg.fullName} for ${id.verdi}")
        }
    }
}