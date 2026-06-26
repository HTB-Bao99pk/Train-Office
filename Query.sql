IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin')
    BEGIN
        INSERT INTO users (username, password_hash, role, status, created_at, updated_at)
        VALUES ('admin', '123', 'ADMIN', 'ACTIVE', GETDATE(), GETDATE());
    END