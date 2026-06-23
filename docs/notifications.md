# Notifications API

Simple notification-sending API built with Spring Boot 4.1 (Java 21), JPA, and PostgreSQL. This document covers usage (Part 1) and the QA test design + analysis (Part 2).

## Architecture

```
controller  -> NotificationController        REST endpoints, validation, HTTP mapping
service     -> NotificationService           orchestration: validate channel, send, persist
provider    -> NotificationProvider          Strategy interface (one impl per channel)
               EmailNotificationProvider
               SmsNotificationProvider
               NotificationProviderFactory   Factory: selects provider by channel
model       -> Notification, Channel         JPA entity + channel enum
repository  -> NotificationRepository        JPA history persistence
dto         -> NotificationRequest/Response, DataResponse
exception   -> GlobalExceptionHandler        consistent 400/500 JSON errors
```

### Design pattern: Strategy + Factory

Each channel has its own `NotificationProvider` implementation (the **Strategy**). Spring injects every provider into `NotificationProviderFactory`, which indexes them by `Channel` and returns the right one at runtime (the **Factory**). Adding a new channel (e.g., push) only requires a new provider component — no existing code changes (open/closed principle).

### Persistence

Each successful sending is stored as a `Notification` row, forming the history returned by `GET /notifications`. Runtime uses PostgreSQL (`application.properties`); automated tests use in-memory H2 (`src/test/resources/application.properties`) so they run with no external database.

## Running

Requires a PostgreSQL instance matching `src/main/resources/application.properties` (`localhost:5432/university`, user `postgres`).

```bash
./mvnw spring-boot:run
```

### Endpoints

#### POST /notifications

Sends a notification and stores it. All fields are required; `channel` must be `email` or `sms`.

Request:
```json
{ "userId": "123", "message": "Hola", "channel": "email" }
```

Response `201 Created`:
```json
{ "id": 1, "userId": "123", "message": "Hola", "channel": "email", "createdAt": "2026-06-23T15:00:00Z" }
```

Validation / invalid channel → `400 Bad Request`:
```json
{ "timestamp": "...", "status": 400, "error": "Bad Request",
  "message": "Invalid channel 'WhatsApp'. Supported channels: email, sms.", "details": [] }
```

#### GET /notifications

Returns the full history, newest first.

Response `200 OK`:
```json
{ "data": [ { "id": 2, "userId": "456", "message": "Hi", "channel": "sms", "createdAt": "..." } ] }
```

### curl examples

```bash
curl -X POST localhost:8080/notifications -H "Content-Type: application/json" \
  -d '{"userId":"123","message":"Hola","channel":"email"}'

curl -X POST localhost:8080/notifications -H "Content-Type: application/json" \
  -d '{"userId":"123","message":"Hola","channel":"whatsapp"}'   # 400

curl localhost:8080/notifications
```

---

## Part 2: QA + Automation

### Test case design

#### POST /notifications

| #  | Case                                | Input                                | Expected                             |
|----|-------------------------------------|--------------------------------------|--------------------------------------|
| 1  | Valid email                         | `{userId, message, channel:"email"}` | 201, body persisted, channel `email` |
| 2  | Valid sms                           | `{userId, message, channel:"sms"}`   | 201, channel `sms`                   |
| 3  | Channel case-insensitive            | `channel:"EMAIL"`                    | 201 (normalized to `email`)          |
| 4  | Invalid channel                     | `channel:"whatsapp"`                 | 400, supported-channels message      |
| 5  | Missing message                     | `{userId, channel}`                  | 400, validation error                |
| 6  | Missing userId                      | `{message, channel}`                 | 400                                  |
| 7  | Missing channel                     | `{userId, message}`                  | 400                                  |
| 8  | Blank fields                        | `{"":"","":""}`                      | 400                                  |
| 9  | Malformed JSON / wrong content-type | invalid body                         | 400                                  |
| 10 | Persistence side effect             | valid POST then GET                  | record appears in history            |

#### GET /notifications

| # | Case           | Precondition             | Expected                              |
|---|----------------|--------------------------|---------------------------------------|
| 1 | Empty history  | no sends                 | 200, `{"data": []}`                   |
| 2 | Returns sends  | 2 prior POSTs            | 200, `data` has 2 items               |
| 3 | Ordering       | sends at different times | newest first                          |
| 4 | Response shape | any                      | items have `userId, message, channel` |

### Automated tests

Implemented with JUnit 5 + Spring MockMvc against H2 — run with `./mvnw test`.

- `NotificationControllerTest` ($5$ cases): valid email POST, valid sms POST, an invalid channel → 400, missing field → 400, GET round-trip returns created notifications.
- `NotificationProviderFactoryTest` ($3$ cases): correct provider per channel, error when no provider is registered.

### Analysis

**What would you automate first, and why?**
The `POST /notifications` write path: required-field validation, channel validation, provider selection, and persistence. It concentrates all the business rules, is the highest risk (bad data or a wrong provider has real consequences), and it is the precondition for `GET` — automating it first protects the core behavior and enables the GET round-trip tests.

**What risks do you identify in this API?**
- **No authentication/authorization** — anyone can send notifications as any `userId`.
- **No rate limiting / abuse protection** — open to spam and cost blow-ups on real channels.
- **No real delivery guarantees** — providers are fire-and-forget, with no retries, dead-letter handling, or delivery-status tracking.
- **Unbounded input** — `message` length and payload size are only loosely constrained.
- **Unbounded history growth** — `GET` returns the entire table with no pagination.
- **No idempotency** — a retried POST creates duplicate sends.
- **Plaintext DB credentials** in `application.properties` — should come from secrets/env.
- **Channel coupling** — only `email`/`sms`; new channels require a (small) code change + deploy.
