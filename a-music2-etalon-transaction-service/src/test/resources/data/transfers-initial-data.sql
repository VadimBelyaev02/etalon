INSERT INTO transfers (id, transaction_id, comment, user_id, amount, fee, destination, source, status, created_at,
                       updated_at, transfer_type_id, is_template, template_name, is_fee_provided)
VALUES (1, 4, 'comment', '3', 10, 0, 'PL48234567840000000000000032', 'PL48234567840000000000000031', 'PROCESSING', '2023-11-23 17:47:24.613519 +00:00',
        '2023-11-23 17:47:36.276218 +00:00', 1, true, 'Template 5', false),
       (2, 6, 'comment', '3', 9, 1, 'PL48234567840000000000000031', 'PL48234567840000000000000011', 'APPROVED', '2023-11-23 17:47:24.613519 +00:00',
            '2023-11-23 17:47:36.276218 +00:00', 1, true, 'Template 5', true),
       (3, 5, 'comment', '3', 10, 0, 'PL48234567840000000000000032', 'PL48234567840000000000000031', 'DECLINED', '2023-11-23 17:47:24.613519 +00:00',
        '2023-11-23 17:47:36.276218 +00:00', 1, true, 'Template 5', false),
       (7, 1, 'comment', '3', 10, 0, 'PL48234567840000000000000012', 'PL48234567840000000000000011', 'APPROVED', '2023-11-23 17:47:24.613519 +00:00',
        '2023-11-23 17:47:36.276218 +00:00', 1, true, 'Template 5', false),
       (8, 7, 'comment', '3', 600, 0, 'PL48234567840000000000000012', 'PL48234567840000000000000031', 'PROCESSING', '2023-11-23 17:47:24.613519 +00:00',
        '2023-11-23 17:47:36.276218 +00:00', 1, false, 'Template 5', false),
       (9, 7, 'comment', '3', 400, 0, 'PL48234567840000000000000012', 'PL48234567840000000000000031', 'PROCESSING', '2023-11-23 17:47:24.613519 +00:00',
        '2023-11-23 17:47:36.276218 +00:00', 1, false, 'Template 5', false),
       (10, 8, 'comment', '3', 1000, 0, 'PL48234567840000000000000032', 'PL48234567840000000000000012', 'PROCESSING', '2023-11-23 17:47:24.613519 +00:00',
        '2023-11-23 17:47:36.276218 +00:00', 1, false, 'Template 5', false);

INSERT INTO transfers(
    id,  comment, user_id, amount, destination, source, status, created_at,
    updated_at, transfer_type_id, is_fee_provided, fee, standard_rate)
VALUES
    (4, 'transfer for different currency', '3', 10, 'PL48234567840000000000000032', 'PL48234567840000000000000031', 'PROCESSING', '2023-11-23 17:47:36 +00:00',
     '2023-11-23 17:47:36 +00:00', 1, false, null, 2),
    (5, 'transfer from PLN to USD', '3', 100, 'PL48234567840000000000000032', 'PL48234567840000000000000031', 'PROCESSING', '2023-11-23 17:47:36 +00:00',
     '2023-11-23 17:47:36 +00:00', 1, false, null, 4.035),
    (6, 'transfer from USD to PLN', '3', 100, 'PL48234567840000000000000032', 'PL48234567840000000000000031', 'PROCESSING', '2023-11-23 17:47:00 +00:00',
     '2023-11-23 17:47:00 +00:00', 1, false, null, 0.247);
