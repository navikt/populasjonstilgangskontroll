package no.nav.tilgangsmaskin.felles.cache

import glide.api.GlideClient
import glide.api.models.GlideString
import glide.api.models.GlideString.gs
import glide.api.models.commands.SetOptions.Expiry.Seconds
import glide.api.models.commands.SetOptions.builder
import no.nav.tilgangsmaskin.felles.cache.CacheConfig.Companion.VALKEY
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

@Service
class GlideCacheClient(private val client: CompletableFuture<GlideClient>, private val handler: CacheNøkkelHandler) : CacheOperations {

    override fun delete( id: String,vararg caches: CachableConfig,) =
        client.get().del(caches.map<CachableConfig, GlideString> { cache ->
            gs(handler.nøkkel(id,cache))
        }.toTypedArray()).get()

    override fun <T : Any> getOne( id: String, clazz: KClass<T>,cache: CachableConfig): T? =
        client.get().get(handler.nøkkel(id,cache))?.get()?.let { json -> handler.json(json,clazz)}

    override fun putOne( id: String, value: Any, ttl: Duration,cache: CachableConfig) {
        client.get().set(handler.nøkkel(id,cache), handler.json(value), builder()
            .expiry(Seconds(ttl.toSeconds()))
            .build()).get()
    }

    override fun <T : Any> getMany( ids: Set<String>, clazz: KClass<T>,cache: CachableConfig): Map<String, T> {
        val keys = ids.map { gs(handler.nøkkel(it,cache)) }
        val results = client.get().mget(keys.toTypedArray()).get()
        return ids.zip(results)
            .mapNotNull { (id, value) -> value?.let { id to handler.json(it.string, clazz) } }
            .toMap()
    }
    override fun putMany(innslag: Map<String, Any>,
                         ttl: Duration,cache: CachableConfig) {

        innslag.forEach { (id, value) ->
            client.get().set(
                handler.nøkkel(id,cache),
                handler.json(value),
                builder()
                    .expiry(Seconds(ttl.toSeconds()))
                    .build()).get()
        }
    }

    override fun tilNøkkel(cache: CachableConfig, id: String) = handler.nøkkel(id,cache)


    override fun ping() = client.get().ping(gs("PING")).get()

    override val pingEndpoint: String
        get() = TODO("Not yet implemented")
    override val name = VALKEY
}