CREATE TABLE sellers
(
    id                SERIAL PRIMARY KEY,
    seller_name       VARCHAR(50) NOT NULL,
    contact_info      VARCHAR(50) NOT NULL,
    registration_date TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active         BOOLEAN     NOT NULL DEFAULT TRUE
);
