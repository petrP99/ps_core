--liquibase formatted sql

--changeset pers:transfer-exchange-1
ALTER TABLE transfer
    ADD COLUMN IF NOT EXISTS exchange_rate NUMERIC(19, 6),
    ADD COLUMN IF NOT EXISTS commission    NUMERIC(19, 2),
    ADD COLUMN IF NOT EXISTS debit_amount  NUMERIC(19, 2);

--rollback ALTER TABLE transfer DROP COLUMN exchange_rate, DROP COLUMN commission, DROP COLUMN debit_amount;
