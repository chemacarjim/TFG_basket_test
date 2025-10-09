-- Cambiar de ENUM 'choice_value' a VARCHAR(16)
ALTER TABLE response
    ALTER COLUMN selected_value TYPE VARCHAR(16)
    USING selected_value::text;

-- (Opcional) Si ya no usas el tipo ENUM en ninguna otra columna, puedes borrarlo:
/*
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_type WHERE typname = 'choice_value') THEN
        DROP TYPE choice_value;
    END IF;
END$$;
*/