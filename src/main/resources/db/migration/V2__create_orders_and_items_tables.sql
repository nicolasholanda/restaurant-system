CREATE TABLE orders (
    id            BIGSERIAL       PRIMARY KEY,
    customer_name VARCHAR(255)    NOT NULL,
    status        VARCHAR(20)     NOT NULL,
    created_at    TIMESTAMP       NOT NULL
);

CREATE TABLE order_items (
    id       BIGSERIAL      PRIMARY KEY,
    order_id BIGINT         NOT NULL REFERENCES orders(id),
    dish_id  BIGINT         NOT NULL REFERENCES dishes(id),
    quantity NUMERIC(5, 2)  NOT NULL
);
