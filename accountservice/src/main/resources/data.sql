-- Ensure the 'users' table exists
CREATE TABLE IF NOT EXISTS "users" (
                                       id UUID PRIMARY KEY,
                                       email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- Insert the user if no existing user with the same id or email exists
INSERT INTO "users" (id, email, password, role, first_name, last_name, created_at, updated_at)
SELECT '223e4567-e89b-12d3-a456-426614174006', 'jesus@test.com',
       '$2b$12$7hoRZfJrRKD2nIm2vHLs7OBETy.LWenXXMLKf99W8M4PUwO6KB7fu', 'ADMIN', 'jesus', 'rincon',
       '2025-04-02 10:00:00', '2025-04-02 10:00:00'
    WHERE NOT EXISTS (
    SELECT 1
    FROM "users"
    WHERE id = '223e4567-e89b-12d3-a456-426614174006'
);