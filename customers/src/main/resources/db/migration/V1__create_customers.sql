CREATE TABLE customers (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    phone VARCHAR(32),
    created_at TIMESTAMPTZ NOT NULL
);
