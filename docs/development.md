# Messaging System — Development

## Tools

| Tool | Version | Evidence |
|---|---|---|
| JDK | 21 | `pom.xml` |
| Maven | wrapper (`./mvnw`) | `.mvn/wrapper` |
| MongoDB | any recent version, locally (for example `docker run -p 27017:27017 mongo`) | `application.properties` |
| Docker | for the Testcontainers MongoDB tests | `messaging-service/pom.xml` |

## Commands

```bash
./mvnw clean verify                                        # tests + licence header check
./mvnw -pl messaging-service spring-boot:run               # run on :8383/api against local MongoDB
./mvnw install -DskipTests                                 # install SNAPSHOTs for local consumers
./mvnw com.mycila:license-maven-plugin:5.0.0:format        # add/refresh Apache headers
```

Verified: `./mvnw verify` passes on `fix/report-issues` (37 tests). The MongoDB tests use
Testcontainers and need Docker.

## Local tips

- Locally the API is open (no API key or issuer). Set `messaging-system.api-key` to try the key,
  or point `issuer-uri` at a local Keycloak realm to try JWT mode.
- The Spring Boot configuration processor runs at compile time (`maven.compiler.proc=full`, needed on
  JDK 23+) and publishes the `messaging-system.client.*` metadata.
- Enable `springdoc.api-docs.enabled=true` and `springdoc.swagger-ui.enabled=true` to try requests in
  the browser.
- Any new mapping on `TopicThreadsController` (core) is also implemented by the client. Mappings that
  must not appear on consumers' APIs belong on the service's `MessagingController` only.

## Conventions

Conventional Commits with scopes (`feat(service):`, `fix(client):`, `feat(privacy):`). Release commits are
prefixed `chore(release):`. Apache 2.0 header on every Java file, enforced at `validate`.
