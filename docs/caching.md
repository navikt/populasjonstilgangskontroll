# Caching: oppsett og bruk

Denne guiden beskriver hvordan caching er satt opp og brukes i Populasjonstilgangskontroll,
med utgangspunkt i `felles/cache/`-pakken og eksisterende bruk i `ansatt/`- og `bruker/`-tjenestene.

Se også [ADR-002: Valkey (Redis) for distribuert cache med resilient feilhåndtering](adr/ADR-002-caching-strategi.md)
for bakgrunnen til valgene beskrevet her.

## 1. Oversikt

Caching løses på to nivåer som utfyller hverandre:

1. **Spring sin `@Cacheable`-abstraksjon** — deklarativ caching på tjenestemetoder, backet av en
   `RedisCacheManager` (GCP) eller Caffeine (lokalt).
2. **`CacheOperations`** — et eksplisitt API for manuell cache-lesing/-skriving, brukt der
   `@Cacheable` ikke passer (bulk-oppslag, delvis cache-treff, TTL-overstyring per kall).

Begge nivåene deler samme lagringsformat og feilhåndtering, slik at cache-feil (timeout,
serialisering, nettverk) **aldri** propagerer til forretningslogikken — appen faller tilbake til
direkte tjenestekall ved cache-miss/-feil.

```
Tjeneste (f.eks. SkjermingTjeneste, PdlTjeneste, EntraTjeneste)
   ├─ @Cacheable                       → RedisCacheManager (Spring cache-abstraksjon)
   └─ CacheOperations.getMany/putMany  → ValkeyCacheOperations (manuelt API for bulk)

CacheBeanConfig                  → cacheManager, feilhåndtering, pub/sub-oppsett
CachableRestConfig (per domene)  → cache-navn + TTL
CacheNøkkelConfig                → nøkkel-prefiks per cache
AbstractCacheOppfrisker          → asynkron oppfrisking ved expiry/delete (keyspace events)
```

## 2. Definere en cache for et nytt domene

Hvert domene som skal caches implementerer `CachableRestConfig` — typisk i samme klasse som
holder REST-klientkonfigurasjonen (base-URL, ping-endepunkt osv.):

```kotlin
@Component
class SkjermingConfig(
    @Value("\${spring.http.serviceclient.skjerming.base-url}") baseUrl: URI,
) : CachableRestConfig, RestConfig(baseUrl, SKJERMING_PING_PATH, SKJERMING) {

    override val navn = name
    override val caches = setOf(SKJERMING_CACHE)
    // varighet (TTL) arves som standard 12 timer fra CachableRestConfig

    companion object {
        const val SKJERMING = "skjerming"
        val SKJERMING_CACHE = CacheNøkkelConfig(SKJERMING)
    }
}
```

`CachableRestConfig` gir fornuftige defaults:

```kotlin
interface CachableRestConfig {
    val varighet: Duration get() = ofHours(12)   // TTL — overstyres per domene ved behov
    val navn: String                              // navnet på cachen i RedisCacheManager
    val cacheNulls: Boolean get() = false          // om null-verdier skal caches (unngår "cache stampede")
    val caches: Set<CacheNøkkelConfig>             // ett eller flere nøkkel-navnerom under samme cache
}
```

Eksempler på TTL brukt i kodebasen:

| Domene | TTL | Config |
|--------|-----|--------|
| Entra OID (NAV-ident → OID) | 365 dager | `EntraOidConfig` |
| Entra AD-grupper (geo/globale) | 3 timer | `EntraGrupperConfig` |
| Skjerming (NOM) | 12 timer (default) | `SkjermingConfig` |
| PDL (familie/utvidet familie) | 12 timer (default) | `PdlPipConfig` |

`CacheNøkkelConfig` styrer selve Redis-nøkkelen:

```kotlin
data class CacheNøkkelConfig(val name: String, val extraPrefix: String? = null) {
    val fullName: String get() = extraPrefix?.let { "$name:$it" } ?: name
    val prefix: String get() = "$name::"
    fun tilNøkkel(nøkkel: String) = "$prefix${extraPrefix?.let { "$it:" } ?: ""}$nøkkel"
}
```

Bruk `extraPrefix` når samme domene har flere logisk atskilte cacher under samme `navn`
(f.eks. `EntraGrupperConfig` har `GEO_CACHE = CacheNøkkelConfig(GRAPH, GEO)` og
`GEO_OG_GLOBALE_CACHE = CacheNøkkelConfig(GRAPH, GEO_OG_GLOBALE)`).

Alle `CachableRestConfig`-bønner samles automatisk opp av `CacheBeanConfig` (via
`vararg cfgs: CachableRestConfig`) og registreres i `RedisCacheManager` med riktig TTL og
`cacheNulls`-innstilling — ingen manuell registrering nødvendig utover å lage `@Component`-klassen.

## 3. Bruke `@Cacheable` for enkle oppslag

For rene "hent én verdi, cache den"-metoder brukes standard Spring-annotasjon:

```kotlin
@RestRetryingWhenRecoverableService
class SkjermingTjeneste(private val client: SkjermingClient, private val cache: CacheOperations) {

    @Cacheable(cacheNames = [SKJERMING], key = "#brukerId.verdi")
    fun skjerming(brukerId: BrukerId) =
        client.skjerming(mapOf(IDENT to brukerId.verdi))
}
```

- `cacheNames` refererer til `navn` på en `CachableRestConfig` (her `SKJERMING`).
- `key` er et SpEL-uttrykk mot metodeparametrene.
- Cache-treff/miss, serialisering og feilhåndtering er identisk med det manuelle API-et under —
  begge bruker samme `RedisCacheManager`/serializer.

## 4. Bruke `CacheOperations` for bulk-oppslag

`@Cacheable` fungerer dårlig når du skal slå opp **mange** nøkler samtidig og bare noen av dem er
cachet fra før. Da brukes `CacheOperations` direkte, med et "delvis cache-treff → hent resten fra
REST → skriv tilbake"-mønster:

```kotlin
interface CacheOperations {
    fun delete(cache: CacheNøkkelConfig, id: String): Boolean
    fun <T : Any> getOne(cache: CacheNøkkelConfig, id: String, clazz: KClass<T>): T?
    fun putOne(cache: CacheNøkkelConfig, id: String, value: Any, ttl: Duration? = null)
    fun <T : Any> getMany(cache: CacheNøkkelConfig, ids: Set<String>, clazz: KClass<T>): Map<String, T?>
    fun putMany(cache: CacheNøkkelConfig, innslag: Map<String, Any>, ttl: Duration? = null)
    fun clear(cache: CacheNøkkelConfig): Long
    fun clearAll(): Long
    fun size(cache: CacheNøkkelConfig): Long
    fun sizes(vararg caches: CacheNøkkelConfig): Map<String, Long>
}
```

Reifiserte hjelpefunksjoner unngår å måtte sende `KClass` eksplisitt fra Kotlin:

```kotlin
inline fun <reified T : Any> CacheOperations.getOne(cfg: CacheNøkkelConfig, id: String): T? =
    getOne(cfg, id, T::class)

inline fun <reified T : Any> CacheOperations.getMany(cfg: CacheNøkkelConfig, ids: Set<String>): Map<String, T?> =
    getMany(cfg, ids, T::class)
```

Eksempel fra `SkjermingTjeneste.skjerminger(...)`:

```kotlin
fun skjerminger(brukerIds: List<BrukerId>): Map<BrukerId, Boolean> {
    val ids = brukerIds.mapTo(mutableSetOf()) { it.verdi }
    val fraCache = cache.getMany<Boolean>(SKJERMING_CACHE, ids)
        .filterValues { it != null }

    if (fraCache.size == ids.size) return fraCache.mapKeys { BrukerId(it.key) }

    val manglende = ids - fraCache.keys
    val fraRest = client.skjerminger(mapOf(IDENTER to manglende))
    cache.putMany(SKJERMING_CACHE, fraRest)     // skriv tilbake det som ble hentet
    return (fraRest + fraCache).mapKeys { BrukerId(it.key) }
}
```

Mønsteret er gjennomgående: **hent det du kan fra cache → hent resten fra kilden → skriv tilbake
til cache → returner union**. Bruk TTL-parameteren (`ttl` på `putOne`/`putMany`) kun når du
eksplisitt trenger en annen levetid enn cachens `varighet` — ellers brukes default fra
`CachableRestConfig` automatisk.

## 5. `CacheBeanConfig` — hvordan cache-laget kobles sammen

```kotlin
@Configuration(proxyBeanMethods = true)
@ConditionalOnGCP
class CacheBeanConfig(
    private val cf: RedisConnectionFactory,
    private val errorHandler: CacheErrorHandler,
    private vararg val cfgs: CachableRestConfig,
) : CachingConfigurer, RedisListenerConfigurer {

    override fun errorHandler() = errorHandler

    @Bean
    override fun cacheManager() =
        RedisCacheManager.builder(nonLockingRedisCacheWriter(cf))
            .withInitialCacheConfigurations(cfgs.associate { it.navn to cacheConfig(it) })
            .enableStatistics()
            .build()

    private fun cacheConfig(cfg: CachableRestConfig) =
        defaultCacheConfig()
            .entryTtl(cfg.varighet)
            .serializeKeysWith(fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(fromSerializer(ResilientValkeySerializer(GenericJacksonJsonRedisSerializer(VALKEY_MAPPER))))
            .apply { if (!cfg.cacheNulls) disableCachingNullValues() }
}
```

Denne bønnen er kun aktiv i GCP-profilen (`@ConditionalOnGCP`). Lokalt/i test brukes
Spring Boot sin Caffeine-autokonfigurasjon i stedet, styrt av `application-local.yaml`:

```yaml
spring:
  cache:
    type: caffeine
```

Dette betyr at `@Cacheable` fungerer likt lokalt (in-memory, per instans), mens
`CacheOperations`/`ValkeyCacheOperations` fortsatt krever en ekte (Testcontainers-basert) Redis i
tester, siden det manuelle API-et snakker direkte med `StringRedisTemplate`.

### Serialisering

- **Nøkler**: `StringRedisSerializer` — enkle streng-nøkler (`cache::verdi` eller
  `cache:ekstra:verdi`, se `CacheNøkkelConfig.tilNøkkel`).
- **Verdier**: `GenericJacksonJsonRedisSerializer` med en dedikert `VALKEY_MAPPER`
  (`JsonMapper` med `NavPolymorphicTypeValidator` og `JacksonTypeInfoAddingValkeyModule` for
  polymorf type-info), pakket inn i `ResilientValkeySerializer`.
- **`ResilientValkeySerializer`** fanger deserialiseringsfeil og returnerer `null` (cache miss) i
  stedet for å kaste — en modellendring skal aldri gi 500 til konsumenter, kun et ekstra
  tjenestekall.

### Feilhåndtering

`CacheMeteredErrorHandler` implementerer Spring sin `CacheErrorHandler` og sørger for at
GET/PUT-feil kun logges og telles (`cache.operation.failed`-metrikk), mens EVICT/CLEAR-feil
fortsatt kastes videre (siden en feilet sletting kan bety at gammel/feil data blir stående).

## 6. Asynkron oppfrisking ved expiry (`CacheOppfrisker`)

For enkelte cacher (typisk der data endres av eksterne hendelser, f.eks. Entra-grupper og
skjerming) ønsker vi å **friske opp** cachen proaktivt når et innslag utløper, i stedet for å bare
vente på neste kall (cache-miss). Dette løses med Valkeys keyspace-notifications:

```kotlin
@Component
class ValkeyEventListeningCacheOppfrisker(
    erLeder: Boolean = true,
    private vararg val oppfriskere: CacheOppfrisker,
) : LeaderAware(erLeder) {

    @RedisListener("__keyevent@0__:expired")
    @RedisListener("__keyevent@0__:del")
    fun onEvent(nokkel: CacheNøkkel) {
        somLeder {
            oppfriskere.firstOrNull { it.cacheName == nokkel.cacheName }?.oppfrisk(nokkel)
        }
    }
}
```

- Kun **leder-poden** (`LeaderAware`) reagerer på events, slik at ikke alle replikaer trigger
  samme oppfrisking samtidig.
- `CacheNøkkel` parser Redis-nøkkelen (`cache::metode:id` eller `cache::id`) tilbake til
  cache-navn, (valgfri) metode og id — og maskerer fnr i logging via `maskert`.
- Implementer `AbstractCacheOppfrisker` for domenespesifikk oppfrisking. Feil logges (`WARN`) og
  svelges — oppfrisking er *best effort*, ikke kritisk:

```kotlin
@Component
class SkjermingCacheOppfrisker(private val skjerming: SkjermingTjeneste) : AbstractCacheOppfrisker() {
    override val cacheName = SKJERMING
    override fun doOppfrisk(nøkkel: CacheNøkkel) = skjerming.skjerming(BrukerId(nøkkel.id))
}
```

For mer sammensatte tilfeller (som `EntraCacheOppfrisker`) kan `nøkkel.metode` brukes til å
skille mellom flere spørringer under samme cache (f.eks. `geoGrupper` vs.
`geoOgGlobaleGrupper`), og feil av typen "ansatt ikke funnet" kan trigge en cache-invalidering +
ny oppfrisking i stedet for å bare gi opp.

`redisMessageListenerContainer` (i `CacheBeanConfig`) starter denne lytteren med egen
retry/logging (`setRecoveryInterval(5_000)`), slik at forbigående Pub/Sub-feil ved oppstart ikke
stopper applikasjonen — TTL-basert utløp fungerer uansett som fallback.

## 7. Administrasjon: `CacheController` (kun DEV)

`felles/cache/CacheController.kt` eksponerer endepunkter under `/${DEV}/cache` for manuell
inspeksjon og tømming under utvikling/feilsøking:

| Endepunkt | Beskrivelse |
|-----------|-------------|
| `POST cache/skjerminger` | Slå opp skjermingsstatus for en liste NAV-identer direkte i cache |
| `POST cache/personer` | Slå opp PDL-personer direkte i cache |
| `DELETE flush/{id}` | Slett ett enkelt cache-innslag (f.eks. Entra OID for en ansatt) |
| `DELETE flushCache/{cacheName}` | Tøm en hel navngitt cache (scan + batch-slett) |
| `DELETE flushDB` | Tøm hele Valkey-databasen (`FLUSHDB`) |
| `GET flush` | Enkel HTML-side for å tømme cache for innlogget bruker |

`clear(...)` og `clearAll()` i `ValkeyCacheOperations` er eksplisitt sperret i prod
(`check(!isProd) { ... }`) for å unngå utilsiktet sletting av cache-innhold i produksjon.

## 8. Sjekkliste for å legge til caching for et nytt domene

1. Implementer `CachableRestConfig` på domenets konfigurasjonsklasse (`navn`, `caches`, og
   ev. egen `varighet`/`cacheNulls`).
2. Lag én eller flere `CacheNøkkelConfig`-konstanter (bruk `extraPrefix` for flere
   navnerom under samme cache-navn).
3. Bruk `@Cacheable` for enkle "ett oppslag → én cache-nøkkel"-metoder.
4. Bruk `CacheOperations.getMany`/`putMany` for bulk-oppslag med delvis cache-treff.
5. Vurder om cachen trenger proaktiv oppfrisking ved expiry — implementer i så fall
   `AbstractCacheOppfrisker` og la den registrere seg som `@Component` (den plukkes automatisk opp
   av `ValkeyEventListeningCacheOppfrisker` sin `vararg oppfriskere: CacheOppfrisker`).
6. Ikke logg sensitivt cache-innhold — bruk `CacheNøkkel.maskert` og `maskFnr()`-extensions.
7. Health-sjekk (`CachePingable`) og error-metrikker (`CacheMeteredErrorHandler`) dekkes allerede
   felles — ingen endring nødvendig per domene.
