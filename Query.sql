IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@gmail.local')
    BEGIN
        INSERT INTO users (email, password, role, status, created_at, updated_at)
        VALUES ('admin@gmail.local', '123', 'ADMIN', 'ACTIVE', GETDATE(), GETDATE());
    END

IF EXISTS (
    SELECT 1
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'passengers'
      AND COLUMN_NAME = 'full_name'
      AND DATA_TYPE <> 'nvarchar'
)
    BEGIN
        ALTER TABLE passengers ALTER COLUMN full_name NVARCHAR(100) NOT NULL;
    END
