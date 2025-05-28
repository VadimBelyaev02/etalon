delete
from deposits;
delete
from products;
delete
from deposit_interests;

insert into products (id, name, min_deposit_period, max_deposit_period, term, interest_rate, min_open_amount, max_deposit_amount, currency, created_at, updated_at, early_withdrawal) values (1, 'Profit', 3, 3, 'MONTH', 5, 200, 1000000, 'PLN', '2023-08-28 11:30:00', '2023-08-28 11:30:00', 'false');

insert into products (id, name, min_deposit_period, max_deposit_period, term, interest_rate, min_open_amount, max_deposit_amount, currency, created_at, updated_at, early_withdrawal) values (2, 'Looong', 6, 24, 'MONTH', 7, 500, 500000, 'PLN', '2023-08-28 11:31:00', '2023-08-28 11:31:00', 'true');

insert into products (id, name, min_deposit_period, max_deposit_period, term, interest_rate, min_open_amount, max_deposit_amount, currency, created_at, updated_at, early_withdrawal) values (3, 'Smooth profit', 1, 5, 'YEAR', 9, 2000, 500000, 'PLN', '2023-08-28 11:32:00', '2023-08-28 11:32:00', 'false');

