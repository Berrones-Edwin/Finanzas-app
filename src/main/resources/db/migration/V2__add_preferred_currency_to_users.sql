ALTER TABLE users
ADD COLUMN preferred_currency VARCHAR(3);

UPDATE users
SET preferred_currency = 'MXN'
WHERE preferred_currency IS NULL;

ALTER TABLE users
ALTER COLUMN preferred_currency SET NOT NULL;