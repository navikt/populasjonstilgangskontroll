# GraphQL-klient mot PDL (Spring GraphQL Client)

Denne guiden beskriver hvordan Tilgangsmaskinen kaller en GraphQL-tjeneste (PDL sitt
`pdl-api`) med Spring sin `HttpSyncGraphQlClient`, inkludert token-, header-, logging- og
feiloversettelse. I dag er PDL-oppslaget via GraphQL den eneste GraphQL-integrasjonen i
appen; mønsteret under er det som skal gjenbrukes hvis en ny GraphQL-nedstrøms legges til.

## 1. Oversikt

```
PdlSyncGraphQLClientAdapter        (domene-API: partnere(ident): Set<FamilieMedlem>)
  └─ GraphQlClient (Spring GraphQL, HttpSyncGraphQlClient)
       ├─ PdlGraphQLLoggingInterceptor   (logger query + variabler, CONFIDENTIAL)
       ├─ OAuth2ClientHttpRequestInterceptor (client_credentials-token)
       ├─ RestHeaderAddingRequestInterceptor (behandlingsnummer-header)
       └─ RestClient → https://<pdlgraph-host>/graphql
  └─ PdlGraphQLErrorHandler           (oversetter GraphQL-feil til RecoverableRestException/
                                        NotFoundRestException/IrrecoverableRestException)
```

I motsetning til de deklarative `@HttpExchange`-klientene (se `docs/http-clients.md`) er dette
et vanlig `RestClient` pakket inn i Spring sin `GraphQlClient`-abstraksjon — fordi GraphQL alltid
POST-er samme endepunkt med query/variabler i body, gir det ikke noe å hente å deklarere ett
interface per query.

## 2. Konfigurasjon (`PdlGraphQLConfig`)

```kotlin
@Component
class PdlGraphQLConfig(@Value("\${pdlgraph}") pdlHost: String) :
    RestConfig(create("https://$pdlHost$DEFAULT_GRAPHQL_PATH"), "", PDLGRAPH) {

    companion object {
        const val PDLGRAPH = "pdlgraph"
        private const val BID = "B897"
        val BEHANDLINGSNUMMER = "behandlingsnummer" to BID
        private const val DEFAULT_GRAPHQL_PATH = "/graphql"
    }
}
```

Kilde: `bruker/pdl/PdlGraphQLConfig.kt`

- Gjenbruker samme `RestConfig`-basisklasse som de vanlige REST-klientene (se
  `docs/http-clients.md`), slik at `baseUri`, `name` og helsesjekk-plumbing er konsistent.
  `pingPath` settes til `""` fordi PDL sitt GraphQL-endepunkt ikke har et eget helsesjekk-path —
  ping gjøres i stedet med en `{__typename}`-spørring (seksjon 4).
- `BEHANDLINGSNUMMER` er et lovpålagt Nav-konsept (hvilken behandling/formål oppslaget gjøres
  for) som PDL krever som header på alle kall — se `Nav sitt behandlingskatalog`-krav.
- `oauth2.client.registration.pdlgraph` i `application-gcp.yaml` gir client-id/secret/scope for
  client_credentials-utvekslingen (samme mønster som i `docs/http-clients.md`).

## 3. Bygge `GraphQlClient`-bønnen

```kotlin
@Bean
@Qualifier(PDLGRAPH)
fun pdlGraphRestClient(builder: Builder, mgr: OAuth2AuthorizedClientManager, failureHandler: OAuth2AuthorizationFailureHandler) =
    builder
        .requestInterceptors {
            it.add(OAuth2DownstreamUriCapturingInterceptor())
            it.add(RestHeaderAddingRequestInterceptor(BEHANDLINGSNUMMER))
            it.add(OAuth2ClientHttpRequestInterceptor(mgr).apply {
                setClientRegistrationIdResolver { PDLGRAPH }
                setAuthorizationFailureHandler(failureHandler)
            })
        }
        .build()

@Bean
fun pdlGraphSyncGraphQLClient(@Qualifier(PDLGRAPH) client: RestClient, cfg: PdlGraphQLConfig) =
    HttpSyncGraphQlClient.builder(client)
        .url(cfg.baseUri)
        .interceptors { it.addFirst(PdlGraphQLLoggingInterceptor()) }
        .build()
```

Kilde: `bruker/pdl/PdlGraphBeanConfig.kt`

Fordi denne klienten *ikke* går via `@ImportHttpServices`/`RestClientHttpServiceGroupConfigurer`
(de er laget for deklarative interfaces, ikke for `GraphQlClient`), settes
`OAuth2ClientHttpRequestInterceptor` opp manuelt her, med
`setClientRegistrationIdResolver { PDLGRAPH }` som alltid returnerer samme registrerings-id
(kallet trenger aldri velge registrering dynamisk). `@Qualifier(PDLGRAPH)` disambiguerer
`RestClient`-bønnen fra andre `RestClient`-instanser i konteksten.

`HttpSyncGraphQlClient` er den **synkrone** GraphQL-klienten fra `spring-graphql` — passer godt
her siden resten av kallkjeden (REST-klienter, cache) også er synkron/blocking.

## 4. Helsesjekk

```kotlin
@Bean
fun pdlGraphHealthIndicator(cfg: PdlGraphQLConfig, @Qualifier(PDLGRAPH) client: RestClient) =
    PingableHealthIndicator(cfg) {
        client.post()
            .uri(cfg.baseUri)
            .contentType(APPLICATION_JSON)
            .body("""{"query":"{__typename}"}""")
            .retrieve()
            .toBodilessEntity()
    }
```

Siden GraphQL ikke har et eget liveness-endepunkt, brukes introspeksjonsspørringen
`{__typename}` som ping — den krever ingen data og validerer bare at endepunktet svarer.
`PingableHealthIndicator` er samme byggekloss som brukes for alle andre nedstrøms-avhengigheter
(se `docs/http-clients.md` seksjon 8).

## 5. Query-dokumenter

Spørringer legges som `.graphql`-filer under `src/main/resources/graphql-documents/`, én fil per
"document name":

```graphql
# graphql-documents/query-sivilstand.graphql
query($ident: ID!){
    hentPerson(ident: $ident) {
        sivilstand(historikk: true){
            type
            gyldigFraOgMed
            relatertVedSivilstand
            bekreftelsesdato
        }
    }
}
```

Spring GraphQL sin auto-konfigurasjon plukker disse opp automatisk (klassisk
`DocumentSource`-oppførsel), og de refereres med filnavn uten extension:

```kotlin
private val SIVILSTAND_QUERY = "query-sivilstand" to "hentPerson"
```

Fordeler fremfor inline query-strenger i Kotlin-kode: syntax highlighting, gjenbrukbarhet, og
mulighet for GraphQL-tooling (schema-validering, linting) på selve `.graphql`-filene.

## 6. Kalle klienten (`PdlSyncGraphQLClientAdapter`)

```kotlin
@Component
class PdlSyncGraphQLClientAdapter(
    private val cfg: PdlGraphQLConfig,
    private val client: GraphQlClient,
    private val errorHandler: PdlGraphQLErrorHandler = PdlGraphQLErrorHandler()
) {
    fun partnere(ident: String): Set<FamilieMedlem> =
        runCatching {
            query<Partnere>(SIVILSTAND_QUERY, ident(ident)).sivilstand.mapNotNullTo(mutableSetOf()) {
                it.relatertVedSivilstand?.let { brukerId -> FamilieMedlem(BrukerId(brukerId), tilPartner(it.type)) }
            }
        }.recover { e ->
            (e as? NotFoundRestException)?.let { emptySet() } ?: throw e
        }.getOrThrow()

    private inline fun <reified T : Any> query(query: Pair<String, String>, vars: Map<String, String>) =
        runCatching {
            client.documentName(query.first)
                .variables(vars)
                .executeSync()
                .field(query.second)
                .toEntity(T::class.java)
                ?: throw IrrecoverableRestException(INTERNAL_SERVER_ERROR, cfg.baseUri, "Fant ikke feltet ${query.second} i responsen")
        }.getOrElse { errorHandler.handle(cfg.baseUri, it) }
}
```

Kilde: `bruker/pdl/PdlSyncGraphQLClientAdapter.kt`

Mønster:
- `documentName(...)` slår opp `.graphql`-filen fra seksjon 5, `variables(...)` setter
  spørrevariablene, `executeSync()` sender kallet.
- `.field("hentPerson")` peker på GraphQL-response-treet der resultatet skal hentes fra (unngår
  å måtte deserialisere hele svaret manuelt), og `.toEntity(T::class.java)` mapper til domene-DTO
  (`Partnere` i dette tilfellet).
- Hver spørring wrappes i `runCatching` + en delt privat `query<T>`-hjelpefunksjon slik at all
  feilhåndtering (seksjon 7) er ett sted, uavhengig av hvor mange query-metoder adapteren får i
  fremtiden.
- Domenemetoden (`partnere`) gjør selv om `NotFoundRestException` til et tomt resultat der det gir
  mening forretningsmessig (ingen partner er ikke en feil) — ikke noe error-handleren skal
  bestemme generelt.

## 7. Feiloversettelse (`PdlGraphQLErrorHandler`)

GraphQL returnerer *alltid* HTTP 200, selv ved feil — feil kommer i stedet som en
`errors`-liste i JSON-responsen. Spring GraphQL kaster da et
`FieldAccessException` (feltet du ba om er ikke tilgjengelig pga. en GraphQL-feil) i stedet for
en HTTP-statuskode-basert exception. Derfor må GraphQL-feil oversettes til appens vanlige
REST-exception-hierarki (`docs/retry.md` seksjon 3) manuelt:

```kotlin
class PdlGraphQLErrorHandler {
    fun handle(uri: URI, e: Throwable): Nothing = when (e) {
        is FieldAccessException -> throw e.oversett(uri)
        is GraphQlTransportException -> throw RecoverableRestException(INTERNAL_SERVER_ERROR, uri, e.message ?: "Uventet respons", e)
        else -> throw IrrecoverableRestException(INTERNAL_SERVER_ERROR, uri, e.message ?: "Uventet respons", e)
    }
    ...
}
```

- **`FieldAccessException`** → leser `extensions.code` fra det første `ResponseError`-objektet
  (PDL sin egen feilkode, f.eks. `"not_found"` eller `"unauthenticated"`), mapper den til en
  `HttpStatus`, og kaster tilsvarende `NotFoundRestException`/`IrrecoverableRestException`.
  `"UNAUTHENTICATED"` mappes eksplisitt til `401` siden det ikke er et gyldig `HttpStatus`-navn.
- **`GraphQlTransportException`** (nettverksfeil, tidsavbrudd på selve HTTP-transporten) → alltid
  `RecoverableRestException`, slik at `@RestRetryingWhenRecoverableService` (se `docs/retry.md`)
  kan forsøke på nytt.
- Alt annet → `IrrecoverableRestException` (fail-safe: ukjente feiltyper retries ikke).

Dette gjør at resten av retry-/logging-infrastrukturen som allerede finnes for vanlige
REST-klienter (se `docs/retry.md` og `docs/http-clients.md`) fungerer uendret for
GraphQL-adapteren — den kaster samme exception-hierarki uansett transport.

## 8. Logging

```kotlin
class PdlGraphQLLoggingInterceptor : SyncGraphQlClientInterceptor {
    override fun intercept(req: ClientGraphQlRequest, chain: Chain) =
        chain.next(req).also {
            log.trace(CONFIDENTIAL, "Eksekverte {} med variabler {}", req.document, req.variables)
        }
}
```

Logges på `trace`-nivå og merkes med `CONFIDENTIAL` markeren (se Logback-oppsett), siden
variablene ofte inneholder fødselsnummer — samme sladdingsmønster som brukes ellers for
personopplysninger i logger.

## 9. Testing

Bruk `@RestClientTest` + `MockRestServiceServer`, akkurat som for vanlige `RestClient`-baserte
tjenester, men koble `GraphQlClient`-bønnen på samme `RestClient`:

```kotlin
@RestClientTest
@ContextConfiguration(initializers = [PropertySettingTestContextInitializer::class],
    classes = [PdlSyncGraphQLClientAdapter::class, PdlGraphQLConfig::class])
@Import(GraphQLTestConfig::class, OAuth2ClientTestConfig::class)
class PdlSyncGraphQLClientAdapterTest(
    private val adapter: PdlSyncGraphQLClientAdapter,
    private val server: MockRestServiceServer,
    private val cfg: PdlGraphQLConfig
) : BehaviorSpec() {

    @TestConfiguration
    class GraphQLTestConfig {
        @Bean @Qualifier(PDLGRAPH)
        fun pdlGraphRestClient(b: RestClient.Builder) =
            b.requestInterceptors { it.add(RestHeaderAddingRequestInterceptor(BEHANDLINGSNUMMER)) }.build()

        @Bean @Qualifier(PDLGRAPH)
        fun syncPdlGraphQLClient(@Qualifier(PDLGRAPH) client: RestClient, cfg: PdlGraphQLConfig,
                                 interceptors: List<SyncGraphQlClientInterceptor>) =
            HttpSyncGraphQlClient.builder(client).url(cfg.baseUri).interceptors { it.addAll(interceptors) }.build()
    }

    init {
        Given("oppslag av partnere fra PDL") {
            When("sivilstand er GIFT") {
                Then("returneres PARTNER") {
                    server.expect(requestTo(cfg.baseUri))
                        .andRespond(withSuccess(sivilstandRespons(Sivilstandstype.GIFT), APPLICATION_JSON))
                    adapter.partnere("Z999999").single().relasjon shouldBe PARTNER
                }
            }
        }
        Given("NOT_FOUND fra PDL") {
            When("PDL returnerer NOT_FOUND-feil") {
                Then("returneres tom mengde") {
                    server.expect(requestTo(cfg.baseUri)).andRespond(withSuccess(notFoundErrorRespons(), APPLICATION_JSON))
                    adapter.partnere("Z999999").shouldBeEmpty()
                }
            }
        }
    }
}
```

Kilde: `bruker/pdl/PdlSyncGraphQLClientAdapterTest.kt`

Noter:
- Responsen som stubbes med `MockRestServiceServer` er **rå GraphQL JSON**
  (`{"data": {...}}` eller `{"errors": [...], "data": {...}}`), ikke en typet DTO — akkurat slik
  PDL faktisk svarer over HTTP.
- `server.expect(requestTo(cfg.baseUri))` matcher alle kall mot samme URL (GraphQL POST-er alltid
  til samme endepunkt); bruk `.andExpect(header(...))` for å verifisere headere som
  `behandlingsnummer` (se testen "behandlingsnummer-header").
- GraphQL-feilresponser (`errors`-array med `extensions.code`) brukes til å teste
  `PdlGraphQLErrorHandler`-oversettelsen ende-til-ende, uten å mocke `PdlGraphQLErrorHandler`
  direkte — se `felles/graphql/GraphQLErrorHandlerTest.kt` for mer finkornede enhetstester av
  selve oversettelseslogikken.

## Sjekkliste for ny GraphQL-integrasjon

1. Legg query som `.graphql`-fil i `src/main/resources/graphql-documents/`.
2. Lag en `XGraphQLConfig : RestConfig(...)` med base-URI til GraphQL-endepunktet
   (`.../graphql`), og en konstant for hvilken `oauth2.client.registration`-id som skal brukes.
3. Bygg en `RestClient`-bønn med `OAuth2ClientHttpRequestInterceptor` (fast
   `clientRegistrationIdResolver`) + evt. egne headere, og pakk den i en `HttpSyncGraphQlClient`
   pekende på samme URI.
4. Legg til en `PingableHealthIndicator` med en `{__typename}`-spørring.
5. Lag en adapter-klasse med domenemetoder som bruker `client.documentName(...).variables(...).executeSync().field(...).toEntity(...)`,
   og oversett feil til `Recoverable`/`Irrecoverable`/`NotFoundRestException` via en delt
   error-handler (se seksjon 7).
6. Legg til `oauth2.client.registration.<id>` i `application-gcp.yaml` med riktig scope.
7. Test med `@RestClientTest` + `MockRestServiceServer`, stub rå GraphQL JSON-responser
   (både `data`- og `errors`-varianter).
