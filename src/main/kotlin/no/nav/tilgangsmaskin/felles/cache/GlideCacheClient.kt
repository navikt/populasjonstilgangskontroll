package no.nav.tilgangsmaskin.felles.cache

import glide.api.GlideClusterClient
import glide.api.models.ClusterBatch
import glide.api.models.GlideString.gs
import glide.api.models.commands.SetOptions.Expiry.Seconds
import glide.api.models.commands.SetOptions.builder
import java.time.Duration
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.reflect.KClass

class GlideCacheClient(private val client: GlideClusterClient, private val handler: CacheNøkkelHandler, private val slotCalculator: CacheSlotCalculator) : CacheOperations {

    override fun delete( id: String,cache: CachableConfig) =
        client.del(arrayOf(gs(handler.nøkkel(id,cache)))).get()

    override fun <T : Any> getOne(id: String, clazz: KClass<T>, cache: CachableConfig): T? =
        client.get(handler.nøkkel(id,cache))?.get()?.let { json -> handler.json(json,clazz)}

    override fun putOne(id: String, value: Any, ttl: Duration, cache: CachableConfig) {
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
                         ttl: Duration, cache: CachableConfig) {

        val slotEntries = slotCalculator.slotsFor(innslag, cache)

        slotEntries.forEach { (_, entries) ->
            val pipeline = ClusterBatch(false)
            entries.forEach { (id, value) ->
                pipeline.set(
                    handler.nøkkel(id, cache),
                    handler.json(value),
                    builder()
                        .expiry(Seconds(ttl.toSeconds()))
                        .build()
                )
            }

            client.exec(pipeline, false).get()  // Execute once per slot group
        }
    }

    override fun tilNøkkel(cache: CachableConfig, id: String) = handler.nøkkel(id, cache)

}
