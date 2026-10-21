ALTER TABLE tokens ADD COLUMN session_id VARCHAR(255);

UPDATE tokens
   SET session_id = gen_random_uuid()::text
 WHERE session_id IS NULL;

ALTER TABLE tokens ALTER COLUMN session_id SET NOT NULL;

CREATE INDEX idx_tokens_session_id ON tokens (session_id);