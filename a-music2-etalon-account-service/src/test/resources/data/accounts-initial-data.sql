insert into accounts (id, user_id, iban, updated_at, created_at, balance, currency, blocked, status, account_type)
values (2, '1', 'PL04234567840000000000000001', '2023-6-13T00:00:00', '2023-6-13T00:00:00', 0,
        'PLN', false, 'ACTIVE', 'CARD'),
       (3, '1', 'PL04234567840000000000000002', '2023-6-13T00:00:00', '2023-6-13T00:00:00', 0,
        'PLN', false, 'ACTIVE', 'CARD'),
       (4, '1', 'PL04234567840000000000000003', '2023-6-13T00:00:00', '2023-6-13T00:00:00', 5000.0,
        'PLN', false, 'ACTIVE', 'CARD'),
        (5, '1', 'PL04234567840000000000000004', '2023-6-13T00:00:00', '2023-6-13T00:00:00', 5000.0,
        'PLN', true, 'ACTIVE', 'CARD');

