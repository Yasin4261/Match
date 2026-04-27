# Match API

Tinder-benzeri sosyal platformun arka uç API'si — **Hexagonal (Ports & Adapters)** mimarisiyle, Spring Boot 3 + Java 21 üzerinde, tamamen Docker üzerinde çalışacak şekilde tasarlandı.

## Özellikler
- Kayıt / giriş (JWT access + refresh)
- Profil + foto (MinIO) iskelet hazır
- PostGIS ile coğrafi keşif (`/api/v1/discovery`)
- Swipe + karşılıklı beğenide otomatik `Match`
- 1:1 mesajlaşma — STOMP üzerinden (`/ws/chat`)
- **Sesli + görüntülü canlı görüşme** — WebRTC + coturn TURN/STUN, signaling kanalı `/ws/signaling`
- Presence (Redis), e-posta (Mailhog dev), OpenAPI/Swagger UI

## Mimari

```
com.match
├── bootstrap/   → SpringBootApplication, config (Security, WebSocket, MinIO, OpenAPI)
├── domain/      → Saf Java: entity / VO / port arayüzleri (framework-bağımsız)
│   └── port/{in,out}
├── application/ → Use case servisleri (port'ları kullanır)
└── adapter/
    ├── rest/         → HTTP controller, JWT filter, error handler
    ├── ws/           → STOMP chat + raw WebRTC signaling
    ├── persistence/  → JPA entity + Spring Data + port adapter
    ├── cache/redis/  → presence
    ├── storage/minio/→ photo upload (presigned URL)
    └── mail/         → SMTP (Mailhog)
```

Mimari kuralları `HexagonalArchitectureTest` (ArchUnit) ile derleme/test zamanında zorlanır:
`domain → adapter` veya `domain → org.springframework` bağımlılığı yasaktır.

## Hızlı Başlangıç (Docker)

```bash
cp .env.example .env
docker compose up -d --build
```

Servisler:

| Servis     | URL/Port |
|------------|----------|
| API        | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Postgres   | localhost:5432 (PostGIS) |
| Adminer    | http://localhost:8081 |
| Redis      | localhost:6379 |
| MinIO      | http://localhost:9000 (console: 9001) |
| Mailhog    | http://localhost:8025 |
| coturn     | UDP/TCP 3478 (host network — Linux) |

## API Akışı (örnek)

1. **Register**: `POST /api/v1/auth/register` → `{accessToken, refreshToken}`
2. **Discover**: `GET /api/v1/discovery?lat=...&lng=...&radiusKm=50` (Bearer)
3. **Swipe**: `POST /api/v1/swipes {swipeeId, direction:"LIKE"}`
   - Karşılıklı LIKE varsa response: `{matched:true, matchId:"..."}`
4. **Chat (STOMP)**: `ws://localhost:8080/ws/chat`
   - Subscribe: `/topic/conv.{conversationId}`
   - Send: `/app/chat.send.{conversationId}` payload `{"body":"..."}`
5. **TURN credentials**: `GET /api/v1/calls/turn-credentials`
6. **Call init**: `POST /api/v1/calls {calleeId, type:"VIDEO"}` → `callId`
7. **WebRTC signaling**: `ws://localhost:8080/ws/signaling?token=<JWT>`
   - JSON mesajlar: `{type:"offer|answer|ice-candidate|call-invite|call-accept|call-end", to:"<userId>", callId:"...", payload:{...}}`
   - Sunucu sadece **relay** eder; medya P2P/TURN üzerinden akar.
8. **Call end**: `POST /api/v1/calls/{id}/end {reason:"hangup"}`

## Geliştirme (yerel JVM)

```bash
docker compose up -d postgres redis minio minio-init mailhog coturn

SPRING_PROFILES_ACTIVE=dev \
POSTGRES_HOST=localhost REDIS_HOST=localhost MINIO_ENDPOINT=http://localhost:9000 \
JWT_SECRET=please-change-this-to-a-32-char-or-longer-secret-string \
./mvnw spring-boot:run
```

## Test

```bash
./mvnw test
```

## Veritabanı

PostgreSQL 16 + PostGIS 3.4. Şema yönetimi **Flyway** ile (`src/main/resources/db/migration/V1__init.sql`).

Tablolar: `users`, `profiles` (geography Point), `preferences`, `photos`, `swipes`, `matches`, `conversations`, `messages`, `call_sessions`, `reports`, `blocks`, `notifications`.

## WebRTC Notları

- Tarayıcılar TURN üzerinden NAT geçer; coturn `static-auth-secret` ile time-limited credential üretir.
- `/api/v1/calls/turn-credentials` endpoint'i `username = "<unix-expiry>:<userId>"`, `password = HMAC-SHA1(secret, username)` döner.
- `coturn` Linux'ta `network_mode: host` ile çalışır. Docker Desktop'ta (Mac/Win) bu satırı kaldırıp UDP port aralığını (`49160-49200/udp`) expose et.

## Sonraki Adımlar
- Profile/Photo upload controller (presigned URL akışı)
- Mesaj geçmişi REST endpoint'leri
- Block / report use case'leri + discovery filtresi
- FCM/APNs push bildirim adapter'ı
- Çoklu instance için Redis pub/sub signaling köprüsü
- Testcontainers ile entegrasyon testleri (Postgres+Redis+MinIO)

