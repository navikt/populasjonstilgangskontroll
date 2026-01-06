package no.nav.tilgangsmaskin.felles.cache

import glide.api.BaseClient.OK
import glide.api.GlideClient
import glide.api.models.commands.SetOptions
import glide.api.models.commands.SetOptions.Expiry.Seconds
import no.nav.boot.conditionals.ConditionalOnNotProd
import java.time.Duration
import kotlin.reflect.KClass

@ConditionalOnNotProd
class GlideCacheClient(private val glide: GlideClient, cfg: CacheConfig, handler: CacheNøkkelHandler) : AbstractCacheOperations(handler, cfg) {

    override fun delete(id: String, cache: CachableConfig) =
        glide.del(arrayOf(nøkkel(id, cache))).get() == 1L

    override fun <T : Any> get(id: String, clazz: KClass<T>, cache: CachableConfig): T? =
        glide.get(nøkkel(id, cache))?.get()?.let { json(it, clazz) }

    override fun put(id: String, verdi: Any, ttl: Duration, cache: CachableConfig) =
        glide.set(nøkkel(id, cache), json(verdi), expiry(ttl)).get() == OK

    override fun <T : Any> get(ids: Set<String>, clazz: KClass<T>, cache: CachableConfig) =
        buildMap {
            ids.zip(glide.mget(ids.map {
                nøkkel(it, cache)
            }.toTypedArray()).get()).forEach {
                (id, verdi) -> verdi?.let {
                    put(id, json(it, clazz))
                }
            }
        }

    override fun put(verdier: Map<String, Any>, ttl: Duration, cache: CachableConfig) {
        glide.mset(buildMap {
            verdier.forEach { (id, verdi) ->
                put(nøkkel(id, cache), json(verdi))
            }
        }).get().also {
            verdier.keys.forEach { id ->
                glide.expire(nøkkel(id, cache), ttl.toSeconds()).get()
            }
        }
    }

    private fun expiry(ttl: Duration) =
        SetOptions.builder()
            .expiry(Seconds(ttl.toSeconds()))
            .build()

    override fun ping() =
        glide.ping().get()

    override fun destroy() {
        glide.close()
    }
}