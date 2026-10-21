# Messaging System — Security

## Authentication

The service authenticates the **calling application**, not end users. Two optional mechanisms
exist; they can be combined.

| Mechanism | Trigger | Behaviour | Evidence |
|---|---|---|---|
| API key | `messaging-system.api-key` set | Every request needs the same value in the `X-API-Key` header (constant-time comparison), otherwise `401` | `config/ApiKeyWebFilter`, `config/SecurityConfig` |
| JWT | `spring.security.oauth2.resourceserver.jwt.issuer-uri` set | Every request needs a valid bearer JWT from the issuer (signature, issuer, expiry), otherwise `401` | `config/SecurityConfig` |
| Neither | both unset | All endpoints open. Logs `WARN Neither messaging-system.api-key nor a JWT issuer is configured: all endpoints are public` | `config/SecurityConfig` |

`/actuator/health/**`, `/v3/api-docs/**`, `/swagger-ui/**` and `/webjars/**` are exempt from both.
Sessions are stateless (`NoOpServerSecurityContextRepository`). (`ApiKeySecurityTest`,
`PublicModeSecurityTest`)

In JWT mode, set `spring.security.oauth2.resourceserver.jwt.audiences` so that only tokens issued for
this service are accepted.

## Authorisation

**None in the service.** User and group identity comes from **request parameters** (`email`,
`groupId`, `groups`, `userId`, message `from`), not from a token. (`MessagingController`)

The calling application is responsible for per-user authorisation: it must check that the
current user may act as the `email`/`groupId` it sends. Restrict the API to such applications with
the API key or a JWT issuer, and keep it off public networks.

**Planned:** end-user authorisation, with identity taken from the user's JWT instead of request
parameters, is intended for a later version. It isn't implemented yet.

## Client authentication

`messaging-client` sends `messaging-system.client.api-key` as `X-API-Key` when set. It builds its
`WebClient` from the application's `WebClient.Builder`, so a filter added through a
`WebClientCustomizer` (for example an OAuth2 client-credentials filter for JWT mode) applies to
every call. (`MessagingWebClients`, `MessagingClientConfigTest`)

## Input handling

- The `regex` parameters of inbox and outbox search and count are matched **literally**: the
  service quotes them (`SearchTerms.literal`) before using them in the case-insensitive MongoDB
  `$regex` filters. (`SearchTermsTest`, `DefaultTopicThreadServiceTest`)
- Erasure quotes the email (`Pattern.quote`) and anchors it, so the match is exact.
  (`DefaultTopicThreadService#anonymizeUser`)
- A new message is stored inside a `$literal` expression, so `$`-prefixed values in its content are
  stored as data. (`DefaultTopicThreadService#addMessage`)

## Headers, CORS, CSRF

CSRF disabled (stateless API), CORS disabled (no cross-origin browser access), X-XSS-Protection
header disabled. (`SecurityConfig`)

Health details (`show-details=when-authorized`) are shown to any caller with a valid JWT. The API
key creates no principal, so it doesn't reveal them.

## Personal data

- Threads store names and emails of correspondents and the `readBy` email sets.
- Erasure (`POST user/anonymize`) replaces the user's name and email with `[Deleted User]` in all
  correspondent slots and removes their read receipts. Subjects and bodies are kept.
  (`PersonalDataEraser`, 10 unit tests)
- Unhandled exceptions are sent to Sentry when `sentry.dsn` is set (`Sentry.captureException`).
  The Sentry starter's `sentry.send-default-pii` defaults to `false`, but request URLs, whose query
  strings carry emails, may still be attached. Configure Sentry data scrubbing accordingly.

## Secrets

The API key, the MongoDB password and the Sentry DSN. Supply them through the environment. Never
commit them.
