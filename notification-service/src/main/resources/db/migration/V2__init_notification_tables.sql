CREATE TABLE notification_logs (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(100) NOT NULL,
    reference_no VARCHAR(50) NOT NULL,
    from_account_no VARCHAR(30),
    to_account_no VARCHAR(30),
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_notification_logs_reference_no
ON notification_logs(reference_no);

CREATE INDEX idx_notification_logs_event_id
ON notification_logs(event_id);

CREATE TABLE processed_events (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(100) NOT NULL UNIQUE,
    topic VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP NOT NULL
);
