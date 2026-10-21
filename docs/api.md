# Messaging System — API

| Item | Value |
|---|---|
| Base URL | `http://<host>:8383/api` (`server.port`, `spring.webflux.base-path`) |
| Versioning | None in the path |
| Authentication | None by default. `X-API-Key` header when `messaging-system.api-key` is set. Bearer JWT when `spring.security.oauth2.resourceserver.jwt.issuer-uri` is set. Health and docs stay open (see [security.md](security.md)) |
| OpenAPI | `/api/v3/api-docs`. Swagger UI `/api/swagger-ui.html`. Both **disabled by default** (`springdoc.api-docs.enabled`, `springdoc.swagger-ui.enabled`). The bearer scheme is added to the document when an issuer is configured (`OpenApiConfig`) |
| Health | `/api/actuator/health`, with `/liveness` and `/readiness` probes |

## Endpoint groups

Paths are relative to the base URL. They're defined in `messaging-core/.../controller/RestApiPaths.java`
and `TopicThreadsController.java`, with the erasure and user-lookup endpoints in
`messaging-service/.../controller/MessagingController.java`. The generated OpenAPI is
authoritative. The README has examples.

| Group | Paths | Notes |
|---|---|---|
| Threads | `POST threads`, `GET/DELETE threads/{threadId}`, `PUT threads/{threadId}` | `GET` takes `email` and/or `groupId` query parameters and returns the thread only if they match a correspondent (empty response otherwise). `PUT` replaces the thread named in the path, whatever `id` the body carries |
| Messages | `POST threads/{threadId}/messages?anonymous=`, `PATCH threads/{threadId}/messages/{messageId}?read=&userId=` | Add a message or reply (`from.email` required). Mark read/unread for `userId` (email). `messageId` is the message's position in the thread (`0`, `1`, ...) |
| Inbox | `GET inbox/unread?groups=&email=`, `GET inbox/threads/count`, `GET inbox/threads/search`, `GET inbox/threads/unread` | `groupId`/`groups`, `email`, `regex`, paging (`page`, `size`, `sortBy`, `direction`). `regex` is matched as literal text, case-insensitively, against subject, tags and correspondent emails |
| Outbox | `GET outbox/threads/count`, `GET outbox/threads/search` | Same parameters as inbox. `regex` matches subject and tags |
| Users | `GET user?email=` | Threads in which the email appears as a sender or recipient |
| Privacy | `POST user/anonymize` with body `{"email": "..."}` | Returns the number of threads modified. The email goes in the body, so it never lands in access logs |

Responses are `ThreadDTO` (thread with per-user `read` flags computed for the given email),
`UnreadThreads`, or integers for counts. (`dto/*`)

## Status codes

| Status | When |
|---|---|
| `400` | Message without `from.email`, read marking without `userId` or with a non-numeric or negative `messageId`, erasure without `email` |
| `401` | Missing or wrong `X-API-Key` (API key set) or bearer JWT (issuer set) |
| `404` | `PUT` of an unknown thread, message posted to an unknown thread, read marking of an unknown thread or message |
| `5xx` | MongoDB errors and other unhandled exceptions (reported to Sentry when configured) |

(`DefaultTopicThreadService`, `MessagingController`, tests)
