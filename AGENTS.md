# AGENTS.md

## Overview

**Populasjonstilgangskontroll** ("Tilgangsmaskinen") is a Spring Boot 4 / Kotlin backend that determines whether a Nav employee (ansatt) has access to a citizen (bruker) based on population-level rules. It is NOT domain-specific access control — consuming systems handle that themselves.

## Architecture

```
TilgangController (REST API)
  └─ RegelTjeneste (orchestration)
       ├─ AnsattTjeneste → Entra ID groups, NOM (identity mapping)
       ├─ BrukerTjeneste → PDL (person data), family, geographic info
       └─ RegelMotor (rule engine)
            ├─ Kjerneregler (core rules — minimum checks)
            └─ OverstyrbareRegler (full rules — can be overridden via EnkeltTilgang)
```

### Key domain concepts

- **Ansatt**: Employee identified by `AnsattId` (NAV-ident), enriched with Entra AD group memberships
- **Bruker**: Citizen identified by `BrukerId` (fnr/dnr), enriched from PDL with family, geographic ties, shielding status
- **Regel**: Interface with `evaluer(ansatt, bruker): Boolean` — returns true if access is allowed
- **RegelSett**: Ordered collection of rules (`KJERNE` or `KOMPLETT`); evaluation stops on first failure
- **EnkeltTilgang**: Per-case override allowing an employee access despite rule failure (stored in PostgreSQL)

### Token types

The API supports two auth flows:
- **OBO** (On-Behalf-Of): Employee-initiated, ansattId extracted from token
- **CCF** (Client Credentials Flow): System-initiated, ansattId passed as path parameter

### External integrations

| System | Purpose | Client registration |
|--------|---------|-------------------|
| PDL (pdl-api, pdl-pip-api) | Person data, family relations | Azure AD client_credentials |
| Entra ID / Graph API (via entra-proxy) | Employee AD group memberships | Azure AD client_credentials |
| NOM (skjermede-personer-pip) | Shielded persons lookup | Azure AD client_credentials |
| repr-api | Guardian/representative data | Azure AD client_credentials |

### Caching (Valkey/Redis in GCP, Caffeine locally)

Caches are central to performance — see `felles/cache/`. TTLs: OID→NavIdent 365d, AD-groups 3h (async refresh), Skjerming/NOM/PDL 12h.

## Build & Run

```bash
./gradlew build          # Compile + test
./gradlew test           # Tests only (uses Testcontainers — Docker required)
./gradlew bootJar        # Produces build/libs/app.jar
```

- **Java 25** (toolchain managed by Gradle)
- Tests use `SharedPostgresContainer` singleton to avoid container restarts per test class
- Kotest `ProjectConfig` initializes `EntraGlobalGruppe` IDs from `test.properties` before all specs

## Project Conventions

### Language & naming

- Norwegian domain terms in code: `tjeneste` (service), `bruker` (user/citizen), `ansatt` (employee), `regler` (rules)
- Package structure mirrors domain: `ansatt/`, `bruker/`, `regler/`, `tilgang/`, `felles/` (shared)

### Rule engine pattern

New rules implement `Regel` (or `OverstyrbarRegel`/`KjerneRegel`) and are annotated with `@SortertRegel(order)` for evaluation priority. They are auto-discovered via Spring component scan and assembled in `RegelBeanConfig`.

### REST clients

Built via `RestClientFactory` in `felles/rest/`. All outbound calls use `nav-security-token-client` for token acquisition. Error handling via `RestDefaultErrorHandler`.

### Testing

- **Framework**: JUnit 5 + Kotest matchers + MockK + springmockk
- **Integration tests**: `@SpringBootTest` with `TestApp`, Testcontainers (PostgreSQL, Redis)
- **Slice tests**: `@DataJpaTest` reuses `SharedPostgresContainer`
- **Test profile**: `application-test.yaml` activates; group UUIDs loaded from `test.properties`

### Database (Flyway)

Migrations in `src/main/resources/db/migration/`. Currently at V19. Schema centers on `enkelt_tilgang` (per-case access overrides).

## Key Files

| Path | Purpose |
|------|---------|
| `src/main/kotlin/no/nav/tilgangsmaskin/regler/motor/Regel.kt` | Rule interface |
| `src/main/kotlin/no/nav/tilgangsmaskin/regler/motor/RegelMotor.kt` | Rule evaluation engine |
| `src/main/kotlin/no/nav/tilgangsmaskin/tilgang/TilgangController.kt` | Main API endpoints |
| `src/main/kotlin/no/nav/tilgangsmaskin/ansatt/Ansatt.kt` | Employee domain model with group-based access checks |
| `src/main/kotlin/no/nav/tilgangsmaskin/bruker/Bruker.kt` | Citizen domain model |
| `src/main/kotlin/no/nav/tilgangsmaskin/felles/cache/` | Valkey/Caffeine cache infrastructure |
| `src/test/kotlin/no/nav/tilgangsmaskin/SharedPostgresContainer.kt` | Shared test DB container |
| `src/main/resources/application-gcp.yaml` | Production configuration (Kafka, Valkey, DB, OAuth2 clients) |

