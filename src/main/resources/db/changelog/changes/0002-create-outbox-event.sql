CREATE TABLE outbox_event (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    original_event_id UUID NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    status VARCHAR(40) NOT NULL DEFAULT 'ENRICHING',
    published BOOLEAN NOT NULL DEFAULT FALSE,
    published_at TIMESTAMP WITH TIME ZONE,
    error TEXT,
    CONSTRAINT fk_original_event FOREIGN KEY(original_event_id)
        REFERENCES inbox_event(id)
        ON DELETE CASCADE
);

-- Optional: index for lookup by status or original_event_id
CREATE INDEX idx_outbox_event_status ON outbox_event(status);
CREATE INDEX idx_outbox_event_original_event ON outbox_event(original_event_id);
