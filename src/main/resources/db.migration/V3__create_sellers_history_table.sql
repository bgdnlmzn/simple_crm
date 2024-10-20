CREATE TABLE sellers_history
(
    id                SERIAL PRIMARY KEY,
    seller_id         BIGINT,
    seller_name       VARCHAR(50),
    contact_info      VARCHAR(50),
    registration_date TIMESTAMP,
    updated_at        TIMESTAMP,
    change_timestamp  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    change_type       VARCHAR(20) NOT NULL
);