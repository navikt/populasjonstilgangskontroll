# Deklarative HTTP-klienter med Spring Security-tokenutveksling

Denne guiden beskriver hvordan Tilgangsmaskinen kaller eksterne REST-tjenester ved bruk av
Spring 7/Spring Boot 4 sine deklarative HTTP-klienter (`@HttpExchange`), koblet sammen med
`spring-security-oauth2-client` for automatisk client_credentials-tokenutveksling. Målet er at
en ny integrasjon skal kunne legges til uten å skrive noen `RestClient`- eller
token-håndteringskode selv.

## 1. Byggeklossene

| Byggekloss | Rolle |
|---|---|
| `interface XClient` med `@GetExchange`/`@PostExchange` | Deklarerer endepunktene som et Kotlin-interface — ingen implementasjon |
| `@ClientRegistrationId("navn")` | Kobler et kall (metode eller interface) til en OAuth2-klientregistrering |
| `@ImportHttpServices(types = [...], group = "navn")` | Registrerer interfacet som en HTTP-service-gruppe; Spring lager en proxy-implementasjon |
| `spring.http.serviceclient.<gruppe>.base-url` | Base-URL for gruppen (auto-konfigurert av Spring Boot) |
| `spring.security.oauth2.client.registration.<id>` | Client-id/secret/scope for tokenutvekslingen |
| `RestClientHttpServiceGroupConfigurer` (bean) | Kobler OAuth2-autorisasjon og egne interceptors på gruppen(e) |
| `OAuth2AuthorizedClientManager` | Henter/cacher/fornyer access-token via client_credentials |

## 2. Definere klienten

Et interface per nedstrøms-tjeneste, i samme pakke som tjenesten som bruker den:

```kotlin
interface SkjermingClient {

    @PostExchange(SKJERMING_PATH)
    @ClientRegistrationId(SKJERMING)
    fun skjerming(@RequestBody body: Map<String, String>): Boolean

    @PostExchange(SKJERMING_BULK_PATH)
    @ClientRegistrationId(SKJERMING)
    fun skjerminger(@RequestBody body: Map<String, Set<String>>): Map<String, Boolean>

    @GetExchange(SKJERMING_PING_PATH)
    fun ping(): Any

    companion object {
        const val SKJERMING_PATH = "/skjermet"
        const val SKJERMING_BULK_PATH = "/skjermetBulk"
        const val SKJERMING_PING_PATH = "/internal/health/liveness"
    }
}
```

Kilde: `ansatt/skjerming/SkjermingClient.kt`

Regler:
- Metodesignaturer bruker rene domenetyper/DTO-er — ingen `ResponseEntity`, ingen manuell (de)serialisering.
- `@ClientRegistrationId` kan settes på hele interfacet (når alle kall bruker samme registrering, se
  `EntraGrupperClient`/`EntraOidClient`/`PdlPipClient`) eller per metode (når kall i samme interface bruker
  ulike scopes).
- `ping()` mot helse-endepunktet er med i alle klienter — se seksjon 8.
- Path-variabler (`@PathVariable`), query-parametre (`@RequestParam`) og headere (`@RequestHeader`)
  brukes akkurat som i `@RestController`, bare speilvendt (klient i stedet for server).

## 3. Registrere klienten på tjenesten

```kotlin
@Observed
@RestRetryingWhenRecoverableService
@ImportHttpServices(types = [SkjermingClient::class], group = SKJERMING)
class SkjermingTjeneste(private val client: SkjermingClient, ...) {
    ...
}
```

Kilde: `ansatt/skjerming/SkjermingTjeneste.kt`

`@ImportHttpServices` er det som faktisk lager proxy-implementasjonen og gjør `SkjermingClient`
injectable — det trengs ingen `@Bean`-metode for selve klienten. `group`-navnet er nøkkelen som
binder klienten til riktig `base-url` og riktig `RestClientHttpServiceGroupConfigurer` (seksjon 4).

Gruppenavnet er som regel en konstant delt mellom `XConfig` (cache/rest-konfigurasjon) og
`XClient`/`XTjeneste`, f.eks. `SkjermingConfig.Companion.SKJERMING`.

## 4. Koble på OAuth2-tokenutveksling

All autorisasjon for HTTP-service-grupper går gjennom én sentral configurer:

```kotlin
@Bean
fun oauth2GroupConfigurer(manager: OAuth2AuthorizedClientManager) =
    RestClientHttpServiceGroupConfigurer { groups ->
        from(manager).configureGroups(groups)
        groups.forEachClient { _, builder ->
            builder.requestInterceptors {
                it.addFirst(OAuth2DownstreamUriCapturingInterceptor())
            }
        }
    }
```

Kilde: `felles/security/OAuth2SecurityBeanConfig.kt`

`OAuth2RestClientHttpServiceGroupConfigurer.from(manager)` legger på en
`OAuth2ClientHttpRequestInterceptor` for **alle** grupper. Den leser `@ClientRegistrationId` fra
kallet, henter et gyldig access-token fra `manager` (client_credentials-flyt), og setter
`Authorization: Bearer ...`-header før kallet går ut. `OAuth2AuthorizedClientManager`-bønnen
under samme klasse binder sammen:

```kotlin
@Bean
fun oauth2AuthorizedClientManager(
    repo: ClientRegistrationRepository,
    service: OAuth2AuthorizedClientService,
    successHandler: OAuth2AuthorizationSuccessHandler,
    failureHandler: OAuth2AuthorizationFailureHandler
) = AuthorizedClientServiceOAuth2AuthorizedClientManager(repo, service).apply {
    setAuthorizedClientProvider(OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build())
    setAuthorizationSuccessHandler(successHandler)
    setAuthorizationFailureHandler(failureHandler)
}
```

`clientCredentials()` er hva som gjør flyten maskin-til-maskin (ingen bruker involvert), token
caches av `OAuth2AuthorizedClientService` og fornyes automatisk når det er i ferd med å utløpe.

### Enkeltgruppe-spesifikk konfigurasjon

Trenger en gruppe egne headere (i tillegg til Authorization), legges det til en ekstra
`RestClientHttpServiceGroupConfigurer`, filtrert på gruppenavn:

```kotlin
@Bean
fun entraGraphGroupConfigurer() = RestClientHttpServiceGroupConfigurer { groups ->
    groups.filterByName(GRAPH).forEachClient { _, builder ->
        builder.requestInterceptor(RestHeaderAddingRequestInterceptor(CONSISTENCY_LEVEL))
    }
}
```

Kilde: `ansatt/graph/oid/EntraOidBeanConfig.kt` (tilsvarende i `bruker/pdl/PdlPipBeanConfig.kt`
for `BEHANDLINGSNUMMER`-header). Alle `RestClientHttpServiceGroupConfigurer`-bønner i konteksten
kjøres etter tur — rekkefølge har ingen betydning så lenge de filtrerer på egen gruppe.

## 5. Felles feilhåndtering for alle REST-klienter

Uansett hvilken gruppe/nedstrøms-tjeneste et kall gjelder, skal en ikke-2xx-respons oversettes
til det samme exception-hierarkiet (`RecoverableRestException`/`IrrecoverableRestException`/
`NotFoundRestException`, se `docs/retry.md`). Dette løses **ikke** per gruppe, men med én
global `RestClientCustomizer`-bønn:

```kotlin
@Bean
fun restClientCustomizer() =
    RestClientCustomizer { c ->
        c.requestInterceptors {
            logbookInterceptor.ifAvailable { interceptor -> it.add(interceptor) }
        }
        c.defaultStatusHandler(HttpStatusCode::isError, handler::handle)
    }
```

Kilde: `felles/rest/RestBeanConfig.kt` — `handler` er `RestDefaultErrorHandler`
(`felles/rest/RestDefaultErrorHandler.kt`), injisert som `RestClient.ResponseSpec.ErrorHandler`.

`RestClientCustomizer` er et Spring Boot-interface som kalles på *hver* `RestClient.Builder`
appen bygger — uavhengig av om det er hoved-`RestClient`-bønnen, en `@ImportHttpServices`-gruppe,
eller GraphQL-klientens underliggende `RestClient` (se `docs/graphql.md`). Spring Boot sin
autokonfigurasjon (`RestClientCustomizerHttpServiceGroupConfigurer`) plukker automatisk opp
**alle** `RestClientCustomizer`-bønner i konteksten og kjører dem mot hver
`HttpServiceGroup`-builder, i tillegg til at `RestClientAutoConfiguration` gjør det samme for
den vanlige `RestClient.Builder`-bønnen. Konkret betyr det:

- `defaultStatusHandler(HttpStatusCode::isError, handler::handle)` registreres på *alle*
  RestClient-instanser i appen — en ny `@ImportHttpServices`-gruppe trenger ikke noen egen
  feilhåndteringskode, den arver den globale statushandleren automatisk bare ved å eksistere i
  Spring-konteksten.
- `RestDefaultErrorHandler.handle(...)` mapper statuskode → exception-type ett sted
  (`404` → `NotFoundRestException`, `408`/`429`/5xx → `RecoverableRestException`, øvrige 4xx →
  `IrrecoverableRestException`), og logger `info` for "forventede" 404-er og `warn` for resten.
  Denne mappingen trenger med andre ord aldri gjentas eller vedlikeholdes per klient.
- Fordi customizeren også legger på en delt `LogbookClientHttpRequestInterceptor` (når den
  finnes i konteksten), får alle klienter samme request/response-logging uten ekstra oppsett.
- Gruppe-spesifikke `RestClientHttpServiceGroupConfigurer`-bønner (seksjon 4, "Enkeltgruppe-
  spesifikk konfigurasjon") kommer *i tillegg til* denne globale customizeren — de to
  mekanismene konkurrerer ikke, de dekker ulike behov (gruppespesifikke headere vs. felles
  feilhåndtering/logging for alt).

I tester erstattes ikke denne mekanismen med noe annet — `OAuth2ClientTestConfig` definerer sin
egen `RestClientCustomizer`-bønn med samme `defaultStatusHandler(..., RestDefaultErrorHandler()::handle)`,
og en `restClientGroupCustomizer`-bønn som eksplisitt kjører `customizers.forEach { it.customize(builder) }`
for hver gruppe (siden `RestClientCustomizerHttpServiceGroupConfigurer` ikke er del av
`@RestClientTest`-slicen). Se `docs/graphql.md` seksjon 9 for et eksempel i praksis.

## 6. Konfigurasjon (application-gcp.yaml)

Base-URL per gruppe og client-registrering (client-id/secret/scope) holdes strengt adskilt:

```yaml
spring:
  http:
    serviceclient:
      skjerming:
        base-url: "http://skjermede-personer-pip.nom"
      entraproxy:
        base-url: "http://entra-proxy.sikkerhetstjenesten"
      graph:
        base-url: "https://graph.microsoft.com/v1.0"
      pdlpip:
        base-url: "https://${pdlpip}"
    clients:
      connect-timeout: 3s
      read-timeout: 5s

  security:
    oauth2:
      client:
        provider:
          azuread:
            issuer-uri: ${azure.openid.config.issuer}
        registration:
          skjerming:
            provider: azuread
            client-id: ${azure.app.client.id}
            client-secret: ${azure.app.client.secret}
            authorization-grant-type: client_credentials
            scope:
              - "api://${nais.cluster.type}-gcp.nom.skjermede-personer-pip/.default"
```

- `serviceclient.<gruppe>.base-url` matcher `group` i `@ImportHttpServices`.
- `oauth2.client.registration.<id>` matcher `@ClientRegistrationId(id)`.
- Gruppenavn og registrerings-id trenger *ikke* være like (se `entraproxy`-gruppen som bruker
  `entraproxy`-registreringen, mens `graph`-gruppen har to klienter — `EntraGrupperClient` og
  `EntraOidClient` — som begge peker på samme `graph`-registrering).
- Alle registreringer her bruker `client_credentials` mot samme Azure AD-app
  (`azure.app.client.id`/`secret`), bare med ulikt `scope` (audience for nedstrøms-tjenesten).

## 7. Sporing og observability på tokenutveksling

Fordi tokenfeil ofte er vanskelige å diagnostisere (utløpt hemmelighet, feil scope, feil
audience), er standard Spring Security-hooks kledd med logging:

- **`OAuth2LoggingAuthorizationSuccessHandler`** logger `info` ved første autorisasjon og ved
  hver token-fornyelse, med `registrationId`, ny/gammel utløpstid og hvilken nedstrøms-URI
  kallet gjaldt.
- **`OAuth2LoggingAuthorizationFailureHandler`** logger `debug` ved autorisasjonsfeil, inkl.
  `registrationId` og OAuth2 `errorCode`.
- **`OAuth2DownstreamUriCapturingInterceptor`** legger nedstrøms-URI i en
  `ThreadLocal`/`OAuth2DownstreamURIContext` slik at handlerne over kan referere til *hvilket*
  kall som trigget token-utvekslingen — nyttig fordi flere klienter kan dele samme
  `registrationId`.

Ingen av disse trenger å endres når en ny klient legges til — de virker automatisk for enhver
gruppe som er koblet på `oauth2GroupConfigurer`.

## 8. Helsesjekk (ping)

Hver klient har en `ping()`-metode mot nedstrøms sitt liveness-endepunkt, koblet til en
`PingableHealthIndicator`:

```kotlin
@Bean
fun graphHealthIndicator(cfg: EntraGrupperConfig, client: EntraOidClient) =
    PingableHealthIndicator(cfg, client::ping)
```

`cfg` (en `RestConfig`-underklasse) gir `name` og `pingEndpoint` for logging/health-detaljer;
`client::ping` er selve HTTP-kallet. Dette gir automatisk `/monitoring/health`-status per
nedstrøms-avhengighet uten egen kode i klienten.

## 9. Testing

I tester byttes den ekte OAuth2-autoriseringen ut med en enkel base-URL-oppløsning, uten
tokenutveksling — se `felles/rest/OAuth2ClientTestConfig.kt`:

```kotlin
@Bean
fun restClientGroupCustomizer(customizers: ObjectProvider<RestClientCustomizer>, env: Environment) =
    RestClientHttpServiceGroupConfigurer { groups ->
        groups.forEachClient { group, builder ->
            env.getRequiredProperty("spring.http.serviceclient.${group.name()}.base-url").let(builder::baseUrl)
            customizers.forEach { it.customize(builder) }
        }
    }
```

Testene setter `base-url` til en WireMock-instans (eller lignende) via `@DynamicPropertySource`,
og stubber respons på klientens path. Siden `@ClientRegistrationId` ikke er koblet på i test,
trengs ingen `spring.security.oauth2.client.registration.*`-oppsett — kall går rett mot stub uten
`Authorization`-header.

## Sjekkliste for ny deklarativ HTTP-klient

1. Lag `interface XClient` med `@GetExchange`/`@PostExchange` + `@ClientRegistrationId`.
2. Legg `@ImportHttpServices(types = [XClient::class], group = X)` på tjenesteklassen som
   injiserer klienten.
3. Legg til `spring.http.serviceclient.<gruppe>.base-url` i `application-gcp.yaml` (og evt.
   `application-dev-gcp.yaml`).
4. Legg til `spring.security.oauth2.client.registration.<id>` med riktig `scope` (audience for
   nedstrøms-appen i riktig cluster/miljø).
5. Hvis klienten trenger egne headere utover Authorization: legg til en egen, gruppe-filtrert
   `RestClientHttpServiceGroupConfigurer`-bønn.
6. Legg til en `ping()`-metode + `PingableHealthIndicator`-bønn for helsesjekk.
7. Bruk `OAuth2ClientTestConfig` i tester og pek `base-url` mot en stub/WireMock.
