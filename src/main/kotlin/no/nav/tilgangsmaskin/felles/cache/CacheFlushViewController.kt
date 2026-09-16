package no.nav.tilgangsmaskin.felles.cache

import io.swagger.v3.oas.annotations.Operation
import no.nav.tilgangsmaskin.felles.rest.DevController
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

private const val SUMMARY_CACHE_VG = "${MSG}openapi.dev.cache.vg.summary"
private const val DESCRIPTION_CACHE_VG = "${MSG}openapi.dev.cache.vg.description"


@Controller("/${DEV}/cache")
class CacheFlushViewController {

    @GetMapping("flush")
    @Operation(summary = SUMMARY_CACHE_VG, description = DESCRIPTION_CACHE_VG)
    fun flushAnsatt(): String = "dev/cache/flush"
}
