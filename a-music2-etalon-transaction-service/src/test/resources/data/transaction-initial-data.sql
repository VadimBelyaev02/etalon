insert into transactions (id, status, details, name, created_at,
                          processed_at, receiver_account, sender_account, amount, currency)
values (1, 'APPROVED', 'OPEN_DEPOSIT', 'transactionName', current_date - 1, '2023-11-23 17:47:24.613519 +00:00',
        'PL48234567840000000000000012',
        'PL48234567840000000000000011', 10, 'PLN'),
       (5, 'APPROVED', 'OPEN_DEPOSIT', 'transactionName', '2023-09-29 11:00:00', '2023-09-30 11:00:00',
        'PL48234567840000000000000032', 'PL48234567840000000000000031', 10, 'PLN'),
       (6, 'APPROVED', 'OPEN_DEPOSIT', 'transactionName', current_date, '2023-09-30 11:00:00',
        'PL48234567840000000000000011', 'PL48234567840000000000000031', 10, 'PLN'),
       (7, 'APPROVED', 'TRANSFER', 'transaction7', current_timestamp, current_timestamp, 'PL48234567840000000000000011',
        'PL48234567840000000000000031', 700, 'PLN');
insert into transactions (id, status, details, name, created_at,
                          receiver_account, sender_account, amount, currency)
values (2, 'DECLINED', 'LOAN', 'transaction2', current_date - 1, 'PL48234567840000000000000011',
        'PL48234567840000000000000011', 100, 'PLN'),
       (3, 'PROCESSING', 'TRANSFER', 'transaction3', current_date - 1, 'PL48234567840000000000000011',
        'PL48234567840000000000000011', 1000, 'PLN'),
       (4, 'APPROVED', 'TRANSFER', 'transaction4', current_timestamp, 'PL48234567840000000000000032',
        'PL48234567840000000000000031', 10000, 'PLN'),
       (8, 'APPROVED', 'TRANSFER', 'transaction4', current_timestamp, 'PL48234567840000000000000032',
        'PL48234567840000000000000031', 800, 'PLN');

insert into transactions (id, status, details, name, created_at,
                          processed_at, receiver_account, sender_account, amount, currency)
values (9, 'APPROVED', 'TRANSFER', 'transaction9', current_date - 1, current_date - 1, 'PL48234567840000000000000021',
        'PL48234567840000000000000022', 700, 'PLN'),
       (10, 'APPROVED', 'TRANSFER', 'transaction10', current_timestamp, current_timestamp,
        'PL48234567840000000000000021',
        'PL48234567840000000000000032', 700, 'PLN'),
       (11, 'APPROVED', 'TRANSFER', 'transaction11', current_date - 10, current_date - 10,
        'PL48234567840000000000000022', 'PL48234567840000000000000021', 700, 'PLN');


insert into transactions (id, status, details, name, created_at, processed_at, receiver_account, sender_account, amount, currency)
values (12, 'APPROVED', 'PAYMENT', 'transaction12', current_timestamp, current_timestamp, 'PL48234567840000000000000011', 'PL48234567840000000000000031', 800, 'PLN'),
       (13, 'APPROVED', 'PAYMENT', 'transaction13', current_timestamp, current_timestamp, 'PL48234567840000000000000011', 'PL48234567840000000000000050', 10, 'PLN'),
       (14, 'APPROVED', 'PAYMENT', 'transaction14', current_timestamp, current_timestamp, 'PL48234567840000000000000011', 'PL48234567840000000000000050', 50, 'PLN');

insert into transactions (id, status, details, name, created_at, processed_at, receiver_account, sender_account, amount, currency, receiver_card_id, sender_card_id)
values (15, 'APPROVED', 'TRANSFER', 'transaction15', current_timestamp, current_timestamp, 'PL48234567840000000000009741', 'PL48234567840000000000004152', 100, 'PLN', 4, 5),
       (16, 'APPROVED', 'TRANSFER', 'transaction16', current_timestamp, current_timestamp, 'PL48234567840000000000009741', 'PL48234567840000000000004152', 300, 'PLN', 4, 5)