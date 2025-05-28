delete
from deposits;

delete
from products;

INSERT
INTO products (id, name, min_deposit_period, max_deposit_period, term, currency, interest_rate, min_open_amount, max_deposit_amount, early_withdrawal, created_at,
                   updated_at)
VALUES
    (1, 'Profit', 3.00, 3.00, 'MONTH', 'PLN', 5.00, 200.00, 1000000.00, false, current_date, current_date),
    (2, 'Looong', 6.00, 24.00, 'MONTH', 'PLN', 7.00, 500.00, 500000.00, true, current_date, current_date),
    (3, 'Smooth profit', 1.00, 5.00, 'YEAR', 'PLN', 9.00, 2000.00, 500000.00, false, current_date, current_date);

