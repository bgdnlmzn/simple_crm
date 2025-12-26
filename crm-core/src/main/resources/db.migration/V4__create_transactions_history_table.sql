CREATE TABLE transactions_history
(
    id                SERIAL PRIMARY KEY,
    transaction_id    BIGINT,
    seller_id         BIGINT,
    amount            NUMERIC(10, 2),
    payment_type      VARCHAR(20),
    transaction_date  TIMESTAMP,
    updated_at        TIMESTAMP,
    change_timestamp  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    change_type       VARCHAR(20) NOT NULL
);