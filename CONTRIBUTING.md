# Contributing

Contributions are welcome: bug reports, feature requests and pull requests.

## Reporting issues

Use [GitHub Issues](https://github.com/madgeek-arc/messaging-system/issues) for bugs, feature requests and
questions. For bugs, include the version, the environment, the steps to reproduce, and the expected
and actual behaviour.

Don't report security vulnerabilities in public issues; follow [SECURITY.md](SECURITY.md) instead.

## Branching

- `main` is the integration and release branch and carries the next `-SNAPSHOT` version.
- Work on short-lived branches named `<type>/<short-description>` (for example `fix/client-auth`) and
  open a pull request against `main`.
- Without write access to the repository, fork it, create the branch in your fork and open the
  pull request from there.

## Commit messages

Use [Conventional Commits](https://www.conventionalcommits.org/) with module scopes where useful:
`feat(service):`, `fix(client):`, `feat(privacy):`, `docs:`, `build:`. The release plugin prefixes its
commits with `chore(release):`.

## Pull requests

- Describe the change and its motivation, and link the related issue.
- At least one maintainer review is required before merging.
- Changes to `messaging-core` (DTOs, paths, the controller interface) affect both the service and
  every client consumer. Call them out.
- Update `README.md`/`docs/` for API, configuration or security changes, and add a `CHANGELOG.md`
  entry.

## Required checks

- `./mvnw clean verify` must pass. This includes the licence header check, which needs a full clone.
- Run `./mvnw com.mycila:license-maven-plugin:5.0.0:format` after adding Java files.
- Privacy-related changes must keep `PersonalDataEraserTest` green and extend it.

## Releases and versioning

- [Semantic Versioning](https://semver.org/).
- Release with `maven-release-plugin` (`./mvnw release:prepare release:perform`). Tags are `<version>`.
