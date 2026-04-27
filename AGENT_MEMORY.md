# Agent Memory

This file stores stable, project-specific working context for future turns.

## User Preferences
- Language: Turkish preferred.
- Wants practical, direct commands and quick resolution.
- Architecture: **Hexagonal (Ports & Adapters)** strictly enforced (ArchUnit tests).
- Stack: Spring Boot 3.3.5, Java 21, PostgreSQL+PostGIS, Redis, MinIO, coturn.
- Runtime: Docker Compose for full stack.

## Workspace Facts
- Project root: `/home/yasin/Downloads/api`
- Git remote: `origin = git@github.com:Yasin4261/Match.git`, branch `main`
- Build: Maven (`./mvnw`), single module, package-based hexagonal layout under `com.match.*`
- Main packages: `bootstrap`, `domain`, `domain.port.{in,out}`, `application`, `adapter.{rest,ws,persistence,cache,storage,mail}`
- Migrations: Flyway, `src/main/resources/db/migration/V1__init.sql` (PostGIS + auth/profile/swipe/match/chat/call/report tables)
- Compose services: api, postgres(PostGIS), redis, minio(+init), mailhog, adminer, coturn (host network on Linux)
- Env file template: `.env.example` (copied to `.env`)

## Key Endpoints
- REST: `/api/v1/auth/{register,login,refresh}`, `/api/v1/swipes`, `/api/v1/discovery`, `/api/v1/calls/...`
- STOMP chat: `/ws/chat`  (sub `/topic/conv.{id}`, send `/app/chat.send.{id}`)
- Raw WS signaling: `/ws/signaling?token=<JWT>` (offer/answer/ice/call-* relay)
- Swagger UI: `/swagger-ui.html`

## Verified Build Status
- `./mvnw clean compile` → BUILD SUCCESS (75 sources)
- `DomainSmokeTest` + `HexagonalArchitectureTest` → 6/6 PASSED
- `docker compose config` → syntax OK

## Operational Defaults
- Pre-Git checks: `git status --short --branch`, `git remote -v`, `git branch -vv`.
- Prefer conflict-safe merge flow; avoid destructive reset unless explicitly requested.
- For full integration tests, prefer Testcontainers (already on classpath).

## Pending Next Steps
- Profile/Photo upload controller (presigned URL).
- Conversation/message history REST endpoints.
- Block/report use cases + discovery filter wiring.
- Push notifications (FCM/APNs) adapter.
- Multi-instance signaling via Redis pub/sub.

## Test Strategy Notes
- Unit tests use **Mockito strict stubbing**; for shared stubs across tests use `lenient()`.
- Integration tests:
  - Base class: `com.match.integration.AbstractPostgresIT` (singleton PostGIS container, mocks Redis+MinIO, supplies no-op `JavaMailSenderImpl`).
  - Discoverable test app: `com.match.IntegrationTestApplication` (excludes prod `ApiApplication` from scan + excludes Mail autoconfig).
  - Naming: `*IT.java` → Failsafe (`mvn verify`); `*Test.java` → Surefire (`mvn test`).
  - Test profile config: `src/test/resources/application-test.yml`.
- ArchUnit enforces hexagonal rules (`HexagonalArchitectureTest`).

## Bugs Found & Fixed via Tests
- `AuthService.register` now lowercases email before `existsByEmail` (prevents duplicate via case).
- `SecurityConfig` returns **401** for unauthenticated requests (was 403) via `HttpStatusEntryPoint`.
- Removed `CHECK (user_a_id < user_b_id)` from `matches` (Java/PG UUID ordering mismatch); uniqueness still guarded by `uq_match_pair`.

## Session Note
- Memory file initialized and activated for subsequent assistance.
