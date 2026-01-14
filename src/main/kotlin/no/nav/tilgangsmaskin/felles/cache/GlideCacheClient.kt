package no.nav.tilgangsmaskin.felles.cache

import glide.api.BaseClient
import glide.api.GlideClient
import glide.api.GlideClusterClient
import glide.api.models.GlideString
import glide.api.models.GlideString.gs
import glide.api.models.commands.SetOptions.Expiry.Seconds
import glide.api.models.commands.SetOptions.builder
import no.nav.tilgangsmaskin.felles.cache.CacheConfig.Companion.VALKEY
import java.time.Duration
import kotlin.reflect.KClass

class GlideCacheClient(private val client: BaseClient, private val handler: CacheNøkkelHandler) : CacheOperations {

    override fun delete( id: String,vararg caches: CachableConfig,) =
        client.del(caches.map<CachableConfig, GlideString> { cache ->
            gs(handler.nøkkel(id,cache))
        }.toTypedArray()).get()

    override fun <T : Any> getOne( id: String, clazz: KClass<T>,cache: CachableConfig): T? =
        client.get(handler.nøkkel(id,cache))?.get()?.let { json -> handler.json(json,clazz)}

    override fun putOne( id: String, value: Any, ttl: Duration,cache: CachableConfig) {
        client.set(handler.nøkkel(id,cache), handler.json(value), builder()
            .expiry(Seconds(ttl.toSeconds()))
            .build()).get()
    }

    override fun <T : Any> getMany( ids: Set<String>, clazz: KClass<T>,cache: CachableConfig): Map<String, T> {
        val keys = ids.map { gs(handler.nøkkel(it,cache)) }
        val results = client.mget(keys.toTypedArray()).get()
        return ids.zip(results)
            .mapNotNull { (id, value) -> value?.let { id to handler.json(it.string, clazz) } }
            .toMap()
    }
    override fun putMany(innslag: Map<String, Any>,
                         ttl: Duration,cache: CachableConfig) {

        innslag.forEach { (id, value) ->
            client.set(
                handler.nøkkel(id,cache),
                handler.json(value),
                builder()
                    .expiry(Seconds(ttl.toSeconds()))
                    .build()).get()
        }
    }

    override fun tilNøkkel(cache: CachableConfig, id: String) = handler.nøkkel(id,cache)

    override fun ping() = when (client) {
        is GlideClusterClient -> client.ping().get()
        is GlideClient -> client.ping().get()
        else -> throw IllegalStateException("Ukjent client type ${client.javaClass.name}")
    }

    override val pingEndpoint: String
        get() = TODO("Not yet implemented")
    override val name = VALKEY
}