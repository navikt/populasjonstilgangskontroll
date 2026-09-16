package no.nav.tilgangsmaskin.felles.cache

import io.swagger.v3.oas.annotations.Operation
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.felles.rest.DevController
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private const val SUMMARY_CACHE_FLUSH = "${MSG}openapi.dev.cache.flush.summary"
private const val DESCRIPTION_CACHE_FLUSH = "${MSG}openapi.dev.cache.flush.description"

@Controller
@RequestMapping("/cache")
class CacheFlushController {

    @GetMapping("flush")
    fun flushAnsatt(): String = "cache/flush"
}

@RestController
@RequestMapping("/cache")
class CacheFlushIdController(
    private val cache: CacheOperations,
) {

    private val log = getLogger(javaClass)

    @DeleteMapping("flushit/{id}/")
    @Operation(summary = SUMMARY_CACHE_FLUSH, description = DESCRIPTION_CACHE_FLUSH)
    fun flushId(@PathVariable id: AnsattId) =
        cache.delete(OID_CACHE, id.verdi).also {
            if (it) log.info("Slettet cache innslag i cache ${OID_CACHE.fullName} for ${id.verdi}")
            else log.trace("Fant ikke cache innslag i cache ${OID_CACHE.fullName} for ${id.verdi}")
        }
}
