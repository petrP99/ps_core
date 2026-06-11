--liquibase formatted sql

--changeset pers:account-transfer-1
CREATE TABLE IF NOT EXISTS account_transfer
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id         UUID           NOT NULL REFERENCES client (id),
    account_from      UUID           NOT NULL REFERENCES account (id),
    account_from_name VARCHAR(255)   NOT NULL,
    account_to        UUID           NOT NULL REFERENCES account (id),
    account_to_name   VARCHAR(255)   NOT NULL,
    amount            NUMERIC(19, 2) NOT NULL,
    amount_to         NUMERIC(19, 2) NOT NULL,
    exchange_rate     NUMERIC(19, 6) NOT NULL,
    currency          VARCHAR(10)    NOT NULL,
    target_currency   VARCHAR(10)    NOT NULL,
    time_of_transfer  TIMESTAMP      NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_account_transfer_client_time
    ON account_transfer (client_id, time_of_transfer DESC);

--rollback DROP TABLE account_transfer;
