--liquibase formatted sql

--changeset pers:replenishment-account-1
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'replenishment' AND column_name = 'card_no'
ALTER TABLE replenishment
    ADD COLUMN account_id UUID;

UPDATE replenishment replenishment_record
SET account_id = card_record.account_id
FROM card card_record
WHERE replenishment_record.card_no = card_record.card_number;

ALTER TABLE replenishment
    ALTER COLUMN account_id SET NOT NULL,
    ADD CONSTRAINT fk_replenishment_account
        FOREIGN KEY (account_id) REFERENCES account (id),
    DROP COLUMN card_no;

ALTER TABLE replenishment
    ALTER COLUMN id DROP DEFAULT,
    ALTER COLUMN id TYPE UUID USING gen_random_uuid(),
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

--rollback not required
