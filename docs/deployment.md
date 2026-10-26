# Messaging System — Deployment

The repository has no Dockerfile, compose file, Kubernetes manifest or CI pipeline. Production
deployment isn't defined in this repository. The service can run as:

- the executable jar, managed as a system service (for example a systemd unit);
- a container built from the OCI image, started with Docker Compose or deployed on Kubernetes.

## Prerequisites

- JDK 21.
- MongoDB reachable from the service.
- Optional: an OIDC issuer for JWT mode. Outbound HTTPS to Sentry if a DSN is set.
- Docker, to run the MongoDB tests during the build (they're skipped without it).
- A **full git clone** to build: the licence-header check derives copyright years from history.

## Build artefacts

| Command | Artefact |
|---|---|
| `./mvnw clean package` | `messaging-service/target/messaging-service-<version>.jar` (Spring Boot executable), plus `messaging-core` and `messaging-client` jars |
| `./mvnw -pl messaging-service spring-boot:build-image` | OCI image `messaging-service:<version>` (Cloud Native Buildpacks; the POM also declares `native-maven-plugin` for the image build) |
| `./mvnw release:prepare release:perform` | Tag and deploy (`maven-release-plugin`) |

A CI job outside the repository builds and tags the image like this:

```bash
image="docker.madgik.di.uoa.gr/messaging-service:$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout | tail -1 | sed s/-SNAPSHOT//g)"
mvn -f messaging-service/pom.xml spring-boot:build-image -Dspring-boot.build-image.imageName=${image}
```

## Runtime

| Aspect | Value |
|---|---|
| Port | `8383` |
| Base path | `/api` |
| Volumes | none |
| Dependent services | MongoDB (required). OIDC issuer and Sentry (optional) |
| Order | MongoDB → messaging-service → applications using `messaging-client` |
| Migrations | none. Indexes are created at startup (`auto-index-creation=true`) |

```bash
java -jar messaging-service/target/messaging-service-<version>.jar \
  --spring.config.additional-location=file:./application.properties
```

## Production requirements

- Set `messaging-system.api-key` (and the same `messaging-system.client.api-key` in calling
  applications), or a JWT issuer with `jwt.audiences`. Either way, make the API reachable only by
  backends that authorise their users (see [security-model.md](security-model.md)).
- Keep Swagger disabled unless the docs endpoints are access-controlled.
- Use MongoDB authentication and TLS (`spring.data.mongodb.uri` with `tls=true`).

## Post-deployment verification

```bash
curl -s http://<host>:8383/api/actuator/health            # {"status":"UP"}
curl -s http://<host>:8383/api/actuator/health/readiness
```

## Rollback and recovery

The service is stateless apart from MongoDB. Redeploy the previous jar. Back up and restore MongoDB
with `mongodump`/`mongorestore`.
