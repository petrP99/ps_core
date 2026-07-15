--liquibase formatted sql

--changeset pers:add-account-created-at
ALTER TABLE account
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT now();

--changeset pers:add-replenishment-source
ALTER TABLE replenishment
    ADD COLUMN IF NOT EXISTS external_operation_id UUID,
    ADD COLUMN IF NOT EXISTS source_type VARCHAR(40) NOT NULL DEFAULT 'MANUAL',
    ADD COLUMN IF NOT EXISTS description VARCHAR(255);

CREATE UNIQUE INDEX IF NOT EXISTS uq_replenishment_external_operation_id
    ON replenishment (external_operation_id)
    WHERE external_operation_id IS NOT NULL;
