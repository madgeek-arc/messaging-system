# Messaging System

A reactive messaging service with a Spring Boot client. Applications create topic threads and post messages through a REST API; the service stores them in MongoDB and supports inbox and outbox searches, unread tracking, replies and user anonymization.

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
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

Configure the service location:

```properties
messaging-system.client.endpoint=http://localhost:8383/api/
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

Methods return `Mono`/`Flux`. A failed request (connection error, non-2xx response) surfaces as a `WebClientResponseException` carrying the status and body. A missing `messaging-system.client.endpoint` is logged as a warning at startup.

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
| `PATCH` | `threads/{threadId}/messages/{messageId}` | Mark a message as read |
| `GET` | `inbox/threads/search`, `inbox/threads/count`, `inbox/threads/unread` | Inbox queries |
| `GET` | `outbox/threads/search`, `outbox/threads/count` | Outbox queries |
| `POST` | `user/anonymize` | Anonymize a user's personal data |

The full API is documented by the service's OpenAPI endpoints (see below).

## Running the service

### Build

```bash
./mvnw clean package
```

This runs the tests and the license header check, and produces an executable jar at `messaging-service/target/messaging-service-<version>.jar`.

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

The API is public by default. To require a valid JWT on every request, set the issuer of your identity provider:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://idp.example.org/realms/example
```

Callers then send `Authorization: Bearer <jwt>`. The health endpoint (and the API documentation, when enabled) stays open. Leave the property unset, not empty, to run without authentication.

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

## License

Apache License 2.0. See [LICENSE](LICENSE).
