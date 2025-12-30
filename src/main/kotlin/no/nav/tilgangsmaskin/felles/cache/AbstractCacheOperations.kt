package no.nav.tilgangsmaskin.felles.cache

import no.nav.tilgangsmaskin.felles.cache.CacheConfig.Companion.VALKEY
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.DisposableBean
import org.springframework.web.util.UriComponentsBuilder
import kotlin.reflect.KClass

abstract class AbstractCacheOperations(private val handler: CacheNøkkelHandler, cfg: CacheConfig) : CacheOperations,
    DisposableBean {
    override val name = VALKEY
    override val  pingEndpoint = UriComponentsBuilder.newInstance()
        .host(cfg.host)
        .port(cfg.port)
        .scheme("https")
        .build().toString()

    protected val log = getLogger(javaClass)

    protected fun json(verdi: Any) =
        handler.json(verdi)

    protected fun <T : Any> json(verdi: String, clazz: KClass<T>) =
        handler.json(verdi, clazz)

    protected fun nøkkel(id: String, cache: CachableConfig) =
        handler.nøkkel(id, cache)

    protected fun id(nøkkel: String) =
        handler.id(nøkkel)

    companion object {
         const val UTLØPT_KANAL = "__keyevent@0__:expired"
    }
}