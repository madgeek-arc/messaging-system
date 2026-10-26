# Messaging System — Configuration

Defaults are in `messaging-service/src/main/resources/application.properties` (packaged in the
service jar). Override them with `--spring.config.additional-location`, environment variables
(relaxed binding, for example `SPRING_DATA_MONGODB_HOST`) or command-line arguments, following
Spring Boot's standard precedence. A copyable example is in
[`../application-example.properties`](../application-example.properties).

## messaging-service

| Property | Required | Default (packaged) | Description |
|---|---|---|---|
| `server.port` | No | `8383` | HTTP port |
| `spring.webflux.base-path` | No | `/api` | Prefix of every endpoint |
| `server.forward-headers-strategy` | No | `framework` | Honour `X-Forwarded-*` from a reverse proxy |
| `spring.data.mongodb.host` / `.port` / `.database` | Yes | `localhost` / `27017` / `messaging-dev` | MongoDB connection (or use `spring.data.mongodb.uri`) |
| `spring.data.mongodb.username` / `.password` | When Mongo auth is on | empty | **Secret** (password) |
| `spring.data.mongodb.auto-index-creation` | No | `true` | Create `@Indexed` indexes at startup |
| `messaging-system.api-key` | No | unset | When set, every request except health and docs needs this value in the `X-API-Key` header. **Secret** |
| `spring.security.oauth2.resourceserver.jwt.issuer-uri` | No | unset | When set, every request except health and docs requires a valid bearer JWT. **Leave it unset, not empty**, to disable it |
| `spring.security.oauth2.resourceserver.jwt.audiences` | With an issuer | unset | Accept only tokens issued for these audiences |
| `springdoc.api-docs.enabled` / `springdoc.swagger-ui.enabled` | No | `false` / `false` | Expose OpenAPI and Swagger UI |
| `management.endpoints.web.exposure.include` | No | `health` | Actuator exposure |
| `management.endpoint.health.show-details` | No | `when-authorized` | |
| `management.endpoint.health.probes.enabled` | No | `true` | `/actuator/health/liveness`, `/readiness` |
| `sentry.dsn` (and other `sentry.*`) | No | unset | Enables Sentry reporting (Sentry Spring Boot starter). Unset, `Sentry.captureException` is a no-op |
| `logging.level.*` | No | `INFO` | Log4j2 levels |
| `project.version` | — | `@project.version@` (filtered at build) | Shown in the OpenAPI info |

Keys and defaults come from `application.properties` and `config/*`.

## messaging-client

Bound by `messaging-client/.../config/MessagingClientProperties.java` (`messaging-system.*`). The
configuration metadata is published in the jar, so IDEs complete these keys.

| Property | Required | Default | Description |
|---|---|---|---|
| `messaging-system.client.endpoint` | **Yes** | — | Service base URL including the base path, for example `http://messaging:8383/api/`. If it's missing, startup only logs a warning |
| `messaging-system.client.api-key` | When the service sets `messaging-system.api-key` | unset | Sent as `X-API-Key`. **Secret** |
| `messaging-system.client.connect-timeout` | No | `10s` | Connection timeout |
| `messaging-system.client.response-timeout` | No | `30s` | Time allowed for the service to respond |

The auto-configured `MessagingService` uses the application's `WebClient.Builder`, so its
`WebClientCustomizer` beans (filters, default headers) apply. The client sets its own connector to
apply the timeouts.

## Environments

| Environment | Notes |
|---|---|
| Local | MongoDB on `localhost:27017`, no API key or issuer, Swagger enabled if needed |
| Tests | Context tests use `MessagingServiceConfig` only. Service tests start MongoDB with Testcontainers (Docker) |
| Production | API key or issuer, MongoDB credentials and Sentry DSN, set per deployment through environment variables or an external configuration. Not stored in the repository |

## Secrets

The API key, the MongoDB password and the Sentry DSN. Inject them through the environment or a
secret store. Never commit them.
