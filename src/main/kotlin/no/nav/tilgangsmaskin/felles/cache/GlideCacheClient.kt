package no.nav.tilgangsmaskin.felles.cache

import glide.api.GlideClient
import glide.api.models.GlideString
import glide.api.models.GlideString.gs
import glide.api.models.commands.SetOptions.Expiry.Seconds
import glide.api.models.commands.SetOptions.builder
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.felles.cache.CacheConfig.Companion.VALKEY
import java.net.URI
import java.time.Duration
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

@ConditionalOnNotProd
class GlideCacheClient(private val client: CompletableFuture<GlideClient>, private val handler: CacheNøkkelHandler) : CacheOperations {

    override fun delete( id: String,vararg caches: CachableConfig) =
        client.get().del(caches.map<CachableConfig, GlideString> { cache ->
            gs(handler.tilNøkkel(cache, id))
        }.toTypedArray()).get()

    override fun <T : Any> getOne( id: String, clazz: KClass<T>,cache: CachableConfig): T? =
        client.get().get(handler.tilNøkkel(cache, id))?.get()?.let { json -> handler.fraJson(json,clazz)}

    override fun putOne( id: String, value: Any, ttl: Duration,cache: CachableConfig) {
        client.get().set(handler.tilNøkkel(cache, id), handler.tilJson(value), builder()
            .expiry(Seconds(ttl.toSeconds()))
            .build()).get()
    }

    override fun <T : Any> getMany( ids: Set<String>, clazz: KClass<T>,cache: CachableConfig): Map<String, T> {
        val keys = ids.map { gs(handler.tilNøkkel(cache, it)) }
        val results = client.get().mget(keys.toTypedArray()).get()
        if (results.isEmpty()) return emptyMap()
        return ids.zip(results)
            .mapNotNull { (id, value) -> value?.let { id to handler.fraJson(it.string, clazz) } }
            .toMap()
    }
    override fun putMany(innslag: Map<String, Any>, ttl: Duration,cache:CachableConfig) =

        innslag.forEach { (id, value) ->
            client.get().set(
                handler.tilNøkkel(cache, id),
                handler.tilJson(value),
                builder()
                    .expiry(Seconds(ttl.toSeconds()))
                    .build()).get()
        }

    override fun ping() = client.get().ping(gs("PING")).get()

    override val pingEndpoint = "http://www.vg.no"
    override val name = VALKEY
}