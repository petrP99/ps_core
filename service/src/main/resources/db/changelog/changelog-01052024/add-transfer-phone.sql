--liquibase formatted sql

--changeset pers:transfer-phone-1
ALTER TABLE transfer
    ADD COLUMN IF NOT EXISTS recipient_phone VARCHAR(11);

--rollback ALTER TABLE transfer DROP COLUMN recipient_phone;
