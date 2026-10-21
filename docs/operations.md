# Messaging System — Operations

## Start / stop

A standard Spring Boot WebFlux (Netty) process. Stop it with `SIGTERM`. Graceful shutdown is on by
default (Spring Boot 3.4+; this repository uses 3.5 and doesn't override `server.shutdown`).

## Health, readiness, liveness

| Endpoint | Exposure | Evidence |
|---|---|---|
| `/api/actuator/health` | Open, also with an API key or JWT issuer set. Details shown only to callers with a valid JWT | `application.properties`, `SecurityConfig` |
| `/api/actuator/health/liveness`, `/readiness` | Open (`management.endpoint.health.probes.enabled=true`) | `application.properties` |

The MongoDB health indicator is included automatically (reactive Mongo starter with actuator).

## Logging

Log4j2, `INFO` by default. Notable messages:

| Message | Meaning |
|---|---|
| `WARN Neither messaging-system.api-key nor a JWT issuer is configured: all endpoints are public` | Service running without authentication |
| `INFO Erased personal data from N thread(s).` | Erasure completed (the email isn't logged) |
| client `WARN Missing property: 'messaging-system.client.endpoint'` | Client misconfigured in a consumer |

## Metrics and alerting

Only `health` is exposed. Exceptions go to Sentry (`SentryErrorReport`) when `sentry.dsn` is set.

## Background jobs

None.

## Data, backups, restore

All state lives in MongoDB (collection `topicThread`, the Spring Data default for `@Document class TopicThread`). Back it up
regularly. Erasure requests (`POST user/anonymize`) rewrite documents in place. Backups taken
earlier still contain the erased data, so align backup retention with erasure obligations.
