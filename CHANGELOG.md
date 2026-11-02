## [0.2.0](https://github.com/madgeek-arc/messaging-system/compare/0.1.0...0.2.0) (2026-11-02)

### Features

* optional API key between the client and the service ([fe5f21f](https://github.com/madgeek-arc/messaging-system/commit/fe5f21f4864778546a04ea772e0234db8e529f65))

### Bug Fixes

* **client:** build on the application's WebClient.Builder and apply timeouts ([e2c4b26](https://github.com/madgeek-arc/messaging-system/commit/e2c4b261df571ef071d2cc15f6ebe89e40813b87))
* **service:** append messages and update read receipts atomically ([26dcc45](https://github.com/madgeek-arc/messaging-system/commit/26dcc45a0e5cc88627be854c51deb778779d2858))
* **service:** match search terms literally instead of as regular expressions ([a8b05ee](https://github.com/madgeek-arc/messaging-system/commit/a8b05ee8fbbe45123688392daf57bc6c84c320fb))
* **service:** read inbox and outbox counts as Long ([95efe00](https://github.com/madgeek-arc/messaging-system/commit/95efe001a86963225ad3c9ede684dcbca9a497bb))
* **service:** update the thread named in the path and return 404 when it is missing ([3f25bbd](https://github.com/madgeek-arc/messaging-system/commit/3f25bbd9da19bf81998ecaa18afa8c0c5361b41f))

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
