--liquibase formatted sql

--changeset pers:remove-client-role
ALTER TABLE client
    DROP COLUMN IF EXISTS role;

--rollback ALTER TABLE client ADD COLUMN role VARCHAR(20);
