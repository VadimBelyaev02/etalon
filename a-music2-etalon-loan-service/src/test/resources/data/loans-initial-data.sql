insert into loans (id, user_id, product_id, amount, next_payment_date, status, created_at,
                   updated_at, account_number, contract_number)
values (1, 'user', 2, 6000, current_date-4, 'ACTIVE', current_date - 64, current_date,
        'PL04234567840000000000000001', 'CN00000000000001'),
       (2, 'user', 2, 6000, current_date+1, 'DELINQUENT', current_date - 100, current_date,
        'PL04234567840000000000000001', 'CN00000000000002'),
       (3, 'user', 3, 6000, current_date+1, 'ACTIVE', current_date - 30, current_date,
        'PL04234567840000000000000001', 'CN00000000000004'),
       (4, 'user', 3, 6000, current_date+1, 'CLOSED', current_date - 20, current_date,
        'PL04234567840000000000000001', 'CN00000000000005');

