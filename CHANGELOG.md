## [0.1.0](https://github.com/madgeek-arc/messaging-system/compare/messaging-0.0.1...0.1.0) (2026-10-06)

### Features

* Add search for topics by user email ([e69575c](https://github.com/madgeek-arc/messaging-system/commit/e69575ce7ebd8ea888dc3bc585cf2fbc4d4dfbfc))
* **client:** register MessagingService as an autoconfigured bean ([a447059](https://github.com/madgeek-arc/messaging-system/commit/a4470599053b8359eb7341a9cb4965e694d15340))
* **privacy:** add GDPR user erasure endpoint ([3b6bca9](https://github.com/madgeek-arc/messaging-system/commit/3b6bca9179ecee11c1f1f7f519f75d3d47bf652b))
* **service:** add application.properties ([9b36136](https://github.com/madgeek-arc/messaging-system/commit/9b36136f6ea0dc0ce125922f84891692e6da0f8c))
* **service:** report unhandled errors to Sentry ([100f6b1](https://github.com/madgeek-arc/messaging-system/commit/100f6b10c56f997d5e2bb7c7de27eade37ede490))
* **service:** require a JWT when an issuer is configured ([9cee550](https://github.com/madgeek-arc/messaging-system/commit/9cee55034aed6457874a72837f9b9492beb974b2))

### Bug Fixes

* add autoconfiguration ([a62ff79](https://github.com/madgeek-arc/messaging-system/commit/a62ff7907637beda1157de406dcb57af36218801))
* **client:** raise HTTP errors instead of decoding them as payloads ([1cd5510](https://github.com/madgeek-arc/messaging-system/commit/1cd551062a7468953fa449c40b9ffdd5c37891c9))

## 0.0.1 (2023-10-13)

Initial release.

### Features

* Messaging domain model, DTOs, services and a MongoDB repository for threads, messages and their metadata
* Inbox and outbox searches, plus count methods for both
* Unread message tracking: users who read a message are recorded in its metadata, and unread threads can be queried per user
* Reply support through `replyToMessageId` on messages
* Lookup of threads by id for users who have access to them
* Controller methods defined in the core module and extended by the service and client modules
* Client method for streaming unread threads
* Warning when a required property is missing

### Bug Fixes

* Inbox and outbox searches returning incorrect results
* Creation of unread messages
* Retrieval of a user's unread messages
* Message id not being set correctly
* Client controller bugs
* Controller method fixes

### Refactoring

* Renamed `id` to `threadId`
* Split the project into the `messaging-core`, `messaging-service` and `messaging-client` modules
* Removed authentication from the messaging service
* Removed the email method and the SSE method from the client
* Added `equals` and `hashCode` to domain classes
* Removed the `spring-boot-starter` dependency from the core project

### Build

* Moved to Java 17
* Added distribution management, SCM information and the Maven release plugin
