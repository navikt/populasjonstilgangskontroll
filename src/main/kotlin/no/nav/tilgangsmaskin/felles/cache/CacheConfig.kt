package no.nav.tilgangsmaskin.felles.cache

import io.lettuce.core.RedisURI
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.ENTRA_CACHES
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.OID_CACHE
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM_CACHE
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingRestClientAdapter.Companion.SKJERMING_CACHE
import no.nav.tilgangsmaskin.bruker.pdl.PdlRestClientAdapter.Companion.PDL_CACHES
import no.nav.tilgangsmaskin.felles.cache.CacheAdapter.Companion.VALKEY
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(VALKEY)
data class CacheConfig(val username: String, val password: String, val host: String, val port: String) {
    val cacheURI = RedisURI.Builder
        .redis(host, port.toInt())
        .withSsl(true)
        .withAuthentication(username, password)
        .build()
}

enum class Caches(vararg val  caches: CachableConfig) {
    PDL(*PDL_CACHES.toTypedArray()),
    SKJERMING(SKJERMING_CACHE),
    OID(OID_CACHE),
    NOM(NOM_CACHE),
    GRAPH(*ENTRA_CACHES.toTypedArray())
}
