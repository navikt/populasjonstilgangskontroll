package no.nav.tilgangsmaskin.felles.cache

import io.swagger.v3.oas.annotations.Operation
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.EntraGrupperConfig.Companion.ENTRA_CACHES
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private const val SUMMARY_CACHE_FLUSH = "${MSG}openapi.cache.flush.summary"
private const val DESCRIPTION_CACHE_FLUSH = "${MSG}openapi.cache.flush.description"
private const val CACHE = "/cache"
@Controller
@RequestMapping(CACHE)
class CacheFlushController {

    @GetMapping("flush")
    fun flushAnsatt(): String = "cache/flush"
}

@RestController
@RequestMapping(CACHE)
class CacheFlushIdController(private val cache: CacheOperations) {

    private val log = getLogger(javaClass)

    @DeleteMapping("{id}")
    @Operation(summary = SUMMARY_CACHE_FLUSH, description = DESCRIPTION_CACHE_FLUSH)
    fun flushId(@PathVariable id: AnsattId) : Boolean  {
        (ENTRA_CACHES + OID_CACHE).forEach { cacheNøkkel ->
            cache.delete(cacheNøkkel, id.verdi).also {
                if (it) {
                    log.info("Flush av cache ${cacheNøkkel.fullName} for $id OK")
                }
                else {
                    log.trace("Flush av cache {} for {} feilet", cacheNøkkel.fullName, id)
                }
            }
        }
        return true
    }
}
