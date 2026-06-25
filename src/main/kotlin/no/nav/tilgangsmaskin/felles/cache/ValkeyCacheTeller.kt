package no.nav.tilgangsmaskin.felles.cache

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.stereotype.Component
import java.util.Locale.getDefault
import java.util.concurrent.TimeUnit.NANOSECONDS
import kotlin.time.Duration

@Component
class ValkeyCacheTeller(private val registry: MeterRegistry) {

    fun tell(operasjon: Operasjon, cache: String, resultat: Resultat, n: Int = 1) =
        registry.counter(
            OPERASJONER,
            *tags(operasjon, cache, resultat)
        ).increment(n.toDouble())

    fun tellTid(operasjon: Operasjon, cache: String, resultat: Resultat, varighet: Duration) =
        Timer.builder(VARIGHET)
            .description("Varighet for Valkey cache-operasjoner")
            .tags(*tags(operasjon, cache, resultat))
            .register(registry)
            .record(varighet.inWholeNanoseconds, NANOSECONDS)

    private fun tags(operasjon: Operasjon, cache: String, resultat: Resultat) = arrayOf(
        "operasjon", operasjon.name.lowercase(getDefault()),
        "cache", cache,
        "resultat", resultat.name.lowercase(getDefault())
    )

    enum class Operasjon { GET_ONE, GET_MANY, PUT_ONE, PUT_MANY, DELETE, CLEAR }
    enum class Resultat { HIT, MISS, DELVIS, OK, FEILET }

    companion object {
        private const val OPERASJONER = "valkey.cache.operasjoner"
        private const val VARIGHET = "valkey.cache.varighet"
    }
}

