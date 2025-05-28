INSERT INTO scheduled_transfer
    (id, name, schedule_status, schedule_frequency, transfer_type, amount, currency, sender_account_number, beneficiary_account_number, start_date, end_date, next_transfer_date, created_at, updated_at, user_id)
VALUES
    (
      1,
      'electricity',
      'ACTIVE',
      'ONLY_ONCE',
      'TO_ANOTHER_ACCOUNT',
      10,
      'PLN',
      'PL48234567840000000000000012',
      'PL48234567840000000000000011',
      (CURRENT_DATE - INTERVAL '1 DAY'),
      '2023-11-23 17:47:24.613519+00',
      '2023-11-23 17:47:24.613519+00',
      CURRENT_TIMESTAMP,
      CURRENT_TIMESTAMP,
      '1'
    );

INSERT INTO scheduled_transfer
    (id, name, schedule_status, schedule_frequency, transfer_type, amount, currency, sender_account_number, beneficiary_account_number, start_date, end_date, next_transfer_date, created_at, updated_at, user_id)
VALUES
    (
      5,
      'transactionName',
      'DEACTIVATED',
      'ONLY_ONCE',
      'TO_ANOTHER_ACCOUNT',
      10,
      'PLN',
      'PL48234567840000000000000032',
      'PL48234567840000000000000031',
      '2023-09-29 11:00:00',
      '2023-09-30 11:00:00',
      '2023-09-30 11:00:00',
      '2023-09-29 11:00:00',
      '2023-09-29 11:00:00',
      '1'
    );

INSERT INTO scheduled_transfer
    (id, name, schedule_status, schedule_frequency, transfer_type, amount, currency, sender_account_number, beneficiary_account_number, start_date, end_date, next_transfer_date, created_at, updated_at, user_id)
VALUES
    (
      6,
      'transactionName',
      'ACTIVE',
      'ONLY_ONCE',
      'TO_ANOTHER_ACCOUNT',
      10,
      'PLN',
      'PL48234567840000000000000011',
      'PL48234567840000000000000031',
      CURRENT_DATE,
      '2023-09-30 11:00:00',
      '2023-09-30 11:00:00',
      CURRENT_TIMESTAMP,
      CURRENT_TIMESTAMP,
      '1'
    );

INSERT INTO scheduled_transfer
    (id, name, schedule_status, schedule_frequency, transfer_type, amount, currency, sender_account_number, beneficiary_account_number, start_date, end_date, next_transfer_date, created_at, updated_at, user_id)
VALUES
    (
      7,
      'transaction7',
      'ACTIVE',
      'ONLY_ONCE',
      'TO_ANOTHER_ACCOUNT',
      700,
      'PLN',
      'PL48234567840000000000000011',
      'PL48234567840000000000000031',
      CURRENT_TIMESTAMP,
      CURRENT_TIMESTAMP,
      CURRENT_TIMESTAMP,
      CURRENT_TIMESTAMP,
      CURRENT_TIMESTAMP,
      '1'
    );

INSERT INTO scheduled_transfer
    (id, name, schedule_status, schedule_frequency, transfer_type, amount, currency, sender_account_number, beneficiary_account_number, start_date, end_date, next_transfer_date, created_at, updated_at, user_id)
VALUES
    (
      8,
      'transaction7',
      'ACTIVE',
      'ONCE_A_QUARTER',
      'TO_ANOTHER_ACCOUNT',
      701,
      'PLN',
      'PL48234567840000000000000012',
      'PL48234567840000000000000033',
      '2023-09-29 00:00:00',
      '2023-09-30 23:59:59',
      '2023-09-30 23:00:00',
      '2023-09-29 11:00:00',
      '2023-09-29 11:00:00',
      '3'
    );