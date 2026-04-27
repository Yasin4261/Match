-- V1: Initial schema for Match API (PostgreSQL + PostGIS)

CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id              UUID PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    status          VARCHAR(32)  NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL
);

CREATE TABLE profiles (
    user_id         UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    display_name    VARCHAR(120) NOT NULL,
    birth_date      DATE,
    gender          VARCHAR(32),
    bio             VARCHAR(1000),
    location        geography(Point, 4326),
    last_active_at  TIMESTAMPTZ
);
CREATE INDEX idx_profiles_location ON profiles USING GIST (location);
CREATE INDEX idx_profiles_gender_birthdate ON profiles (gender, birth_date);

CREATE TABLE preferences (
    user_id          UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    interested_in    VARCHAR(32),
    age_min          INT,
    age_max          INT,
    max_distance_km  INT
);

CREATE TABLE photos (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    object_key  VARCHAR(255) NOT NULL,
    position    INT  NOT NULL DEFAULT 0,
    is_primary  BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_photos_user_position ON photos (user_id, position);

CREATE TABLE swipes (
    id          UUID PRIMARY KEY,
    swiper_id   UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    swipee_id   UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    direction   VARCHAR(16) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_swipes_pair UNIQUE (swiper_id, swipee_id)
);
CREATE INDEX idx_swipes_swipee_dir ON swipes (swipee_id, direction);

CREATE TABLE matches (
    id           UUID PRIMARY KEY,
    user_a_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user_b_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at   TIMESTAMPTZ NOT NULL,
    unmatched_at TIMESTAMPTZ,
    CONSTRAINT uq_match_pair UNIQUE (user_a_id, user_b_id)
    -- Note: uniqueness is enforced; the application normalizes the pair so that
    -- (user_a_id, user_b_id) always matches Java's UUID.compareTo ordering.
);

CREATE TABLE conversations (
    id              UUID PRIMARY KEY,
    match_id        UUID NOT NULL UNIQUE REFERENCES matches(id) ON DELETE CASCADE,
    last_message_at TIMESTAMPTZ
);
CREATE INDEX idx_conv_last_msg ON conversations (last_message_at DESC);

CREATE TABLE messages (
    id              UUID PRIMARY KEY,
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    body            VARCHAR(4000) NOT NULL,
    type            VARCHAR(16)   NOT NULL,
    status          VARCHAR(16)   NOT NULL,
    created_at      TIMESTAMPTZ   NOT NULL
);
CREATE INDEX idx_messages_conv_created ON messages (conversation_id, created_at DESC);

CREATE TABLE call_sessions (
    id           UUID PRIMARY KEY,
    caller_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    callee_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type         VARCHAR(16) NOT NULL,
    state        VARCHAR(16) NOT NULL,
    started_at   TIMESTAMPTZ NOT NULL,
    answered_at  TIMESTAMPTZ,
    ended_at     TIMESTAMPTZ,
    end_reason   VARCHAR(64)
);
CREATE INDEX idx_call_caller ON call_sessions (caller_id);
CREATE INDEX idx_call_callee ON call_sessions (callee_id);
CREATE INDEX idx_call_state  ON call_sessions (state);

CREATE TABLE reports (
    id           UUID PRIMARY KEY,
    reporter_id  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reported_id  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reason       VARCHAR(64) NOT NULL,
    details      VARCHAR(2000),
    status       VARCHAR(32) NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_reports_reported ON reports (reported_id);

CREATE TABLE blocks (
    blocker_id  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    blocked_id  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (blocker_id, blocked_id)
);

CREATE TABLE notifications (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type        VARCHAR(64) NOT NULL,
    payload     JSONB        NOT NULL,
    read_at     TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_notif_user_read ON notifications (user_id, read_at);

