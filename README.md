# Messaging System

A reactive messaging service with a Spring Boot client. Applications create topic threads and post messages through a REST API; the service stores them in MongoDB and supports inbox and outbox searches, unread tracking, replies and user anonymization.

## Role

Stores and serves message threads between users and user groups (inbox/outbox, unread tracking,
replies, GDPR erasure). Backend components embed `messaging-client` to call it. The service itself
depends only on MongoDB and, optionally, an OIDC/JWT issuer and Sentry.

## Modules

| Module | Description |
|--------|-------------|
| `messaging-core` | Shared domain model, DTOs, `ThreadOperations` interface and REST paths |
| `messaging-service` | Spring Boot WebFlux application exposing the REST API, backed by MongoDB |
| `messaging-client` | Spring Boot auto-configured, reactive `MessagingService` implementation that calls the service |

## Using the client

Add the dependency (Java 21, Spring Boot 3):

```xml
<dependency>
    <groupId>gr.athenarc</groupId>
    <artifactId>messaging-client</artifactId>
    <version>0.1.0</version>
</dependency>
```

Configure the service location:

```properties
messaging-system.client.endpoint=http://localhost:8383/api/
# when the service requires an API key
#messaging-system.client.api-key=<key>
# defaults shown
#messaging-system.client.connect-timeout=10s
#messaging-system.client.response-timeout=30s
```

The client auto-configures a `MessagingService` bean; inject it and send:

```java
@Service
public class Notifications {

    private final MessagingService messaging;

    public Notifications(MessagingService messaging) {
        this.messaging = messaging;
    }

    public Mono<ThreadDTO> ask(String fromEmail, String toGroup) {
        ThreadDTO thread = new ThreadDTO();
        thread.setSubject("Question");
        thread.setFrom(new Correspondent("Jane Doe", fromEmail, null));
        thread.setTo(List.of(new Correspondent(null, null, toGroup)));
        return messaging.add(thread);
    }
}
```

A thread has a `subject`, `tags`, a sender (`from`), recipients (`to`) and a list of messages. Each message has a `body`, `from`, `to`, `anonymousSender`, `replyToMessageId` and a per-user `read` flag. A correspondent is identified by `name`, `email` and `groupId`.

Methods return `Mono`/`Flux`. A non-2xx response surfaces as a `WebClientResponseException` carrying the status and body; connection errors and timeouts surface as `WebClientRequestException`. A missing `messaging-system.client.endpoint` is logged as a warning at startup.

The client is built from the application's `WebClient.Builder`, so filters registered through a `WebClientCustomizer` bean (for example one adding a bearer token) apply to its calls.

### Calling the API directly

```bash
curl -X POST http://localhost:8383/api/threads \
  -H 'Content-Type: application/json' \
  -d '{
        "subject": "Question",
        "from": {"name": "Jane Doe", "email": "jane@example.org"},
        "to": [{"groupId": "support"}]
      }'
```

Main endpoints, relative to the service base path:

| Method | Path | Purpose |
|--------|------|---------|
| `POST` | `threads` | Create a thread |
| `GET` / `PUT` / `DELETE` | `threads/{threadId}` | Read, update or delete a thread |
| `POST` | `threads/{threadId}/messages` | Add a message to a thread |
| `PATCH` | `threads/{threadId}/messages/{messageId}` | Mark a message as read or unread |
| `GET` | `inbox/threads/search`, `inbox/threads/count`, `inbox/threads/unread` | Inbox queries |
| `GET` | `outbox/threads/search`, `outbox/threads/count` | Outbox queries |
| `POST` | `user/anonymize` | Anonymize a user's personal data |

The `regex` search parameters are matched as literal text, case-insensitively. Unknown threads or messages return `404`, and invalid message or read requests return `400`. The full API is documented by the service's OpenAPI endpoints (see below).

## Running the service

### Build

```bash
./mvnw clean package
```

This runs the tests and the license header check, and produces an executable jar at `messaging-service/target/messaging-service-<version>.jar`. To build an OCI
image instead, run `./mvnw install -DskipTests && ./mvnw -pl messaging-service spring-boot:build-image`;
the image build takes `messaging-core` from the local repository, so it must be installed first.

### Configure

The service needs MongoDB, configured through the standard Spring Boot properties:

```properties
server.port=8383
spring.webflux.base-path=/api

spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=messaging
# only when MongoDB authentication is enabled
#spring.data.mongodb.username=
#spring.data.mongodb.password=
```

#### Authentication

The API is open by default and logs a warning at startup. Two optional mechanisms authenticate the calling application; they can be combined.

An API key, sent by callers in the `X-API-Key` header (the client sends `messaging-system.client.api-key`):

```properties
messaging-system.api-key=<key>
```

A JWT from your identity provider, sent as `Authorization: Bearer <jwt>`:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://idp.example.org/realms/example
spring.security.oauth2.resourceserver.jwt.audiences=messaging-system
```

Leave `issuer-uri` unset, not empty, to run without JWT. The health endpoint (and the API documentation, when enabled) stays open in both cases.

The service doesn't authorise individual users: it trusts the `email` and `groupId` values the caller sends, so the calling application must check them. End-user authorisation is planned for a later version.

### Run

```bash
java -jar messaging-service/target/messaging-service-<version>.jar \
  --spring.config.additional-location=file:./application.properties
```

Any property can also be supplied as an environment variable (e.g. `SPRING_DATA_MONGODB_HOST`).

During development the service can be started directly from Maven:

```bash
./mvnw -pl messaging-service spring-boot:run
```

The OpenAPI document and Swagger UI are disabled by default. Enable them with:

```properties
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
```

The UI is then served at `/api/swagger-ui.html` (for example `http://localhost:8383/api/swagger-ui.html`) and the OpenAPI document at `/api/v3/api-docs`.

## Contributing: license headers

Every Java file carries an Apache 2.0 header, checked during `validate` when the root project is part of the build. After adding or editing files, run:

```bash
./mvnw com.mycila:license-maven-plugin:5.0.0:format
```

Copyright years are derived from each file's git history, so builds need a full (non-shallow) clone.

## Documentation

- [Architecture](docs/architecture.md) · [Integration](docs/integration.md) · [API](docs/api.md)
- [Configuration](docs/configuration.md) · [Example configuration](application-example.properties)
- [Deployment](docs/deployment.md) · [Operations](docs/operations.md) · [Troubleshooting](docs/troubleshooting.md)
- [Security model](docs/security-model.md) · [Security policy](SECURITY.md) · [Development](docs/development.md) · [Testing](docs/testing.md)
- [Contributing](CONTRIBUTING.md) · [Changelog](CHANGELOG.md)

## License

Apache License 2.0. See [LICENSE](LICENSE). Maintained by the madgeek-arc team
(<https://github.com/madgeek-arc/messaging-system>).
