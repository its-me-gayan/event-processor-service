CREATE TABLE inbox_event (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR NOT NULL,
    country_code CHAR(2) NOT NULL,
    event_ts TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    status VARCHAR(40) NOT NULL DEFAULT 'RECEIVED',
    published BOOLEAN NOT NULL DEFAULT FALSE,
    published_at TIMESTAMP WITH TIME ZONE,
    error TEXT
);

-- Optional: index for faster lookups by user_id or event_ts
CREATE INDEX idx_inbox_event_user_id ON inbox_event(user_id);
CREATE INDEX idx_inbox_event_event_ts ON inbox_event(event_ts);
