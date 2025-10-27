-- V3 segura: añade columnas a test_session de forma idempotente y sin NOT NULL
-- Nota: usamos IF NOT EXISTS para evitar choques si ya existen columnas.

ALTER TABLE test_session
  ADD COLUMN IF NOT EXISTS participant_name           VARCHAR(150),
  ADD COLUMN IF NOT EXISTS participant_surname        VARCHAR(150),
  ADD COLUMN IF NOT EXISTS participant_birth_date     DATE,
  ADD COLUMN IF NOT EXISTS participant_country        VARCHAR(150),
  ADD COLUMN IF NOT EXISTS participant_gender         VARCHAR(50),
  ADD COLUMN IF NOT EXISTS participant_dominant_hand  VARCHAR(50),
  ADD COLUMN IF NOT EXISTS participant_position       VARCHAR(100),
  ADD COLUMN IF NOT EXISTS participant_inasidnr       VARCHAR(50),
  ADD COLUMN IF NOT EXISTS participant_event          VARCHAR(150),
  ADD COLUMN IF NOT EXISTS participant_instructor     VARCHAR(150),
  ADD COLUMN IF NOT EXISTS finished_at                TIMESTAMP,
  ADD COLUMN IF NOT EXISTS score                      INTEGER,
  ADD COLUMN IF NOT EXISTS total                      INTEGER;

-- Opcional (por si la has ejecutado a medias): elimina defaults "N/A" si los pusiste en otra versión
-- DO NOTHING si no existen:
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.columns
             WHERE table_name='test_session' AND column_name='participant_name') THEN
    BEGIN
      ALTER TABLE test_session ALTER COLUMN participant_name DROP DEFAULT;
    EXCEPTION WHEN undefined_object THEN
      -- ignorar
    END;
  END IF;

  IF EXISTS (SELECT 1 FROM information_schema.columns
             WHERE table_name='test_session' AND column_name='participant_surname') THEN
    BEGIN
      ALTER TABLE test_session ALTER COLUMN participant_surname DROP DEFAULT;
    EXCEPTION WHEN undefined_object THEN
      -- ignorar
    END;
  END IF;
END $$;
