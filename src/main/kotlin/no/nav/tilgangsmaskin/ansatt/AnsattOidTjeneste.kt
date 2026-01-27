package no.nav.tilgangsmaskin.ansatt

import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.ansatt.graph.EntraRestClientAdapter
import no.nav.tilgangsmaskin.ansatt.graph.EntraRestClientAdapter.OidException
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException
import java.time.Duration

@Component
class AnsattOidTjeneste(private val adapter: EntraRestClientAdapter) : CachableRestConfig {


    @Cacheable(cacheNames = [ENTRA_OID],key = "#ansattId.verdi")
     fun oidFraEntra(ansattId: AnsattId) = adapter.oidFraEntra(ansattId.verdi)

    override val varighet = Duration.ofDays(365)  // Godt nok, blås i skuddår
    override val navn = ENTRA_OID
    override val caches = listOf(OID_CACHE)

    companion object {
        const val ENTRA_OID = "entraoid"
    }
}