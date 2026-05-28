INSERT INTO users (id, login, password, role)
VALUES
    (1111, '1@superadmin.ru', '{bcrypt}$2a$10$dZ156dLkqbXN/V.XWvQNwOxLGukLojzipn8NxG9xYK8g.x5o2WAiK', 'SUPER_ADMIN'),
    (1112, '1@admin.ru', '{bcrypt}$2a$10$dZ156dLkqbXN/V.XWvQNwOxLGukLojzipn8NxG9xYK8g.x5o2WAiK', 'ADMIN'),
    (1113, '1@ps.ru', '{bcrypt}$2a$10$dZ156dLkqbXN/V.XWvQNwOxLGukLojzipn8NxG9xYK8g.x5o2WAiK', 'USER'),
    (1114, '2@ps.ru', '{bcrypt}$2a$10$dZ156dLkqbXN/V.XWvQNwOxLGukLojzipn8NxG9xYK8g.x5o2WAiK', 'USER'),
    (1115, '3@ps.ru', '{bcrypt}$2a$10$dZ156dLkqbXN/V.XWvQNwOxLGukLojzipn8NxG9xYK8g.x5o2WAiK', 'USER'),
    (1116, '4@ps.ru', '{bcrypt}$2a$10$dZ156dLkqbXN/V.XWvQNwOxLGukLojzipn8NxG9xYK8g.x5o2WAiK', 'USER'),
    (1117, '5@ps.ru', '{bcrypt}$2a$10$dZ156dLkqbXN/V.XWvQNwOxLGukLojzipn8NxG9xYK8g.x5o2WAiK', 'USER'),
    (1118, '6@ps.ru', '{bcrypt}$2a$10$dZ156dLkqbXN/V.XWvQNwOxLGukLojzipn8NxG9xYK8g.x5o2WAiK', 'USER'),
    (1119, '7@ps.ru', '{bcrypt}$2a$10$dZ156dLkqbXN/V.XWvQNwOxLGukLojzipn8NxG9xYK8g.x5o2WAiK', 'USER'),
    (11110, '8@ps.ru', '{bcrypt}$2a$10$dZ156dLkqbXN/V.XWvQNwOxLGukLojzipn8NxG9xYK8g.x5o2WAiK', 'USER'),
    (11111, '9@ps.ru', '{bcrypt}$2a$10$dZ156dLkqbXN/V.XWvQNwOxLGukLojzipn8NxG9xYK8g.x5o2WAiK', 'USER'),
    (11112, '10@ps.ru', '{bcrypt}$2a$10$dZ156dLkqbXN/V.XWvQNwOxLGukLojzipn8NxG9xYK8g.x5o2WAiK', 'USER');

INSERT INTO client (id, user_id, first_name, last_name, phone, balance, created_time, status)
VALUES
    (1, 1113, 'Ivan', 'Petrov', '+79161234567', 5420.75, '2025-01-15 10:30:00', 'ACTIVE'),
    (2, 1114, 'Maria', 'Sidorova', '+79162345678', 8730.50, '2025-02-20 14:15:00', 'ACTIVE'),
    (3, 1115, 'Alexey', 'Ivanov', '+79163456789', 3250.00, '2025-03-10 09:45:00', 'INACTIVE'),
    (4, 1116, 'Elena', 'Smirnova', '+79164567890', 12980.25, '2025-01-05 16:20:00', 'ACTIVE'),
    (5, 1117, 'Dmitry', 'Kozlov', '+79165678901', 6750.80, '2025-04-12 11:10:00', 'ACTIVE'),
    (6, 1118, 'Olga', 'Popova', '+79166789012', 4450.00, '2025-02-28 13:55:00', 'ACTIVE'),
    (7, 1119, 'Sergey', 'Sokolov', '+79167890123', 8920.45, '2025-03-25 08:30:00', 'ACTIVE'),
    (8, 11110, 'Anna', 'Lebedeva', '+79168901234', 2150.90, '2025-01-20 17:40:00', 'ACTIVE'),
    (9, 11111, 'Pavel', 'Morozov', '+79169012345', 15670.00, '2025-04-05 12:25:00', 'INACTIVE'),
    (10, 11112, 'Natalia', 'Volkova', '+79160123456', 7830.60, '2025-02-15 15:05:00', 'ACTIVE');

INSERT INTO card (id, client_id, balance, created_date, expire_date, status)
VALUES
    (100001, 1, 2150.50, '2025-01-16', '2028-01-16', 'BLOCKED'),
    (100002, 1, 3270.25, '2025-02-10', '2029-02-10', 'ACTIVE'),
    -- Client 2 (Maria)
    (100003, 2, 8730.50, '2025-02-21', '2028-02-21', 'ACTIVE'),
    (100004, 2, 1540.75, '2024-12-01', '2027-12-01', 'EXPIRED'),
    -- Client 3 (Alexey)
    (100005, 3, 1800.00, '2025-03-11', '2028-03-11', 'ACTIVE'),
    (100006, 3, 1450.00, '2025-03-11', '2029-03-11', 'EXPIRED'),
    -- Client 4 (Elena)
    (100007, 4, 5200.00, '2025-01-06', '2028-01-06', 'ACTIVE'),
    (100008, 4, 7780.25, '2025-01-06', '2027-01-06', 'ACTIVE'),
    -- Client 5 (Dmitry)
    (100009, 5, 4120.80, '2025-04-13', '2028-04-13', 'BLOCKED'),
    (100010, 5, 2630.00, '2025-04-13', '2029-04-13', 'EXPIRED'),
    -- Client 6 (Olga)
    (100011, 6, 2890.00, '2025-03-01', '2028-03-01', 'ACTIVE'),
    (100012, 6, 1560.00, '2025-03-01', '2027-03-01', 'ACTIVE'),
    -- Client 7 (Sergey)
    (100013, 7, 5670.45, '2025-03-26', '2028-03-26', 'ACTIVE'),
    (100014, 7, 3250.00, '2025-03-26', '2029-03-26', 'ACTIVE'),
    -- Client 8 (Anna)
    (100015, 8, 980.90, '2025-01-21', '2028-01-21', 'EXPIRED'),
    (100016, 8, 1170.00, '2025-01-21', '2027-01-21', 'ACTIVE'),
    -- Client 9 (Pavel)
    (100017, 9, 8950.00, '2025-04-06', '2028-04-06', 'BLOCKED'),
    (100018, 9, 6720.00, '2025-04-06', '2029-04-06', 'ACTIVE'),
    -- Client 10 (Natalia)
    (100019, 10, 4310.60, '2025-02-16', '2028-02-16', 'BLOCKED'),
    (100020, 10, 3520.00, '2025-02-16', '2027-02-16', 'ACTIVE');

INSERT INTO payment (shop_name, amount, pay_by_client_id, pay_by_card_no, time_of_pay, status)
VALUES
    -- Client 1 payments (Ivan - 4 payments)
    ('MTS', 450.00, 1, 100001, '2025-02-05 11:20:00', 'SUCCESS'),
    ('Netflix', 799.00, 1, 100002, '2025-03-12 18:45:00', 'FAILED'),
    ('Skillbox', 12500.00, 1, 100001, '2025-04-01 09:15:00', 'FAILED'),
    ('Ozon', 2340.50, 1, 100002, '2025-05-20 14:30:00', 'SUCCESS'),
    -- Client 2 payments (Maria - 4 payments)
    ('Beeline', 380.00, 2, 100003, '2025-03-15 10:10:00', 'SUCCESS'),
    ('Yandex Music', 399.00, 2, 100004, '2025-04-22 16:25:00', 'IN_PROGRESS'),
    ('Udemy', 4500.00, 2, 100003, '2025-01-10 12:00:00', 'SUCCESS'),
    ('Wildberries', 1850.75, 2, 100004, '2025-06-05 17:40:00', 'SUCCESS'),
    -- Client 3 (Alexey)
    ('Tele2', 290.00, 3, 100005, '2025-04-18 08:50:00', 'FAILED'),
    ('VK Combo', 499.00, 3, 100006, '2025-05-03 19:15:00', 'SUCCESS'),
    ('GeekBrains', 8900.00, 3, 100005, '2025-02-28 11:30:00', 'SUCCESS'),
    ('Avito', 1250.00, 3, 100006, '2025-06-12 13:20:00', 'SUCCESS'),
    -- Client 4 (Elena)
    ('Megafon', 520.00, 4, 100007, '2025-02-10 14:55:00', 'FAILED'),
    ('Spotify', 599.00, 4, 100008, '2025-03-25 20:10:00', 'SUCCESS'),
    ('Netology', 6700.00, 4, 100007, '2025-04-15 10:00:00', 'SUCCESS'),
    ('Ozon', 3240.80, 4, 100008, '2025-05-08 15:45:00', 'SUCCESS'),
    -- Client 5 (Dmitry)
    ('MTS', 310.50, 5, 100009, '2025-05-20 09:30:00', 'SUCCESS'),
    ('Apple Music', 349.00, 5, 100010, '2025-01-15 21:05:00', 'SUCCESS'),
    ('Coursera', 5200.00, 5, 100009, '2025-03-05 07:45:00', 'FAILED'),
    ('Yandex Market', 1890.00, 5, 100010, '2025-06-18 12:15:00', 'SUCCESS'),
    -- Client 6 (Olga)
    ('Beeline', 275.00, 6, 100011, '2025-04-02 16:40:00', 'SUCCESS'),
    ('IVI', 499.00, 6, 100012, '2025-02-14 18:20:00', 'FAILED'),
    ('Stepik', 3200.00, 6, 100011, '2025-05-10 11:55:00', 'SUCCESS'),
    ('Wildberries', 980.00, 6, 100012, '2025-03-22 14:10:00', 'SUCCESS'),
    -- Client 7 (Sergey)
    ('Tele2', 410.00, 7, 100013, '2025-03-18 10:25:00', 'SUCCESS'),
    ('Kinopoisk', 399.00, 7, 100014, '2025-04-30 19:50:00', 'FAILED'),
    ('Hexlet', 7800.00, 7, 100013, '2025-01-25 08:10:00', 'IN_PROGRESS'),
    ('Ozon', 2750.45, 7, 100014, '2025-06-01 13:35:00', 'SUCCESS'),
    -- Client 8 (Anna)
    ('Megafon', 340.00, 8, 100015, '2025-05-12 17:00:00', 'SUCCESS'),
    ('YouTube Premium', 699.00, 8, 100016, '2025-02-05 20:30:00', 'FAILED'),
    ('SkillFactory', 4500.00, 8, 100015, '2025-04-20 09:20:00', 'SUCCESS'),
    ('Avito', 650.90, 8, 100016, '2025-03-15 11:45:00', 'SUCCESS'),
    -- Client 9 (Pavel)
    ('MTS', 480.00, 9, 100017, '2025-01-28 15:10:00', 'SUCCESS'),
    ('Discord Nitro', 450.00, 9, 100018, '2025-06-10 22:15:00', 'SUCCESS'),
    ('ProductStar', 12500.00, 9, 100017, '2025-04-08 10:40:00', 'SUCCESS'),
    ('Wildberries', 3420.00, 9, 100018, '2025-05-25 14:50:00', 'FAILED'),
    -- Client 10 (Natalia)
    ('Beeline', 290.00, 10, 100019, '2025-04-05 08:55:00', 'SUCCESS'),
    ('Yandex Plus', 599.00, 10, 100020, '2025-02-20 19:25:00', 'FAILED'),
    ('GeekBrains', 8900.00, 10, 100019, '2025-03-30 12:30:00', 'IN_PROGRESS'),
    ('Ozon', 1680.60, 10, 100020, '2025-06-15 16:05:00', 'SUCCESS');

INSERT INTO replenishment (client_to, card_no_to, amount, time_of_replenishment, status)
VALUES
    -- Client 1
    (1, 100001, 5000.00, '2025-01-20 10:00:00', 'SUCCESS'),
    (1, 100002, 3000.00, '2025-03-05 14:30:00', 'FAILED'),
    (1, 100001, 2500.00, '2025-05-15 09:15:00', 'SUCCESS'),
    -- Client 2
    (2, 100003, 7000.00, '2025-02-25 11:45:00', 'IN_PROGRESS'),
    (2, 100004, 4500.00, '2025-04-10 16:20:00', 'SUCCESS'),
    -- Client 3
    (3, 100005, 2000.00, '2025-03-15 08:30:00', 'SUCCESS'),
    (3, 100006, 3500.00, '2025-05-01 13:10:00', 'SUCCESS'),
    (3, 100005, 1200.00, '2025-06-10 17:40:00', 'IN_PROGRESS'),
    -- Client 4
    (4, 100007, 10000.00, '2025-01-10 12:00:00', 'SUCCESS'),
    (4, 100008, 6000.00, '2025-03-20 15:25:00', 'FAILED'),
    -- Client 5
    (5, 100009, 4000.00, '2025-04-20 09:50:00', 'SUCCESS'),
    (5, 100010, 5500.00, '2025-02-05 14:15:00', 'SUCCESS'),
    (5, 100009, 2800.00, '2025-05-25 10:35:00', 'IN_PROGRESS'),
    -- Client 6
    (6, 100011, 2500.00, '2025-03-10 11:20:00', 'SUCCESS'),
    (6, 100012, 3200.00, '2025-04-28 18:00:00', 'FAILED'),
    -- Client 7
    (7, 100013, 8000.00, '2025-03-30 13:45:00', 'SUCCESS'),
    (7, 100014, 4200.00, '2025-01-15 16:55:00', 'FAILED'),
    (7, 100013, 1500.00, '2025-06-05 08:10:00', 'SUCCESS'),
    -- Client 8
    (8, 100015, 1800.00, '2025-02-10 10:25:00', 'SUCCESS'),
    (8, 100016, 2700.00, '2025-04-15 19:30:00', 'SUCCESS'),
    -- Client 9
    (9, 100017, 12000.00, '2025-04-10 14:40:00', 'SUCCESS'),
    (9, 100018, 6500.00, '2025-05-20 12:05:00', 'FAILED'),
    (9, 100017, 3500.00, '2025-01-30 17:20:00', 'SUCCESS'),
    -- Client 10
    (10, 100019, 4500.00, '2025-02-25 09:00:00', 'FAILED'),
    (10, 100020, 5200.00, '2025-03-18 15:50:00', 'SUCCESS');

INSERT INTO transfer (client_id, card_no_from, card_no_to, amount, recipient, message, time_of_transfer, status)
VALUES 
    -- Transfers from Client 1
    (1, 100001, 100003, 1500.00, '+79162345678', 'For birthday', '2025-02-15 12:30:00', 'SUCCESS'),
    (1, 100002, 100005, 800.00, 'Alexey Ivanov', NULL, '2025-04-05 18:45:00', 'IN_PROGRESS'),
    -- From Client 2
    (2, 100003, 100007, 2200.00, '+79164567890', 'Debt repayment', '2025-03-20 10:15:00', 'FAILED'),
    (2, 100004, 100009, 950.50, 'Dmitry Kozlov', 'Thanks for help', '2025-05-12 14:20:00', 'SUCCESS'),
    -- From Client 3
    (3, 100005, 100011, 1200.00, '+79166789012', NULL, '2025-04-25 09:55:00', 'SUCCESS'),
    (3, 100006, 100013, 1750.00, 'Sergey Sokolov', 'Family transfer', '2025-06-01 16:40:00', 'SUCCESS'),
    -- From Client 4
    (4, 100007, 100015, 3000.00, '+79168901234', 'Rent share', '2025-01-28 11:10:00', 'FAILED'),
    (4, 100008, 100017, 1250.75, 'Pavel Morozov', NULL, '2025-03-15 13:35:00', 'SUCCESS'),
    -- From Client 5
    (5, 100009, 100019, 850.00, '+79160123456', 'Movie tickets', '2025-05-05 17:50:00', 'SUCCESS'),
    (5, 100010, 100001, 2100.00, 'Ivan Petrov', 'Return money', '2025-02-18 08:25:00', 'FAILED'),
    -- From Client 6
    (6, 100011, 100004, 650.00, '+79162345678', NULL, '2025-04-12 19:15:00', 'IN_PROGRESS'),
    (6, 100012, 100008, 1450.00, 'Elena Smirnova', 'Gift', '2025-06-20 14:00:00', 'SUCCESS'),
    -- From Client 7
    (7, 100013, 100006, 1800.00, 'Alexey Ivanov', 'Loan payback', '2025-03-10 10:50:00', 'IN_PROGRESS'),
    (7, 100014, 100016, 950.00, '+79168901234', NULL, '2025-05-22 15:30:00', 'SUCCESS'),
    -- From Client 8
    (8, 100015, 100018, 500.00, 'Pavel Morozov', 'Coffee money', '2025-04-08 20:10:00', 'SUCCESS'),
    (8, 100016, 100020, 780.90, '+79160123456', NULL, '2025-01-25 12:45:00', 'SUCCESS'),
    -- From Client 9
    (9, 100017, 100002, 1650.00, 'Ivan Petrov', 'Business deal', '2025-06-05 09:35:00', 'FAILED'),
    (9, 100018, 100010, 2400.00, 'Dmitry Kozlov', NULL, '2025-02-28 17:20:00', 'SUCCESS'),
    -- From Client 10
    (10, 100019, 100012, 1100.00, '+79166789012', 'Thanks!', '2025-05-18 11:00:00', 'SUCCESS'),
    (10, 100020, 100014, 1350.60, 'Sergey Sokolov', 'Family support', '2025-03-22 18:30:00', 'SUCCESS');

-- rollback
-- DELETE FROM transfer;
-- DELETE FROM replenishment;
-- DELETE FROM payment;
-- DELETE FROM card;
-- DELETE FROM client;
-- DELETE FROM users;
