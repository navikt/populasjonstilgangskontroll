package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component

/**
 * Teller for alle cache-operasjoner i [ValkeyCacheOperations].
 * Gir full observabilitet over alle operasjoner og resultater — inkludert feil.
 *
 * Metrikknavn: `valkey.cache.operasjoner`
 * Tagger: `operasjon` (getOne/getMany/putOne/putMany/delete/clear), `cache` (cachenavn), `resultat` (hit/miss/ok/feilet)
 */
@Component
class ValkeyCacheTeller(private val registry: MeterRegistry) {

    fun tell(operasjon: Operasjon, cache: String, resultat: Resultat, n: Int = 1) =
        registry.counter(
            "valkey.cache.operasjoner",
            "operasjon", operasjon.name,
            "cache", cache,
            "resultat", resultat.name
        ).increment(n.toDouble())


    enum class Operasjon { getOne, getMany, putOne, putMany, delete, clear }
    enum class Resultat { hit, miss, ok, feilet }
}

