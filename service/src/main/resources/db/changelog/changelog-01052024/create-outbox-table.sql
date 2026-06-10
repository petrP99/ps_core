--liquibase formatted sql

--changeset pers:outbox-1
CREATE TABLE IF NOT EXISTS outbox_event
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_id    UUID          NOT NULL,
    event_type      VARCHAR(50)   NOT NULL,
    event_key       VARCHAR(255)  NOT NULL,
    payload         TEXT          NOT NULL,
    status          VARCHAR(20)   NOT NULL,
    attempts        INTEGER       NOT NULL DEFAULT 0,
    created_at      TIMESTAMP     NOT NULL,
    next_attempt_at TIMESTAMP     NOT NULL,
    published_at    TIMESTAMP,
    last_error      VARCHAR(1000)
);

CREATE INDEX IF NOT EXISTS idx_outbox_event_pending
    ON outbox_event (status, next_attempt_at, created_at);

CREATE INDEX IF NOT EXISTS idx_outbox_event_aggregate
    ON outbox_event (aggregate_id);

--rollback DROP TABLE outbox_event;
