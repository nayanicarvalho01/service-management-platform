CREATE TABLE appointments (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    description VARCHAR(300) NOT NULL,
    scheduled_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT appointment_status_valid
        CHECK (status IN ('SCHEDULED', 'COMPLETED', 'CANCELLED'))
);

CREATE INDEX appointments_customer_scheduled_idx
    ON appointments (customer_id, scheduled_at DESC);
