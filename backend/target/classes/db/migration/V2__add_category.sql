ALTER TABLE contacts
    ADD COLUMN IF NOT EXISTS category VARCHAR(20);

UPDATE contacts
SET category = 'FRIEND'
WHERE category IS NULL;

ALTER TABLE contacts
    ALTER COLUMN category SET DEFAULT 'FRIEND';

ALTER TABLE contacts
    ALTER COLUMN category SET NOT NULL;
