# Retry: oppsett og bruk

Denne guiden beskriver hvordan retry er satt opp for utgående REST-kall i
Populasjonstilgangskontroll, med utgangspunkt i `felles/rest/RestRetryingWhenRecoverableService.kt`
og bruken i `*Tjeneste`-klassene (`PdlTjeneste`, `EntraTjeneste`, `SkjermingTjeneste`,
`VergemålTjeneste`, `EntraProxyTjeneste`).

For Kafka-konsumenters retry/feilhåndtering (som er separat og bruker `DefaultErrorHandler` med
`ExponentialBackOff`), se [spring-kafka.md](spring-kafka.md) og
[ADR-003](adr/ADR-003-kafka-feilhåndtering.md).

## 1. Grunnlaget: Spring Framework sin innebygde `@Retryable`

Fra Spring Framework 7 finnes det en innebygd, minimal retry-mekanisme
(`org.springframework.resilience.annotation.Retryable`), inspirert av det eldre
Spring Retry-prosjektet. Den aktiveres med `@EnableResilientMethods` på hovedapplikasjonen:

```kotlin
@SpringBootApplication
@EnableResilientMethods
@ConditionalOnGCP
class App
```

`@Retryable` kan settes på klasse- eller metodenivå og proxyer metodekall slik at et kastet unntak
kan trigge et nytt forsøk. Relevante defaults (som vår egen annotasjon bygger videre på):

| Attributt | Default | Betydning |
|-----------|---------|-----------|
| `maxRetries` | 3 | Totalt inntil 1 + 3 = 4 forsøk |
| `delay` | 1000 ms | Basisforsinkelse mellom forsøk |
| `multiplier` | 1.0 | Fast forsinkelse (ingen backoff) med mindre satt høyere |
| `maxDelay` | ubegrenset | Øvre tak på forsinkelse |
| `includes`/`value` | alle exceptions | Hvilke exception-typer som skal gi retry |

## 2. Vår egen meta-annotasjon: `@RestRetryingWhenRecoverableService`

I stedet for å bruke `@Retryable` direkte i hver tjeneste, definerer vi én delt
meta-annotasjon som samler standardoppsettet for REST-kall:

```kotlin
@Retryable(delayString = "\${retry.delay:1s}")
@Target(FUNCTION, CLASS)
@Retention(RUNTIME)
@Inherited
@MustBeDocumented
@Service
annotation class RestRetryingWhenRecoverableService(
    @get:AliasFor(annotation = Retryable::class)
    val value: Array<KClass<out Throwable>> = [
        RecoverableRestException::class,
        SocketTimeoutException::class,
        ResourceAccessException::class,
        RedisCommandTimeoutException::class,
        QueryTimeoutException::class,
    ]
)
```

- **`@Service`** — annotasjonen er selv en stereotype-annotasjon, så klasser som bruker den
  trenger ikke `@Component`/`@Service` i tillegg.
- **`delayString = "\${retry.delay:1s}"`** — forsinkelsen er konfigurerbar via property
  `retry.delay`, med 1 sekund som fallback. **I tester overstyres denne til `0ms`**
  (`src/test/resources/application.yaml`) slik at retry-tester går raskt.
- **`value` (= `includes`)** — kun disse exception-typene trigger retry:
  - `RecoverableRestException` — våre egne "midlertidig feil"-exceptions (se punkt 3)
  - `SocketTimeoutException`, `ResourceAccessException` — nettverks-/timeout-feil fra
    `RestClient`
  - `RedisCommandTimeoutException` — Valkey/Lettuce-timeout
  - `QueryTimeoutException` — Spring Data-timeout mot database
- **`maxRetries` (3) og `multiplier` (1.0, fast delay)** arves fra `@Retryable`-defaultene —
  vi overstyrer dem ikke, så alle REST-tjenester får samme oppførsel: inntil 4 forsøk med
  ~1 sekunds mellomrom.

Merk at `@Inherited` gjør at subklasser arver annotasjonen, og at den kan settes på **klasse**-nivå
for å gjelde alle public-metoder i klassen (proxy-basert, samme begrensning som `@Transactional`
— kall må gå via Spring-proxyen, ikke internt `this.metode()`-kall).

## 3. Hvilke exceptions trigger retry?

REST-klienter bruker en delt feilhåndterer, `RestDefaultErrorHandler`, som mapper HTTP-status til
domenespesifikke exceptions:

```kotlin
@Component
class RestDefaultErrorHandler : ErrorHandler {
    override fun handle(req: HttpRequest, res: ClientHttpResponse) {
        val status = res.statusCode
        val e = when (status) {
            NOT_FOUND -> NotFoundRestException(uri, ident)
            REQUEST_TIMEOUT, TOO_MANY_REQUESTS -> RecoverableRestException(status, uri, res.statusText)
            else -> if (status.is4xxClientError) IrrecoverableRestException(status, uri, res.statusText)
                    else RecoverableRestException(status, uri, res.statusText)
        }
        throw e
    }
}
```

Exception-hierarkiet (`RestExceptions.kt`) skiller eksplisitt mellom det som **kan** og **ikke
kan** løses ved å prøve på nytt:

```kotlin
open class IrrecoverableRestException(status: HttpStatusCode, uri: URI, msg: String, cause: Throwable?)
    : ErrorResponseException(...)

class NotFoundRestException(uri: URI, identifikator: String?, ...) : IrrecoverableRestException(NOT_FOUND, ...)
class ConflictRestException(uri: URI, ...) : IrrecoverableRestException(CONFLICT, ...)

open class RecoverableRestException(status: HttpStatusCode, uri: URI, msg: String, cause: Throwable?)
    : ErrorResponseException(...)
```

- **`RecoverableRestException`** — kastes for `408 Request Timeout`, `429 Too Many Requests`, og
  alle 5xx-responser. Dette er feil som ofte er forbigående (nedetid, overbelastning) — de er
  inkludert i `@RestRetryingWhenRecoverableService` sin `value`-liste og gir retry.
- **`IrrecoverableRestException`** (og subklassene `NotFoundRestException`,
  `ConflictRestException`) — kastes for øvrige 4xx-responser. De regnes som permanente feil
  (feil forespørsel, ressurs finnes ikke) og gir **ikke** retry, siden de ikke er med i
  `value`-listen.

Denne todelingen er selve poenget med designet: **retry skal kun skje når et nytt forsøk faktisk
kan gi et annet resultat**. Å prøve på nytt etter en 404 eller 409 vil bare gi samme feil igjen.

## 4. Bruke annotasjonen i en tjeneste

Legg `@RestRetryingWhenRecoverableService` på klassenivå, sammen med `@Observed` (metrikker) og
`@ImportHttpServices` (registrerer REST-klienten):

```kotlin
@Observed
@RestRetryingWhenRecoverableService
@ImportHttpServices(types = [SkjermingClient::class], group = SKJERMING)
class SkjermingTjeneste(private val client: SkjermingClient, private val cache: CacheOperations) {

    @Cacheable(cacheNames = [SKJERMING], key = "#brukerId.verdi")
    fun skjerming(brukerId: BrukerId) =
        client.skjerming(mapOf(IDENT to brukerId.verdi))
}
```

Alle eksisterende REST-tjenester følger dette mønsteret:

| Tjeneste | Ekstern tjeneste |
|----------|-------------------|
| `PdlTjeneste` | PDL PIP (person/familie) |
| `EntraTjeneste` | Microsoft Graph (AD-grupper) |
| `EntraProxyTjeneste` | entra-proxy |
| `SkjermingTjeneste` | NOM skjerming |
| `VergemålTjeneste` | Vergemål |

Ingen av disse trenger å håndtere retry selv — de kaster bare exception fra klienten, og
proxyen rundt klassen fanger opp `RecoverableRestException` m.fl. og prøver på nytt automatisk.

## 5. Observabilitet: `RestRetryLogger`

Hvert retry-forsøk publiseres som et `MethodRetryEvent`. Dette fanges opp sentralt i stedet for
å logges i hver enkelt tjeneste:

```kotlin
@Component
class RestRetryLogger {
    @EventListener(MethodRetryEvent::class)
    fun onEvent(event: MethodRetryEvent) {
        when (val t = cause(event)) {
            is NotFoundRestException ->
                log.info("NotFoundRestException fra '${event.method.name}' for [${t.identifikator}] mot ${t.uri}", t)
            else -> if (event.isRetryAborted) {
                log.warn("Aborterer metode '${event.method.name}' grunnet ${...}")
            } else {
                log.warn("Feil i '${event.method.name}', prøver igjen", t)
            }
        }
    }
}
```

- **Hvert forsøk** som feiler men skal prøves på nytt logges på `WARN` med metodenavn og
  argumenter (til feilsøking).
- **Når retry gis opp** (`event.isRetryAborted`, dvs. `maxRetries` nådd eller exception ikke i
  `includes`-listen), logges dette også på `WARN` — det er siste sjanse til å se hvorfor kallet
  til slutt feilet.
- **`NotFoundRestException`** logges alltid på `INFO` (ikke `WARN`), siden "ikke funnet" ofte er
  en normal og forventet tilstand (f.eks. en ansatt uten registrert skjerming), ikke en feil som
  bør vekke oppmerksomhet.

## 6. Konfigurere delay

`retry.delay` er en vanlig Spring-property og kan settes per profil om nødvendig:

```yaml
retry:
  delay: 1s   # brukes hvis ikke satt (default i annotasjonen)
```

I test-profilen er den satt til `0ms` for å unngå at retry-tester bruker unødvendig tid:

```yaml
# src/test/resources/application.yaml
retry:
  delay: 0ms
```

## 7. Sjekkliste for en ny REST-tjeneste med retry

1. Legg `@RestRetryingWhenRecoverableService` (og `@Observed`) på klassen — ikke bruk
   `@Retryable` direkte, da mister du den delte exception-listen og default-delay.
2. La klienten kaste `RestDefaultErrorHandler` sine exceptions (registrer den som
   `ErrorHandler` på `RestClient`-oppsettet) — ikke fang/oversett feil i tjenesten selv.
3. Kast en `RecoverableRestException`-subklasse (eller la den propagere fra klienten) for feil
   som er verdt å prøve på nytt for; bruk `IrrecoverableRestException`/`NotFoundRestException`
   for permanente feil som ikke skal gi retry.
4. Ikke fang exceptions internt i tjenestemetoden med `runCatching` — det hindrer retry-proxyen i
   å se feilen.
5. Stol på `RestRetryLogger` for logging av retry-forsøk — unngå duplisert logging i tjenesten.
6. Verifiser i tester at `retry.delay=0ms` er satt (arves fra `src/test/resources/application.yaml`
   for `@SpringBootTest`), slik at retry-scenarier ikke gjør testene trege.
