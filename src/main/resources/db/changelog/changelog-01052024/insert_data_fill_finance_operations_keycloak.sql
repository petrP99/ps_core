--liquibase formatted sql

--changeset pers:remap-featured-client-keycloak-id-202607
-- Keep the core client id equal to the Keycloak user id and preserve all
-- existing references to the seeded client.
INSERT INTO client (id, first_name, last_name, phone, created_time, status)
SELECT '8855655a-1d79-4bc1-9fef-9b2cf2059603'::UUID,
       c.first_name,
       c.last_name,
       c.phone,
       c.created_time,
       c.status
FROM client c
WHERE c.phone = '89999999999'
  AND c.id <> '8855655a-1d79-4bc1-9fef-9b2cf2059603'::UUID
ORDER BY c.created_time
LIMIT 1
ON CONFLICT (id) DO NOTHING;

UPDATE account
SET client_id = '8855655a-1d79-4bc1-9fef-9b2cf2059603'::UUID
WHERE client_id IN (SELECT id
                    FROM client
                    WHERE phone = '89999999999'
                      AND id <> '8855655a-1d79-4bc1-9fef-9b2cf2059603'::UUID);

UPDATE card
SET client_id = '8855655a-1d79-4bc1-9fef-9b2cf2059603'::UUID
WHERE client_id IN (SELECT id
                    FROM client
                    WHERE phone = '89999999999'
                      AND id <> '8855655a-1d79-4bc1-9fef-9b2cf2059603'::UUID);

UPDATE payment
SET client_id = '8855655a-1d79-4bc1-9fef-9b2cf2059603'::UUID
WHERE client_id IN (SELECT id
                    FROM client
                    WHERE phone = '89999999999'
                      AND id <> '8855655a-1d79-4bc1-9fef-9b2cf2059603'::UUID);

UPDATE replenishment
SET client_id = '8855655a-1d79-4bc1-9fef-9b2cf2059603'::UUID
WHERE client_id IN (SELECT id
                    FROM client
                    WHERE phone = '89999999999'
                      AND id <> '8855655a-1d79-4bc1-9fef-9b2cf2059603'::UUID);

DELETE FROM client
WHERE phone = '89999999999'
  AND id <> '8855655a-1d79-4bc1-9fef-9b2cf2059603'::UUID;

--changeset pers:seed-minimum-client-accounts-202607-keycloak
INSERT INTO account (client_id, balance, currency, name, cashback, status)
SELECT c.id,
       125000.00,
       'RUB',
       'Основной рублёвый',
       0,
       'ACTIVE'
FROM client c
WHERE NOT EXISTS (SELECT 1
                  FROM account a
                  WHERE a.client_id = c.id
                    AND a.currency = 'RUB');

INSERT INTO account (client_id, balance, currency, name, cashback, status)
SELECT c.id,
       2500.00,
       'USD',
       'Валютный USD',
       0,
       'ACTIVE'
FROM client c
WHERE NOT EXISTS (SELECT 1
                  FROM account a
                  WHERE a.client_id = c.id
                    AND a.currency <> 'RUB');

--changeset pers:seed-featured-client-premium-balance-202607-keycloak
UPDATE account
SET balance = GREATEST(balance, 5000000.00)
WHERE id = (SELECT a.id
            FROM account a
                     JOIN client c ON c.id = a.client_id
            WHERE c.phone = '89999999999'
              AND a.currency = 'RUB'
            ORDER BY a.id
            LIMIT 1);

--changeset pers:seed-minimum-account-cards-202607-keycloak
WITH accounts_without_active_card AS (SELECT a.id,
                                             a.client_id,
                                             a.currency
                                      FROM account a
                                      WHERE NOT EXISTS (SELECT 1
                                                        FROM card existing_card
                                                        WHERE existing_card.account_id = a.id
                                                          AND existing_card.status = 'ACTIVE'))
INSERT INTO card (card_number,
                  client_id,
                  account_id,
                  created_date,
                  expire_date,
                  name,
                  currency,
                  status)
SELECT CASE a.currency
           WHEN 'RUB' THEN '220011'
           WHEN 'USD' THEN '440055'
           WHEN 'CNY' THEN '620099'
           ELSE '400000'
           END || lpad(
               ((('x' || substr(md5(a.id::text || ':ACTIVE:payflow-seed'), 1, 15))::bit(60)::bigint
                   % 10000000000))::text,
               10,
               '0'
                  ),
       a.client_id,
       a.id,
       DATE '2026-07-01',
       DATE '2029-07-01',
       'Основная',
       a.currency,
       'ACTIVE'
FROM accounts_without_active_card a
ON CONFLICT (card_number) DO NOTHING;

WITH accounts_without_blocked_card AS (SELECT a.id,
                                              a.client_id,
                                              a.currency
                                       FROM account a
                                       WHERE NOT EXISTS (SELECT 1
                                                         FROM card existing_card
                                                         WHERE existing_card.account_id = a.id
                                                           AND existing_card.status = 'BLOCKED'))
INSERT INTO card (card_number,
                  client_id,
                  account_id,
                  created_date,
                  expire_date,
                  name,
                  currency,
                  status)
SELECT CASE a.currency
           WHEN 'RUB' THEN '220011'
           WHEN 'USD' THEN '440055'
           WHEN 'CNY' THEN '620099'
           ELSE '400000'
           END || lpad(
               ((('x' || substr(md5(a.id::text || ':BLOCKED:payflow-seed'), 1, 15))::bit(60)::bigint
                   % 10000000000))::text,
               10,
               '0'
                  ),
       a.client_id,
       a.id,
       DATE '2026-07-01',
       DATE '2029-07-01',
       'Резервная',
       a.currency,
       'BLOCKED'
FROM accounts_without_blocked_card a
ON CONFLICT (card_number) DO NOTHING;

--changeset pers:seed-featured-client-payments-202607-keycloak
INSERT INTO payment (
    id,
    client_id,
    account_id,
    time_of_pay,
    amount,
    status,
    recipient,
    payment_destination
)
VALUES
    ('71000000-0000-4000-8000-000000000013',
     '8855655a-1d79-4bc1-9fef-9b2cf2059603',
     '660e8400-e29b-41d4-a716-446655440001',
     TIMESTAMP '2026-07-13 09:15:00', 1350.00, 'SUCCESS', 'MOBILE_PHONE', '89995550113'),
    ('71000000-0000-4000-8000-000000000014',
     '8855655a-1d79-4bc1-9fef-9b2cf2059603',
     '660e8400-e29b-41d4-a716-446655440001',
     TIMESTAMP '2026-07-14 18:40:00', 2490.00, 'SUCCESS', 'INTERNET', 'PF-445901'),
    ('71000000-0000-4000-8000-000000000015',
     '8855655a-1d79-4bc1-9fef-9b2cf2059603',
     '660e8400-e29b-41d4-a716-446655440001',
     TIMESTAMP '2026-07-15 11:25:00', 6800.00, 'SUCCESS', 'UTILITIES', 'ЖКХ-771042'),
    ('71000000-0000-4000-8000-000000000016',
     '8855655a-1d79-4bc1-9fef-9b2cf2059603',
     '660e8400-e29b-41d4-a716-446655440001',
     TIMESTAMP '2026-07-16 08:10:00', 890.00, 'FAILED', 'MOBILE_PHONE', '89997770016'),
    ('71000000-0000-4000-8000-000000000017',
     '8855655a-1d79-4bc1-9fef-9b2cf2059603',
     '660e8400-e29b-41d4-a716-446655440001',
     TIMESTAMP '2026-07-17 20:05:00', 1590.00, 'SUCCESS', 'INTERNET', 'PF-771017'),
    ('71000000-0000-4000-8000-000000000018',
     '8855655a-1d79-4bc1-9fef-9b2cf2059603',
     '660e8400-e29b-41d4-a716-446655440001',
     TIMESTAMP '2026-07-18 10:30:00', 4200.00, 'IN_PROGRESS', 'UTILITIES', 'ЖКХ-771018')
ON CONFLICT (id) DO UPDATE
SET client_id = EXCLUDED.client_id,
    account_id = EXCLUDED.account_id,
    time_of_pay = EXCLUDED.time_of_pay,
    amount = EXCLUDED.amount,
    status = EXCLUDED.status,
    recipient = EXCLUDED.recipient,
    payment_destination = EXCLUDED.payment_destination;
