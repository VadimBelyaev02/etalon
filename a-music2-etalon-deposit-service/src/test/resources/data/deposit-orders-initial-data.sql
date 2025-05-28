delete
from deposit_orders;

insert
into deposit_orders (id, user_id, amount, deposit_period, status, source_account, interest_account, final_transfer_account, product_id, created_at,
                   updated_at, transaction_id)
values
    (1, 'user', 1000.00, 12, 'CREATED', 'PL04234567840000000000000001', 'PL04234567840000000000000001', 'PL04234567840000000000000001', 1, current_date, current_date, 50),
    (2, 'user', 1234.00, 12, 'CREATED', 'PL04234567840000000000000001', 'PL04234567840000000000000001', 'PL04234567840000000000000001', 1, current_date, current_date, 3);