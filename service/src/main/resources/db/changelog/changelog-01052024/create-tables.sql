--liquibase formatted sql

--changeset pers:create-tables
CREATE TABLE client
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name   VARCHAR(128) NOT NULL,
    last_name    VARCHAR(128) NOT NULL,
    phone        VARCHAR(20)  NOT NULL,
    created_time TIMESTAMP    NOT NULL,
    status       VARCHAR(56)  NOT NULL
);

CREATE TABLE account
(
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID           NOT NULL REFERENCES client (id),
    balance   NUMERIC(19, 2) NOT NULL DEFAULT 0,
    currency  VARCHAR(10)    NOT NULL,
    name      VARCHAR(50)    NOT NULL DEFAULT 'Счет',
    cashback  INTEGER                 DEFAULT 0,
    status    VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE card
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    card_number  VARCHAR(16) UNIQUE NOT NULL,
    client_id    UUID               NOT NULL REFERENCES client (id),
    account_id   UUID               NOT NULL REFERENCES account (id),
    created_date DATE               NOT NULL,
    expire_date  DATE               NOT NULL,
    name         VARCHAR(50),
    currency     VARCHAR(10)        NOT NULL,
    status       VARCHAR(56)        NOT NULL
);

CREATE TABLE payment
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id           UUID           NOT NULL REFERENCES client (id),
    account_id          UUID           NOT NULL REFERENCES account (id),
    time_of_pay         TIMESTAMP      NOT NULL,
    amount              NUMERIC(19, 2) NOT NULL,
    status              VARCHAR(20)    NOT NULL,
    recipient           VARCHAR(30)    NOT NULL,
    payment_destination VARCHAR(255)   NOT NULL
);

CREATE TABLE replenishment
(
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id             UUID           NOT NULL REFERENCES client (id),
    account_id            UUID           NOT NULL REFERENCES account (id),
    amount                NUMERIC(19, 2) NOT NULL,
    time_of_replenishment TIMESTAMP      NOT NULL,
    status                VARCHAR(20)    NOT NULL
);

CREATE TABLE exchange_rate
(
    currency_code VARCHAR(3) PRIMARY KEY,
    rate          NUMERIC(19, 2) NOT NULL,
    updated_at    DATE           NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE outbox_event
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_id    UUID         NOT NULL,
    event_type      VARCHAR(50)  NOT NULL,
    event_key       VARCHAR(255) NOT NULL,
    payload         TEXT         NOT NULL,
    status          VARCHAR(20)  NOT NULL,
    attempts        INTEGER      NOT NULL DEFAULT 0,
    created_at      TIMESTAMP    NOT NULL,
    next_attempt_at TIMESTAMP    NOT NULL,
    published_at    TIMESTAMP,
    last_error      VARCHAR(1000)
);

CREATE INDEX idx_outbox_event_pending
    ON outbox_event (status, next_attempt_at, created_at);
CREATE INDEX idx_outbox_event_aggregate
    ON outbox_event (aggregate_id);

CREATE TABLE processed_balance_operation
(
    operation_id UUID PRIMARY KEY,
    successful   BOOLEAN   NOT NULL,
    failure_code VARCHAR(80),
    processed_at TIMESTAMP NOT NULL
);

--rollback DROP TABLE processed_balance_operation; DROP TABLE outbox_event; DROP TABLE exchange_rate; DROP TABLE replenishment; DROP TABLE payment; DROP TABLE card; DROP TABLE account; DROP TABLE client;
