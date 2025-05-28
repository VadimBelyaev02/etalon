INSERT INTO orders(id, user_id, product_id, amount, status, created_at, updated_at, borrower, average_monthly_salary,
                   average_monthly_expenses)
VALUES (1, 'user', 2, 6000.65, 'APPROVED', '2023-08-26 16:04:46.362325', '2023-08-26 16:04:46.362325', 'USER TEST',
        6000.0, 1000.0),
       (2, 'user', 2, 10000.0, 'REJECTED', '2023-6-13T00:00:00', '2023-6-13T00:00:00', 'USER TEST', 6000.0, 5900.0),
       (3, 'user', 2, 10000.0, 'APPROVED', '2023-6-13T00:00:00', '2023-6-13T00:00:00', 'USER TEST', 6000.0, 1000.0);

INSERT INTO guarantors (id, pesel, first_name, last_name, created_at, updated_at)
VALUES (1, '11111111111', 'Marta', 'Nowak', '2023-6-13T00:00:00', '2023-6-13T00:00:00');


INSERT INTO order_to_guarantor (order_id, guarantor_id)
VALUES (1, 1),
       (2, 1),
       (3, 1);





