ALTER SEQUENCE transactions_sequence RESTART;
ALTER SEQUENCE events_sequence RESTART;
ALTER SEQUENCE payments_sequence RESTART;
ALTER SEQUENCE transfers_sequence RESTART;
ALTER SEQUENCE scheduled_transfer_sequence RESTART;
TRUNCATE events CASCADE;
TRUNCATE transactions CASCADE;
TRUNCATE payments CASCADE;
TRUNCATE transfers CASCADE;
TRUNCATE scheduled_transfer CASCADE;

