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
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.felles.rest.DevController
import no.nav.tilgangsmaskin.felles.security.OOAuth2RequireOBO
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.TEXT_HTML
import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.server.ResponseStatusException

const val DESCRIPTION_CACHE_FLUSH: String = "${MSG}openapi.dev.cache.flush.description"
const val SUMMARY_CACHE_FLUSH: String = "${MSG}openapi.dev.cache.flush.summary"
private const val DEV_CACHE_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.dev.cache.tag.description"
private const val SUMMARY_CACHE_SKJERMINGER = "${MSG}openapi.dev.cache.skjerminger.summary"
private const val DESCRIPTION_CACHE_SKJERMINGER = "${MSG}openapi.dev.cache.skjerminger.description"
private const val SUMMARY_CACHE_PERSONER = "${MSG}openapi.dev.cache.personer.summary"
private const val DESCRIPTION_CACHE_PERSONER = "${MSG}openapi.dev.cache.personer.description"
private const val SUMMARY_CACHE_FLUSH_ALL = "${MSG}openapi.dev.cache.flush.all.summary"
private const val DESCRIPTION_CACHE_FLUSH_ALL = "${MSG}openapi.dev.cache.flush.all.description"
private const val SUMMARY_CACHE_FLUSH_DB = "${MSG}openapi.dev.cache.flush.db.summary"
private const val DESCRIPTION_CACHE_FLUSH_DB = "${MSG}openapi.dev.cache.flush.db.description"
private const val SUMMARY_CACHE_VG = "${MSG}openapi.dev.cache.vg.summary"
private const val DESCRIPTION_CACHE_VG = "${MSG}openapi.dev.cache.vg.description"


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

    @DeleteMapping("cache/flush/{id}/")
    @Operation(summary = SUMMARY_CACHE_FLUSH, description = DESCRIPTION_CACHE_FLUSH)
    fun cacheFlushSingle(@PathVariable id: AnsattId) =
        setOf(GEO_CACHE, GEO_OG_GLOBALE_CACHE, OID_CACHE).forEach { flush(it, id) }

    @DeleteMapping("cache/flush/{cacheName}/all")
    @Operation(summary = SUMMARY_CACHE_FLUSH_ALL, description = DESCRIPTION_CACHE_FLUSH_ALL)
    fun cacheFlushAll(@PathVariable cacheName: String) =
        with(alleNøkkelConfigs[cacheName]
            ?: throw ResponseStatusException(NOT_FOUND,
                "Ukjent cache: $cacheName. Tilgjengelige cacher: ${alleNøkkelConfigs.keys}")) {
            log.info("Cache størrelse før tømming for {}: {}", cacheName, cache.size(this))
             cache.clear(this).also {
                log.info("Tømte hele cachen {} og slettet {} nøkler", cacheName, it)
            }
        }

    @DeleteMapping("db/flush")
    @Operation(summary = SUMMARY_CACHE_FLUSH_DB, description = DESCRIPTION_CACHE_FLUSH_DB)
    fun cacheFlushDB() =
        cache.clearAll().also {
            log.info("Tømte hele databasen og slettet {} nøkler", it)
        }

    //@OOAuth2RequireOBO
    @GetMapping("reset", produces = [TEXT_HTML_VALUE])
    @Operation(summary = SUMMARY_CACHE_VG, description = DESCRIPTION_CACHE_VG)
    fun vgKnapp(): ResponseEntity<String> =
        ResponseEntity.ok()
            .contentType(TEXT_HTML)
            .body(
                $$"""
                <!doctype html>
                <html lang="no">
                <head>
                    <meta charset="utf-8">
                    <title>Flush cache</title>
                </head>
                <body>
                    <label for="ansattId">AnsattId</label>
                    <input id="ansattId" name="ansattId" type="text" />
                    <button type="button" onclick="flushCache()">Flush cache</button>
                    <p id="status"></p>
                    <script>
                        async function flushCache() {
                            const ansattId = document.getElementById('ansattId').value.trim();
                            const status = document.getElementById('status');
                            if (!ansattId) {
                                status.textContent = 'AnsattId må fylles ut';
                                return;
                            }

                            const response = await fetch(`cache/flush/${encodeURIComponent(ansattId)}/`, {
                                method: 'DELETE'
                            });

                            if (response.ok) {
                                status.textContent = `Cache flushet for ${ansattId}`;
                                return;
                            }

                            const contentType = response.headers.get('content-type') ?? '';
                            if (contentType.includes('json')) {
                                const problem = await response.json();
                                status.textContent = problem.detail ?? `Flush feilet: ${response.status}`;
                                return;
                            }

                            status.textContent = `Flush feilet: ${response.status}`;
                        }
                    </script>
                </body>
                </html>
                """.trimIndent()
            )

    private fun flush(cfg: CacheNøkkelConfig, id: AnsattId) {
        cache.delete(cfg, id.verdi).also {
            if (it) log.info("Slettet cache innslag i cache ${cfg.fullName} for ${id.verdi}")
            else log.trace("Fant ikke cache innslag i cache ${cfg.fullName} for ${id.verdi}")
        }
    }

}