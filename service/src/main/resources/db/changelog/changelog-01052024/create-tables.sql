--liquibase formatted sql


--changeset pers:2
create TABLE IF NOT EXISTS client
(
    id           BIGSERIAL PRIMARY KEY,
    first_name   VARCHAR(128) NOT NULL,
    last_name    VARCHAR(128) NOT NULL,
    phone        VARCHAR(20) UNIQUE NOT NULL,
    balance      NUMERIC(10, 2) DEFAULT '0',
    created_time TIMESTAMP NOT NULL,
    role         VARCHAR(20) NOT NULL,
    status       VARCHAR(56) NOT NULL
);
--rollback DROP TABLE client;

--changeset pers:3

CREATE SEQUENCE IF NOT EXISTS card_id_seq RESTART WITH 197456 INCREMENT BY 1909;
create TABLE IF NOT EXISTS card
(
    id BIGINT DEFAULT nextval('card_id_seq') PRIMARY KEY,
    client_id    BIGINT REFERENCES client (id) ON DELETE CASCADE NOT NULL,
    balance      NUMERIC(10, 2)                                    DEFAULT '0',
    created_date DATE                                               NOT NULL,
    expire_date  DATE                                               NOT NULL,
    name         VARCHAR(50),
    currency     VARCHAR(10)                                        NOT NULL,
    status       VARCHAR(56)                                        NOT NULL
);

--rollback DROP TABLE card;

--changeset pers:4
create TABLE IF NOT EXISTS payment
(
    id               BIGSERIAL PRIMARY KEY,
    shop_name        VARCHAR(128)                                       NOT NULL,
    amount           NUMERIC(10, 2)                                     NOT NULL,
    pay_by_client_id BIGINT REFERENCES client (id) ON DELETE CASCADE NOT NULL,
    pay_by_card_no   BIGINT REFERENCES card (id)                        NOT NULL,
    time_of_pay      TIMESTAMP                                          NOT NULL,
    status           VARCHAR(20)                                        NOT NULL
);
--rollback DROP TABLE payment;

--changeset pers:5
create TABLE IF NOT EXISTS transfer
(
    id               BIGSERIAL PRIMARY KEY,
    client_id        BIGINT REFERENCES client (id) NOT NULL,
    card_no_from     BIGINT REFERENCES card (id) NOT NULL,
    card_no_to       BIGINT REFERENCES card (id) NOT NULL,
    amount           NUMERIC(10, 2)              NOT NULL,
    recipient        VARCHAR(120)                NOT NULL,
    message          VARCHAR(120),
    time_of_transfer TIMESTAMP                   NOT NULL,
    status           VARCHAR(20)                 NOT NULL
);
--rollback DROP TABLE transfer;

--changeset pers:6
create TABLE IF NOT EXISTS replenishment
(
    id                    BIGSERIAL PRIMARY KEY,
    client_to             BIGINT REFERENCES client (id) ON DELETE CASCADE NOT NULL,
    card_no_to            BIGINT REFERENCES card (id)                        NOT NULL,
    amount                NUMERIC(10, 2)                                     NOT NULL,
    time_of_replenishment TIMESTAMP                                          NOT NULL,
    status                VARCHAR(20)                                        NOT NULL
);
--rollback DROP TABLE replenishment;

ALTER SEQUENCE client_id_seq RESTART WITH 99999;




