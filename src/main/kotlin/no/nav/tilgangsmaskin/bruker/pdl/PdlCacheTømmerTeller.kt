package no.nav.tilgangsmaskin.bruker.pdl

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.felles.AbstractTeller
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelConfig
import no.nav.tilgangsmaskin.felles.security.AuthContext
import org.springframework.stereotype.Component
import java.util.Locale.*

@Component
class PdlCacheTømmerTeller(registry: MeterRegistry, authContext: AuthContext) :
    AbstractTeller(registry, authContext, NAVN, "Cache tømming pr beskyttelsesgrad") {
    fun tell(cache: CacheNøkkelConfig, gradering: String, endringsType: String) =
        tell(Tags.of(CACHE, cache.name, GRADERING,
            gradering.lowercase(getDefault()), TYPE, endringsType))

    private companion object {
        private const val GRADERING = "gradering"
        private const val CACHE = "cache"
        private const val TYPE = "type"
        private const val NAVN = "beskyttelse"
    }
}
