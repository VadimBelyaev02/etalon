insert into auth_confirmations
    (id, created_at, updated_at, blocked_at, invalid_attempts, confirmation_method, confirmation_code, operation, target_id, status)
values
    (1, current_timestamp, current_timestamp, null , 0, 'EMAIL', 123456, 'OPEN_DEPOSIT', 1, 'CREATED');
insert into auth_confirmations
(id, created_at, updated_at, blocked_at, invalid_attempts, confirmation_method, confirmation_code, operation, target_id, status)
values
    (2, current_timestamp - interval '20 min', current_timestamp - interval '20 min', null , 0, 'EMAIL', 123456, 'CREATE_PAYMENT', 1, 'CREATED');

insert into auth_confirmations
(id, created_at, updated_at, blocked_at, invalid_attempts, confirmation_method, confirmation_code, operation, target_id, status)
values
    (3, current_timestamp, current_timestamp, null , 0, 'EMAIL', 123456, 'CREATE_PAYMENT', 1, 'CONFIRMED');

insert into auth_confirmations
(id, created_at, updated_at, blocked_at, invalid_attempts, confirmation_method, confirmation_code, operation, target_id, status)
values
    (4, current_timestamp, current_timestamp, current_timestamp , 0, 'EMAIL', 123456, 'CREATE_PAYMENT', 1, 'BLOCKED');
insert into auth_confirmations
(id, created_at, updated_at, blocked_at, invalid_attempts, confirmation_method, confirmation_code, operation, target_id, status)
values
    (5, current_timestamp, current_timestamp, null , 0, 'EMAIL', 123456, 'CREATE_PAYMENT', 1, 'BLOCKED');
insert into auth_confirmations
(id, created_at, updated_at, blocked_at, invalid_attempts, confirmation_method, confirmation_code, operation, target_id, status)
values
    (6, current_timestamp, current_timestamp, null , 0, 'EMAIL', 123456, 'CREATE_TRANSFER', 1, 'CREATED');
insert into auth_confirmations
(id, created_at, updated_at, blocked_at, invalid_attempts, confirmation_method, confirmation_code, operation, target_id, status, blocked_until)
values
    (7, current_timestamp, current_timestamp, null , 0, 'EMAIL', 123456, 'CREATE_TRANSFER', 1, 'CREATED', null),
    (8, current_timestamp, current_timestamp, null , 0, 'EMAIL', 123456, 'CREATE_TRANSFER', 2, 'CREATED', null),
    (9, current_timestamp, current_timestamp, null , 0, 'EMAIL', 123456, 'CREATE_TRANSFER', 3, 'NOT_PROCESSED', null),
    (10, current_timestamp, current_timestamp, null , 0, 'EMAIL', 123456, 'CREATE_TRANSFER', 4, 'CONFIRMED', null),
    (11, current_timestamp, current_timestamp, current_timestamp , 0, 'EMAIL', 123456, 'CREATE_TRANSFER', 5, 'BLOCKED', null),
    (12, current_timestamp, current_timestamp, current_timestamp , 2, 'EMAIL', 123456, 'EMAIL_MODIFICATION', 5, 'BLOCKED', current_timestamp + interval '10 min'),
    (13, current_timestamp, current_timestamp, current_timestamp , 2, 'EMAIL', 123456, 'EMAIL_MODIFICATION', 5, 'DELETED', current_timestamp + interval '10 min'),
    (14, current_timestamp, current_timestamp, null , 0, 'EMAIL', 123456, 'EMAIL_MODIFICATION', 5, 'CREATED', null)