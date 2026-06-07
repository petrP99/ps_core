--liquibase formatted sql

--changeset pers:2
CREATE TABLE IF NOT EXISTS client
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name   VARCHAR(128) NOT NULL,
    last_name    VARCHAR(128) NOT NULL,
    phone        VARCHAR(20)  NOT NULL,
    created_time TIMESTAMP    NOT NULL,
    role         VARCHAR(20)  NOT NULL,
    status       VARCHAR(56)  NOT NULL
);
--rollback DROP TABLE client;

--changeset pers:3
CREATE TABLE IF NOT EXISTS account
(
    id        UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    client_id UUID        NOT NULL REFERENCES client (id),
    balance   NUMERIC(19, 2)       DEFAULT 0 NOT NULL,
    currency  VARCHAR(10) NOT NULL,
    name      VARCHAR(50) NOT NULL DEFAULT 'Счет',
    cashback  INTEGER              DEFAULT 0
);
--rollback DROP TABLE account;

--changeset pers:4
CREATE TABLE IF NOT EXISTS card
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
--rollback DROP TABLE card;

--changeset pers:5
CREATE TABLE IF NOT EXISTS payment
(
    id          BIGSERIAL PRIMARY KEY,
    client_id   UUID           NOT NULL REFERENCES client (id),
    card_no     VARCHAR(16)    NOT NULL REFERENCES card (card_number),
    time_of_pay TIMESTAMP      NOT NULL,
    amount      NUMERIC(19, 2) NOT NULL,
    status      VARCHAR(20)    NOT NULL,
    recipient   VARCHAR(255),
    shop_name   VARCHAR(255)   NOT NULL
);
--rollback DROP TABLE payment;

--changeset pers:6
CREATE TABLE IF NOT EXISTS transfer
(
    id               BIGSERIAL PRIMARY KEY,
    from_client_id   UUID           NOT NULL REFERENCES client (id),
    to_client_id     UUID           NOT NULL REFERENCES client (id),
    card_from        VARCHAR(16)    NOT NULL REFERENCES card (card_number),
    card_to          VARCHAR(16)    NOT NULL REFERENCES card (card_number),
    amount           NUMERIC(19, 2) NOT NULL,
    amount_to        NUMERIC(19, 2),
    currency         VARCHAR(10),
    target_currency  VARCHAR(10),
    time_of_transfer TIMESTAMP      NOT NULL,
    recipient        VARCHAR(120)   NOT NULL,
    message          VARCHAR(120),
    status           VARCHAR(20)    NOT NULL
);
--rollback DROP TABLE transfer;

--changeset pers:7
CREATE TABLE IF NOT EXISTS replenishment
(
    id                    BIGSERIAL PRIMARY KEY,
    client_id             UUID           NOT NULL REFERENCES client (id),
    card_no               VARCHAR(16)    NOT NULL REFERENCES card (card_number),
    amount                NUMERIC(19, 2) NOT NULL,
    time_of_replenishment TIMESTAMP      NOT NULL,
    status                VARCHAR(20)    NOT NULL
);
--rollback DROP TABLE replenishment;

--changeset pers:8
CREATE TABLE IF NOT EXISTS exchange_rate
(
    currency_code VARCHAR(3) PRIMARY KEY,
    rate          NUMERIC(19, 2) NOT NULL,
    updated_at    DATE           NOT NULL DEFAULT CURRENT_DATE
);
--rollback DROP TABLE exchange_rate;
