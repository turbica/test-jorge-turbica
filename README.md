# Prices API

REST service returning the applicable sale price for a product and brand at a given point in time. When price ranges overlap, the highest-priority entry wins.

Built with Java 25 · Spring Boot 4.0.5 · Spring Data JPA · H2 · MapStruct · OpenAPI Generator · Gradle.

## Architecture

Hexagonal (ports & adapters): `domain` → `application` → `infrastructure` (REST + persistence). The domain layer has no framework dependencies.

## Run

```bash
./gradlew bootRun   # http://localhost:8080
./gradlew test
```

## Endpoint

```
GET /prices?applicationDate=2020-06-14T10:00:00&productId=35455&brandId=1
```

Returns `productId`, `brandId`, `priceList`, `startDate`, `endDate`, `price`, `currency`. `404` if no price applies.

Full contract: [`docs/openapi/api-definition.yaml`](docs/openapi/api-definition.yaml)
