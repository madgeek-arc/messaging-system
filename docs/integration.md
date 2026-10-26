# Messaging System — Integrations

| Direction | Counterpart | Protocol | Purpose | Configuration key | Evidence path/symbol |
|---|---|---|---|---|---|
| provided | Any HTTP caller, typically an application embedding `messaging-client` | REST JSON under `/api` | Thread and message operations, inbox/outbox, erasure | `server.port` (8383), `spring.webflux.base-path` (`/api`), `messaging-system.api-key` | `messaging-service/.../controller/MessagingController.java` |
| consumed | MongoDB | Reactive MongoDB driver | Persist threads | `spring.data.mongodb.*` | `repository/ReactiveMongoTopicThreadRepository.java` |
| consumed | OIDC issuer (optional) | HTTPS: discovery and JWKS | Validate bearer JWTs | `spring.security.oauth2.resourceserver.jwt.issuer-uri`, `.audiences` | `config/SecurityConfig.java` |
| consumed | Sentry (optional) | HTTPS | Unhandled exception reporting | `sentry.dsn` (Sentry starter; not set in the repository) | `config/SentryErrorReport.java` |
| provided (library) | Host Spring Boot application | In-process `MessagingService` bean (`Mono`/`Flux`) | Call the service | `messaging-system.client.*` | `messaging-client/.../config/MessagingClientConfig.java` |
| consumed (library) | `messaging-service` | HTTP through `WebClient` | Client implementation | `messaging-system.client.endpoint` (must include `/api/`), `.api-key`, `.connect-timeout`, `.response-timeout` | `messaging-client/.../service/MessagingService.java` |

Which applications embed `messaging-client` is recorded in their own documentation and in the
platform integration map.

## Provided interfaces

See [api.md](api.md). The OpenAPI document is generated at runtime when
`springdoc.api-docs.enabled=true`.

## Authentication between client and service

- **API key:** when the service sets `messaging-system.api-key`, set the same value in
  `messaging-system.client.api-key`. The client sends it as `X-API-Key`.
- **JWT:** the auto-configured client is built from the application's `WebClient.Builder`, so a
  filter registered through a `WebClientCustomizer` (for example an OAuth2 client-credentials
  filter) adds the bearer token. Applications constructing `MessagingService` themselves pass a
  builder to the `(MessagingClientProperties, WebClient.Builder)` constructor.
  (`MessagingClientConfig`, `MessagingClientConfigTest`)

Either way the service trusts the `email`/`groupId` values the application sends, so the
application must authorise its users before calling it (see [security-model.md](security-model.md)).

## Failure behaviour

| Situation | Behaviour | Evidence |
|---|---|---|
| Client: non-2xx response | `WebClientResponseException` with status and body | `MessagingService` (`retrieve()`) |
| Client: endpoint missing | `WARN Missing property: 'messaging-system.client.endpoint'` at startup. Calls fail at runtime | `MessagingClientProperties#checkMissing` |
| Client timeouts | Connect `10s`, response `30s` (`messaging-system.client.*-timeout`). No retries | `MessagingWebClients` |
| Service: unhandled exception | Captured to Sentry (when configured), then Spring's default problem response | `SentryErrorReport` |
| Service: MongoDB unavailable | Reactive errors propagate as `5xx`. Health reports `DOWN` | Spring Boot defaults |
| Service: missing or wrong credentials | `401` | `ApiKeyWebFilter`, `SecurityConfig` |
| Service: unknown thread or message | `404` on `PUT`, message posting and read marking | `DefaultTopicThreadService` |
| Service: invalid message or read request | `400` | `DefaultTopicThreadService` |
| Service: concurrent messages or read receipts on one thread | Each is applied with a single atomic update; none is lost | `DefaultTopicThreadService` |
| Erasure with blank email | `400 email is required` | `MessagingController#anonymizeUser` |
