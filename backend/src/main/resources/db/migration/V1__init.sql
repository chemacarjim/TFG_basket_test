CREATE TYPE choice_value AS ENUM ('DRIBBLE','PASS','SHOOT');

CREATE TABLE admin_user (
  id BIGSERIAL PRIMARY KEY,
  email TEXT UNIQUE NOT NULL,
  password_plain TEXT,                -- solo en V1; será sustituido por password_hash
  password_hash TEXT,
  created_at TIMESTAMPTZ DEFAULT now(),
  last_login_at TIMESTAMPTZ
);

CREATE TABLE test (
  id BIGSERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  description TEXT,
  is_active BOOLEAN DEFAULT TRUE,
  created_by_admin_id BIGINT REFERENCES admin_user(id),
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ
);

CREATE TABLE question (
  id BIGSERIAL PRIMARY KEY,
  test_id BIGINT NOT NULL REFERENCES test(id) ON DELETE CASCADE,
  prompt TEXT NOT NULL,
  possession_time INT,
  image_url TEXT,
  order_index INT NOT NULL DEFAULT 0
);

CREATE TABLE test_session (
  id BIGSERIAL PRIMARY KEY,
  test_id BIGINT NOT NULL REFERENCES test(id) ON DELETE CASCADE,
  anon_user_code TEXT,
  started_at TIMESTAMPTZ DEFAULT now(),
  finished_at TIMESTAMPTZ,
  score INT,
  total INT,
  duration_ms BIGINT
);

CREATE TABLE response (
  id BIGSERIAL PRIMARY KEY,
  session_id BIGINT NOT NULL REFERENCES test_session(id) ON DELETE CASCADE,
  question_id BIGINT NOT NULL REFERENCES question(id) ON DELETE CASCADE,
  selected_value choice_value NOT NULL,
  is_correct BOOLEAN,
  response_time_ms BIGINT
);

CREATE TABLE asset (
  id BIGSERIAL PRIMARY KEY,
  kind TEXT NOT NULL, -- 'QUESTION_IMAGE'
  url TEXT NOT NULL,
  public_id TEXT,
  uploaded_by_admin_id BIGINT REFERENCES admin_user(id),
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_question_test_order ON question(test_id, order_index);
CREATE INDEX idx_response_session ON response(session_id);
CREATE INDEX idx_session_test ON test_session(test_id, started_at);

-- Seed admin (password plano, será cifrado al iniciar)
INSERT INTO admin_user(email, password_plain)
VALUES ('admin@tfg.local', 'admin123');

-- Seed demo
INSERT INTO test(title, description, is_active)
VALUES ('Test demo: Decisión en baloncesto', 'Batería de 3 preguntas con DRIBBLE/PASS/SHOOT', TRUE);

INSERT INTO question(test_id, prompt, possession_time, image_url, order_index)
SELECT t.id, 'Situación 1: jugador bajo presión', 12, 'https://placehold.co/800x450', 1 FROM test t WHERE t.title LIKE 'Test demo%';
INSERT INTO question(test_id, prompt, possession_time, image_url, order_index)
SELECT t.id, 'Situación 2: dos defensores cerrando', 6, 'https://placehold.co/800x450', 2 FROM test t WHERE t.title LIKE 'Test demo%';
INSERT INTO question(test_id, prompt, possession_time, image_url, order_index)
SELECT t.id, 'Situación 3: compañero liberado', 18, 'https://placehold.co/800x450', 3 FROM test t WHERE t.title LIKE 'Test demo%';
