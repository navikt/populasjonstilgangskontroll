# Spring Kafka: konfigurere en konsument

Denne guiden beskriver hvordan Kafka-konsumenter kan settes opp,


## 1. Felles Kafka-konfigurasjon (`application-gcp.yaml`)

```yaml
spring:
  kafka:
    bootstrap-servers: ${kafka.brokers}
    properties:
      ssl:
        endpoint:
          identification:
            algorithm: ''
    security:
      protocol: SSL
    ssl:
      trust-store-location: file:${kafka.truststore.path}
      trust-store-type: JKS
      trust-store-password: ${kafka.credstore.password}
      key-store-location: file:${kafka.keystore.path}
      key-store-type: PKCS12
      key-store-password: ${kafka.credstore.password}

    consumer:
      key-deserializer: org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
      properties:
        "[auto.offset.reset]": <egen preferanse>
        "[spring.json.use.type.headers]": false
        "[spring.json.trusted.packages]": <dine pakker>
        "[spring.deserializer.key.delegate.class]": org.apache.kafka.common.serialization.StringDeserializer
        "[spring.deserializer.value.delegate.class]": org.springframework.kafka.support.serializer.JacksonJsonDeserializer
```

Nøkkelpunkter:

- **mTLS mot Aiven Kafka** — sertifikater/truststore kommer fra Nais (`kafka.truststore.path` /
  `kafka.keystore.path` / `kafka.credstore.password` injiseres av Nais Kafka-generatoren).
- **`ErrorHandlingDeserializer`** brukes for både nøkkel og verdi. Den delegerer til en indre
  deserializer (`spring.deserializer.*.delegate.class`) og fanger deserialiseringsfeil slik at de
  havner i `CommonErrorHandler` i stedet for å stoppe konsumenten.
- **`JacksonJsonDeserializer`** er standard verdideserializer for JSON-baserte topics.
  `spring.json.use.type.headers=false` betyr at typen *ikke* leses fra Kafka-headere — den må
  oppgis eksplisitt per lytter (se punkt 3).
- **`spring.json.trusted.packages`** begrenser hvilke pakker Jackson har lov til å deserialisere til.
- **`auto.offset.reset: <egen preferanse>`** — nye consumer-grupper leser fra herfra

Denne konfigurasjonen dekker JSON-baserte topics (NOM, Oppfølging). PDL-topicet bruker Avro og
har derfor sin egen `ConsumerFactory` (se punkt 4).

## 2. Anatomien til en enkel JSON-konsument

```kotlin
@Component
class HendelseKonsument(private val tjeneste: Tjeneste) {

    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = [TOPIC],
        properties = ["spring.json.value.default.type=....Hendelse"],
        groupId = "<groupId>")
    fun listen(hendelse: Hendelse) {
        // prosesser hendelsen
    }

    companion object {
        private const val TOPIC = "<some topic>"
    }
}
```

- **`@Component`** — konsumenten er en vanlig Spring-bean, registrert via component scan.
- **`@KafkaListener(topics = [...])`** — topic-navn som et konstant-array; hold navnet som en
  `private const val` i companion object for å unngå "magic strings" spredt i koden.
- **`properties = ["spring.json.value.default.type=..."]`** — siden
  `spring.json.use.type.headers=false` er satt globalt, må hver lytter fortelle
  `JacksonJsonDeserializer` hvilken klasse meldingen skal deserialiseres til.
- **Exceptions propageres ut av `listen(...)`** — ikke fang exceptions internt. Den delte
  `CommonErrorHandler` (se punkt 5) håndterer retry/backoff/dropping sentralt.

## 3. Filtrering av meldinger (`RecordFilterStrategy`)

Bruk `filter` på `@KafkaListener` for å luke ut meldinger konsumenten ikke skal prosessere,
i stedet for å gjøre det som et `if`-utsagn tidlig i `listen(...)`:

```kotlin
@KafkaListener(
    topics = [TOPIC],
    properties = ["spring.json.value.default.type=...Hendelse"],
    groupId = NOM,
    filter = FILTER_STRATEGY)
fun listen(hendelse: Hendelse, ...)
```

Filter-bønnen registreres med et navngitt `@Bean`, og navnet refereres fra `filter = "..."`:

```kotlin
@Configuration
class Config {
    @Bean(FILTER_STRATEGY)
    fun filterStrategy() =
        RecordFilterStrategy<String, Hendelse> {
            runCatching { BrukerId(it.value().personident) }.isFailure  // alt som ikke er gyldig personident droppes
        }
}
```

For mer domenespesifikk filtrering (f.eks. gradering på PDL-hendelser) — implementer
`RecordFilterStrategy` som en egen `@Component` med logging av hvorfor meldingen ble filtrert:

```kotlin
@Component(PDL_GRADERING_FILTER)
class PdlGraderingFilterStrategy : RecordFilterStrategy<String, Personhendelse> {
    override fun filter(hendelse: ConsumerRecord<String, Personhendelse>) =
        hendelse.skalFiltreres()
    // ...
}
```

`filter()` returnerer `true` for meldinger som skal **droppes** (ikke sendes til `listen`).

## 4. Egen `ConsumerFactory` for ikke-standard formater (Avro)

Når meldingsformatet avviker fra standardoppsettet (f.eks. Avro med skjemaregister for PDL),
lag en egen `ConsumerFactory` og `ContainerFactory`, og referer til den fra `@KafkaListener`
via `containerFactory`:

```kotlin
@Configuration
class PdlKafkaBeanConfig {

    @Bean
    fun pdlHendelseKafkaListenerConsumerFactory(
        props: KafkaProperties,
        env: Environment
    ): ConsumerFactory<String, Personhendelse> =
        DefaultKafkaConsumerFactory(
            props.buildConsumerProperties().apply {
                put(GROUP_ID_CONFIG, PDL)
                put(VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer::class.java)
                put(SCHEMA_REGISTRY_URL_CONFIG, env.schemaRegistryUrl())
                put(SPECIFIC_AVRO_READER_CONFIG, true)
                put(BASIC_AUTH_CREDENTIALS_SOURCE, CREDENTIALS_SOURCE)
                put(USER_INFO_CONFIG, env.userInfo())
            }
        )

    @Bean(PDL_CONTAINER_FACTORY)
    fun pdlAvroListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, Personhendelse>,
        commonErrorHandler: CommonErrorHandler,
    ) = ConcurrentKafkaListenerContainerFactory<String, Personhendelse>().apply {
        setConsumerFactory(consumerFactory)
        setCommonErrorHandler(commonErrorHandler)
    }
}
```

```kotlin
@KafkaListener(
    topics = [PDL_LEESAH_TOPIC],
    containerFactory = PDL_CONTAINER_FACTORY,
    filter = PDL_GRADERING_FILTER)
fun listen(hendelse: Personhendelse) { ... }
```

Merk at `props.buildConsumerProperties()` starter fra de globale `spring.kafka.*`-egenskapene
(bootstrap-servers, SSL) og overstyrer kun det som er spesifikt for Avro-formatet
(deserializer, skjemaregister, autentisering).

## 5. Delt feilhåndtering (`CommonErrorHandler`)

Alle konsumenter (både standard `ConcurrentKafkaListenerContainerFactory` fra Spring Boot
autokonfigurasjon og egne som PDL sin) deler samme `CommonErrorHandler`:

```kotlin
@Configuration
class KafkaBeanConfig {
    @Bean
    fun commonErrorHandler(listeners: List<KafkaTypedDroppedMessageMeter<*>>) =
        DefaultErrorHandler(ExponentialBackOff(1_000L, 2.0).apply {
                maxInterval = 30_000L
                maxElapsedTime = 60_000L
            }
        ).apply {
            setRetryListeners(*listeners.toTypedArray())
        }
}
```

- Eksponentiell backoff: 1s → 2s → 4s → ... → 30s, i inntil 60s totalt, deretter droppes meldingen.
- Ingen Dead Letter Topic (se ADR-003 for begrunnelse).
- Hver konsument har en typesikker `KafkaTypedDroppedMessageMeter<T>` som:
  - logger hvert retry-forsøk på `WARN`,
  - logger permanent tap på `ERROR` og inkrementerer metrikken `kafka.message.dropped`.

```kotlin
@Bean
fun nomDroppedMessageMeter(registry: MeterRegistry) =
    object : KafkaTypedDroppedMessageMeter<NomHendelse>(registry, NomHendelse::class) {}
```

Overstyr `formatEvent(event: T)` for å logge domenespesifikk (og ikke-sensitiv!) kontekst,
slik `PdlKafkaBeanConfig.pdlDroppedMessageMeter` gjør for gradering/endringstype.

## 6. Sjekkliste for en ny konsument

1. Legg til topic-navn som `private const val` i konsumentens companion object.
2. Bruk et delt `*Config`-objekt (`@ConfigurationProperties`) for `groupId`/cache-navn hvis
   konsumenten også cacher data (se `NomConfig`, `OppfølgingConfig`).
3. Standard JSON-format: bruk autokonfigurert `ConcurrentKafkaListenerContainerFactory` og oppgi
   `spring.json.value.default.type` i `properties` på `@KafkaListener`.
4. Ikke-standard format (Avro e.l.): lag egen `ConsumerFactory` + `ContainerFactory` som gjenbruker
   den delte `CommonErrorHandler`, og referer via `containerFactory`.
5. Filtrer bort meldinger konsumenten ikke skal prosessere med `RecordFilterStrategy`
   (navngitt `@Bean` eller egen `@Component`), ikke med tidlig-retur i `listen(...)`.
6. La exceptions propagere ut av `listen(...)` — ikke `runCatching` internt. Gjør
   konsument-operasjonen idempotent slik at retry er trygt.
7. Registrer en `KafkaTypedDroppedMessageMeter<T>` for konsumentens hendelsestype, slik at
   permanente feil/tap blir synlige i logg og metrikker.
8. Ikke logg sensitivt meldingsinnhold (fødselsnummer, navn) — kun metadata (topic, partition,
   offset, hendelsestype) med mindre `CONFIDENTIAL`-logger (fortrolig-loggnivå) brukes eksplisitt.
