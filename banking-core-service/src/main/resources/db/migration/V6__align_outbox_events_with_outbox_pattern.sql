ALTER TABLE outbox_events
    DROP COLUMN IF EXISTS aggregate_key;
