# Prices API

REST service that returns the single applicable sale price for a product and brand at a given point in time. When price ranges overlap, the entry with the highest priority wins.

Built with **Java 25 · Spring Boot 4.0.5 · Spring Data JPA · H2 · MapStruct · OpenAPI Generator · Caffeine · Gradle**.

---

## Table of Contents

1. [Architecture](#architecture)
2. [Technology decisions](#technology-decisions)
3. [Performance optimisations](#performance-optimisations)
4. [Local setup](#local-setup)
5. [API usage](#api-usage)

---

## Architecture

The project follows **Hexagonal Architecture** (Ports & Adapters). The core rule is that dependencies only point inward: infrastructure knows about application, application knows about domain, domain knows nothing.

```
┌─────────────────────────────────────────────────────────────┐
│  Infrastructure                                             │
│                                                             │
│   ┌─────────────┐          ┌──────────────────────────┐    │
│   │ REST adapter│          │   Persistence adapter    │    │
│   │ (Spring MVC)│          │ (Spring Data JPA + H2)   │    │
│   └──────┬──────┘          └────────────┬─────────────┘    │
│          │                              │                   │
│   ┌──────▼──────────────────────────────▼─────────────┐    │
│   │                  Application                       │    │
│   │            PriceServiceImpl (@Service)             │    │
│   └──────────────────────┬─────────────────────────────┘   │
│                          │                                  │
│   ┌──────────────────────▼─────────────────────────────┐   │
│   │                    Domain                           │   │
│   │   Price (record) · PriceRepository (interface)     │   │
│   │   PriceNotFoundException                           │   │
│   │   — zero framework dependencies —                  │   │
│   └────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Layer responsibilities

| Layer | Package | Responsibility |
|---|---|---|
| **Domain** | `domain/` | Business rules and types. Pure Java — no Spring, no JPA. |
| **Application** | `application/` | Orchestrates use cases. Depends only on domain interfaces. |
| **Infrastructure – REST** | `infrastructure/rest/` | HTTP adapter: deserialises requests, calls the service, serialises responses. |
| **Infrastructure – Persistence** | `infrastructure/persistence/` | JPA adapter: translates between JPA entities and domain models. |

### Key design choices

- **Domain model as a Java record** — `Price` is immutable by construction. No setters, no accidental mutation.
- **`PriceRepository` is a domain interface** — the application layer calls it without knowing JPA exists. Swapping the database requires no changes outside `infrastructure/persistence/`.
- **`GlobalExceptionHandler`** centralises all HTTP error mapping. Controllers never build error responses themselves.

---

## Technology decisions

| Technology | Role | Why |
|---|---|---|
| **Spring Boot 4** | Application framework | Auto-configuration, embedded server, dependency management. |
| **Spring Data JPA + H2** | Persistence | In-memory database sufficient for a self-contained demo; JPA abstracts SQL. `ddl-auto=none` keeps `schema.sql` as the single source of truth for the schema — Hibernate never surprises you by altering tables. |
| **OpenAPI Generator** | REST contract | Contract-first: the OpenAPI spec in `docs/openapi/api-definition.yaml` is the source of truth. The generator produces Spring MVC interfaces; `PriceController` implements them. A compilation error, not a runtime surprise, if the implementation drifts from the contract. |
| **MapStruct** | Object mapping | Compile-time code generation. Zero reflection, full type safety. Mapping errors surface at build time. Two mappers: `PriceEntityMapper` (entity → domain) and `PriceResponseMapper` (domain → HTTP response). |
| **Caffeine** | In-process cache | Price lookups are read-heavy and the data is stable within a request window. Caffeine caches results in the service layer, skipping the database for repeated identical queries. For multi-instance deployments, replace with a distributed cache (e.g. Redis) by changing `spring.cache.type`. |
| **Gradle** | Build tool | Manages dependencies, annotation processors (Lombok, MapStruct), OpenAPI code generation, test execution, and coverage reporting. |
| **JaCoCo** | Coverage | Measures test coverage and enforces an 80% minimum line coverage gate on every build. Generated OpenAPI model classes are excluded. |

### Why `Pageable` with `PageRequest.of(0, 1)` instead of `findFirst`?

The JPA query in `PriceJpaRepository` uses `Pageable` to pass `LIMIT 1` down to the database. The database returns at most one row after applying `ORDER BY priority DESC`. Without this, the query would fetch all matching rows and discard all but the first in Java — wasteful at scale.

---

## Performance optimisations

### Database index

`schema.sql` defines a composite index on the columns used in the lookup query:

```sql
CREATE INDEX IF NOT EXISTS idx_prices_lookup
    ON prices (brand_id, product_id, start_date, end_date, priority);
```

This covers the `WHERE brand_id = ? AND product_id = ? AND start_date <= ? AND end_date >= ?` filter and allows the engine to satisfy `ORDER BY priority DESC` without a separate sort step. Without it, every request would scan the full table.

### Application cache

`PriceServiceImpl.getApplicablePrice()` is annotated with `@Cacheable`. Identical queries (same `productId`, `brandId`, `applicationDate`) hit the Caffeine cache instead of the database. Configuration:

| Profile | Max entries | TTL |
|---|---|---|
| `dev` | 500 | 60 s |
| `prod` | 2 000 | 300 s |

---

## Local setup

### Prerequisites

- **Java 25** (`java -version` should report 25)
- No other external services required — H2 runs in-memory

### Run

```bash
./gradlew bootRun
# Server starts at http://localhost:8080
```

### Test

```bash
./gradlew test                         # run all tests
./gradlew jacocoTestReport             # generate HTML coverage report
./gradlew jacocoTestCoverageVerification  # fail if coverage < 80%
```

Coverage report: `build/reports/jacoco/test/html/index.html`

### H2 console (dev profile only)

Navigate to [http://localhost:8080/h2-console](http://localhost:8080/h2-console) and connect with:

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:pricesdb` |
| Username | `sa` |
| Password | *(empty)* |

### Application profiles

| Profile | Activated by | SQL logging | H2 console | Cache TTL |
|---|---|---|---|---|
| `dev` (default) | `spring.profiles.active=dev` | enabled | enabled | 60 s |
| `prod` | `SPRING_PROFILES_ACTIVE=prod` | disabled | disabled | 300 s |

---

## API usage

Full contract: [`docs/openapi/api-definition.yaml`](docs/openapi/api-definition.yaml)

### `GET /prices`

| Parameter | Type | Required | Description |
|---|---|---|---|
| `applicationDate` | ISO-8601 datetime | yes | Point in time for the price query |
| `productId` | integer (int64) | yes | Product identifier |
| `brandId` | integer (int64) | yes | Brand identifier |

### Examples

**Applicable price found (200)**

```bash
curl "http://localhost:8080/prices?applicationDate=2020-06-14T10:00:00&productId=35455&brandId=1"
```

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 1,
  "startDate": "2020-06-14T00:00:00",
  "endDate": "2020-12-31T23:59:59",
  "price": 35.5,
  "currency": "EUR"
}
```

**Higher-priority overlap wins (200)**

```bash
curl "http://localhost:8080/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1"
```

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 2,
  "startDate": "2020-06-14T15:00:00",
  "endDate": "2020-06-14T18:30:00",
  "price": 25.45,
  "currency": "EUR"
}
```

**No price found (404)**

```bash
curl "http://localhost:8080/prices?applicationDate=2020-06-14T10:00:00&productId=99999&brandId=1"
```

```json
{
  "status": 404,
  "message": "No applicable price found for productId=99999, brandId=1 at 2020-06-14T10:00:00"
}
```

**Missing required parameter (400)**

```bash
curl "http://localhost:8080/prices?productId=35455&brandId=1"
```

```json
{
  "status": 400,
  "message": "Missing required parameter: applicationDate"
}
```

**Wrong parameter type (400)**

```bash
curl "http://localhost:8080/prices?applicationDate=2020-06-14T10:00:00&productId=35455&brandId=abc"
```

```json
{
  "status": 400,
  "message": "Invalid value for parameter 'brandId': abc"
}
```

### All five required test cases

| # | `applicationDate` | `productId` | `brandId` | Expected `priceList` | Expected `price` |
|---|---|---|---|---|---|
| 1 | `2020-06-14T10:00:00` | 35455 | 1 | 1 | 35.50 |
| 2 | `2020-06-14T16:00:00` | 35455 | 1 | 2 | 25.45 |
| 3 | `2020-06-14T21:00:00` | 35455 | 1 | 1 | 35.50 |
| 4 | `2020-06-15T10:00:00` | 35455 | 1 | 3 | 30.50 |
| 5 | `2020-06-16T21:00:00` | 35455 | 1 | 4 | 38.95 |
