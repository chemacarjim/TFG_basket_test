ALTER TABLE question
    ALTER COLUMN correct_value TYPE VARCHAR(16)
    USING correct_value::text;