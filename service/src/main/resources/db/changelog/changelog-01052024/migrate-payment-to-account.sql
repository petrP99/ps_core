--liquibase formatted sql

--changeset pers:payment-account-1
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'payment' AND column_name = 'card_no'
ALTER TABLE payment
    ADD COLUMN account_id          UUID,
    ADD COLUMN payment_destination VARCHAR(255);

UPDATE payment payment_record
SET account_id          = card_record.account_id,
    payment_destination = payment_record.shop_name,
    recipient           = COALESCE(payment_record.recipient, 'MOBILE_PHONE')
FROM card card_record
WHERE payment_record.card_no = card_record.card_number;

ALTER TABLE payment
    ALTER COLUMN account_id SET NOT NULL,
    ALTER COLUMN payment_destination SET NOT NULL,
    ALTER COLUMN recipient SET NOT NULL,
    ADD CONSTRAINT fk_payment_account
        FOREIGN KEY (account_id) REFERENCES account (id),
    DROP COLUMN card_no,
    DROP COLUMN shop_name;

ALTER TABLE payment
    ALTER COLUMN id DROP DEFAULT,
    ALTER COLUMN id TYPE UUID USING gen_random_uuid(),
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

--rollback not required
