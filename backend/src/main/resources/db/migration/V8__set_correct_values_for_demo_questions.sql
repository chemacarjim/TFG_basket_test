-- Evita que el test demo marque todo como fallo por falta de correct_value.
-- Se asignan respuestas correctas a las preguntas de seed que estaban a NULL.
UPDATE question
SET correct_value = 'PASS'
WHERE prompt = 'Situación 1: jugador bajo presión'
  AND correct_value IS NULL;

UPDATE question
SET correct_value = 'DRIBBLE'
WHERE prompt = 'Situación 2: dos defensores cerrando'
  AND correct_value IS NULL;

UPDATE question
SET correct_value = 'PASS'
WHERE prompt = 'Situación 3: compañero liberado'
  AND correct_value IS NULL;

