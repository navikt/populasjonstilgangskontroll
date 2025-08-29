package no.nav.tilgangsmaskin.ansatt

import no.nav.tilgangsmaskin.ansatt.graph.EntraRestClientAdapter
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class AnsattOidTjeneste(private val adapter: EntraRestClientAdapter) : CachableRestConfig {


    @Cacheable(cacheNames = [ENTRA_OID],key = "#ansattId.verdi")
     fun oidFraEntra(ansattId: AnsattId) = adapter.oidFraEntra(ansattId.verdi)

    override val varighet = Duration.ofDays(365)  // Godt nok, blås i skuddår
    override val navn = ENTRA_OID

    companion object {
        const val ENTRA_OID = "entraoid"
    }
}