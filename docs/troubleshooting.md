# Messaging System — Troubleshooting

| Problem | Possible cause | What to check | Resolution |
|---|---|---|---|
| Every client call fails with `401` | The service sets `messaging-system.api-key` or a JWT issuer, and the client doesn't send matching credentials | Service and client configuration | Set `messaging-system.client.api-key` to the service's key. For JWT mode, add a token filter through a `WebClientCustomizer` |
| Authenticated requests fail although tokens are valid | Issuer unreachable or wrong, or the token's audience doesn't match `jwt.audiences`. OIDC discovery/JWKS is resolved when the first token is validated (Spring Boot's lazy `SupplierReactiveJwtDecoder`) | Logs on the first protected request | Fix the URL, the audience or network access to the IdP |
| `issuer-uri` set to an empty value behaves unexpectedly | The property must be unset, not empty | Effective configuration | Remove the property |
| Client calls go to the wrong path (`404`) | `messaging-system.client.endpoint` missing `/api/` | Client warning and URL | Include the base path, for example `http://host:8383/api/` |
| Client warns `Missing property: 'messaging-system.client.endpoint'` | Endpoint unset | Consumer configuration | Set the property |
| Client calls fail with `ReadTimeoutException` / `ConnectTimeoutException` | Service slow or unreachable within `messaging-system.client.response-timeout` (30 s) or `connect-timeout` (10 s) | Service logs and network | Fix the service or raise the timeouts |
| `404` when posting a message or marking it read | Unknown thread id, or `messageId` beyond the thread's messages | Thread id and message position | Use an existing thread and a message position (`0`, `1`, ...) |
| `400` when posting a message or marking it read | Missing `from.email`, missing `userId`, or non-numeric `messageId` | Request body and parameters | Send the missing fields |
| Search with `.`, `*` or `(` finds fewer threads than before | Search terms are matched literally, not as regular expressions | `regex` parameter | Send the plain text to look for |
| Health `DOWN` / `5xx` errors | MongoDB unreachable or authentication failure | Logs, `spring.data.mongodb.*` | Fix the connection settings |
| Swagger UI `404` | Disabled by default | `springdoc.*.enabled` | Enable both properties |
| Build fails at `validate` with licence errors | Missing headers or shallow clone | `./mvnw license:check` | `./mvnw com.mycila:license-maven-plugin:5.0.0:format`. Use a full clone |
