ALTER TABLE idempotency_keys
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE;

UPDATE idempotency_keys
SET updated_at = created_at
WHERE updated_at IS NULL;

ALTER TABLE idempotency_keys
    ALTER COLUMN updated_at SET NOT NULL;
