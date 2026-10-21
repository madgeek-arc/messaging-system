# Messaging System — Testing

| Module | Class | Tests | Covers |
|---|---|---|---|
| `messaging-core` | `privacy/PersonalDataEraserTest` | 10 | Every correspondent slot, read receipts, idempotency, case-insensitive match, untouched fields |
| `messaging-service` | `MessagingSystemApplicationTests` | 2 | Context with `MessagingServiceConfig` only (no MongoDB) |
| `messaging-service` | `service/DefaultTopicThreadServiceTest` | 10 | Against MongoDB (Testcontainers `mongo:7`): concurrent message appends and read receipts, message ids, 400/404 cases, `PUT` keeping the path id, literal inbox search |
| `messaging-service` | `service/SearchTermsTest` | 3 | Quoting of search terms |
| `messaging-service` | `config/ApiKeySecurityTest` | 6 | `401` without or with a wrong key, access with the key, exempt health path, error statuses through the controller |
| `messaging-service` | `config/PublicModeSecurityTest` | 1 | Open access when neither the key nor an issuer is set |
| `messaging-client` | `config/MessagingClientConfigTest` | 5 | API key header, application `WebClientCustomizer` applied, response timeout, no application builder, back-off when the application defines its own `MessagingService` |

## Running

```bash
./mvnw verify
./mvnw -pl messaging-core test -Dtest=PersonalDataEraserTest
```

Result on `fix/report-issues`: 37 tests, 0 failures. `DefaultTopicThreadServiceTest` needs Docker
and is skipped without it.

## Not covered

- JWT mode.
- The remaining repository queries (unread, outbox, user search, erasure query) against MongoDB.
- No coverage tool and no CI configuration in the repository.
