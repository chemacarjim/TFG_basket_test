ALTER TABLE admin_user 
    ADD COLUMN IF NOT EXISTS is_super_admin BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE admin_user 
    SET is_super_admin = TRUE
    WHERE email = 'admin@tfg.local';