INSERT INTO payments
(id, payment_product_id, transaction_id, comment, created_at, user_id, amount, account_number_withdrawn,
 is_template, template_name, status, updated_at)
VALUES
(1, 1, 1, 'Payment for first product', '2023-11-05 20:06:09.631746', 'user', 10, 'PL48234567840000000000000101',
 false, 'Template 4', 'PROCESSING', '2023-11-05 20:06:47.262178'),
(2, 1, 16, 'Payment for first product', '2023-11-05 20:06:09.631746', '7', 10, 'PL48234567840000000000009741',
 false, 'Template 4', 'PROCESSING', '2023-11-05 20:06:47.262178');
