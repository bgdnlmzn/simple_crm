CREATE TABLE transactions
(
    id               SERIAL PRIMARY KEY,
    seller_id        INT            NOT NULL,
    amount           NUMERIC(10, 2) NOT NULL,
    payment_type     VARCHAR(20)    NOT NULL,
    transaction_date TIMESTAMP      NOT NULL,
    updated_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active        BOOLEAN        NOT NULL DEFAULT TRUE,
    FOREIGN KEY (seller_id) REFERENCES sellers (id)
);