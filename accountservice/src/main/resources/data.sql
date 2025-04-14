-- Crear la tabla tenants si no existe
CREATE TABLE IF NOT EXISTS "tenants"
(
    id           UUID PRIMARY KEY, -- UUID generado automáticamente
    name         VARCHAR(255) NOT NULL UNIQUE,
    email        VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    created_at   TIMESTAMP NOT NULL        DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP NOT NULL      DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO tenants (id, name, email, phone_number)
VALUES (
           'afd4eb70-c4e9-41b1-9034-048103d30c98', -- UUID fijo para el ejemplo
           'Empresa A',
           'contacto@empresa-a.com',
           '123-456-7890'
       ) ON CONFLICT (email) DO NOTHING;

-- Create the address table
CREATE TABLE IF NOT EXISTS address (
    -- Columna ID: UUID como clave primaria.
    -- Asume que los UUIDs son generados por la aplicación o proporcionados en el INSERT.
                         id UUID PRIMARY KEY,

    -- Columnas basadas en la entidad Java, marcadas como NOT NULL
                         street VARCHAR(255) NOT NULL,
                         city VARCHAR(100) NOT NULL,
                         state VARCHAR(100) NOT NULL,
                         country VARCHAR(100) NOT NULL,

    -- Columna zip_code con el nombre especificado y NOT NULL
                         zip_code VARCHAR(20) NOT NULL,

    -- Columna tenant_id: UUID y NOT NULL, según lo requerido
                         tenant_id UUID NOT NULL
);
CREATE TABLE IF NOT EXISTS address (
                                       id UUID PRIMARY KEY,
                                       street VARCHAR(255) NOT NULL,
                                       city VARCHAR(100) NOT NULL,
                                       state VARCHAR(100) NOT NULL,
                                       country VARCHAR(100) NOT NULL,
                                       zip_code VARCHAR(20) NOT NULL,
                                       tenant_id UUID NOT NULL
);

-- ========= IDEMPOTENT INSERT Statements =========

-- Tenant ID constante para todas las inserciones
-- 'afd4eb70-c4e9-41b1-9034-048103d30c98'

-- Insertar Dirección 1 (White House) solo si no existe una dirección con ese ID
INSERT INTO address (id, street, city, state, country, zip_code, tenant_id)
SELECT
    '1e6a87f0-1a7b-4f0a-9c2d-3e8b6f4a1e9c', -- id
    '1600 Pennsylvania Avenue NW',        -- street
    'Washington',                         -- city
    'DC',                                 -- state
    'USA',                                -- country
    '20500',                              -- zip_code
    'afd4eb70-c4e9-41b1-9034-048103d30c98' -- tenant_id
WHERE NOT EXISTS (
    SELECT 1 FROM address WHERE id = '1e6a87f0-1a7b-4f0a-9c2d-3e8b6f4a1e9c'
);

-- Insertar Dirección 2 (Googleplex) solo si no existe una dirección con ese ID
INSERT INTO address (id, street, city, state, country, zip_code, tenant_id)
SELECT
    'f8c3b4a0-2b8e-4d9f-8a1c-9e0b7d3a2f5b', -- id
    '1600 Amphitheatre Parkway',          -- street
    'Mountain View',                      -- city
    'CA',                                 -- state
    'USA',                                -- country
    '94043',                              -- zip_code
    'afd4eb70-c4e9-41b1-9034-048103d30c98' -- tenant_id
WHERE NOT EXISTS (
    SELECT 1 FROM address WHERE id = 'f8c3b4a0-2b8e-4d9f-8a1c-9e0b7d3a2f5b'
);
-- Ensure the 'users' table exists
CREATE TABLE IF NOT EXISTS "users" (
                                       id UUID PRIMARY KEY,
                                       email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    tenant_id UUID NOT NULL,
    address_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE RESTRICT,
    FOREIGN KEY (address_id) REFERENCES address(id) ON DELETE RESTRICT
    );

-- Insert the user if no existing user with the same id or email exists
INSERT INTO "users" (id, tenant_id, address_id, type, email, password, role, first_name, last_name, phone_number, is_active, created_at, updated_at)
SELECT '223e4567-e89b-12d3-a456-426614174006', 'afd4eb70-c4e9-41b1-9034-048103d30c98', '1e6a87f0-1a7b-4f0a-9c2d-3e8b6f4a1e9c' , 'USER_DEFAULT' ,'jesus@test.com',
       '$2b$12$7hoRZfJrRKD2nIm2vHLs7OBETy.LWenXXMLKf99W8M4PUwO6KB7fu', 'ROLE_USER',
       'jesus', 'rincon', '+1234567890', TRUE,
       '2025-04-02 10:00:00', '2025-04-02 10:00:00'
    WHERE NOT EXISTS (
    SELECT 1
    FROM "users"
    WHERE id = '223e4567-e89b-12d3-a456-426614174006'
);

-- Insert the user if no existing user with the same id or email exists
INSERT INTO "users" (id, type, tenant_id, address_id ,email, password, role, first_name, last_name, phone_number, is_active, created_at, updated_at)
SELECT '223e4567-e89b-12d3-a456-426614174010', 'USER_DEFAULT' ,'afd4eb70-c4e9-41b1-9034-048103d30c98', '1e6a87f0-1a7b-4f0a-9c2d-3e8b6f4a1e9c' ,'jesus2@test.com',
       '$2b$12$7hoRZfJrRKD2nIm2vHLs7OBETy.LWenXXMLKf99W8M4PUwO6KB7fu', 'ROLE_ADMIN',
       'jesus', 'admin', '+1234567890', TRUE,
       '2025-04-02 10:00:00', '2025-04-02 10:00:00'
    WHERE NOT EXISTS (
    SELECT 1
    FROM "users"
    WHERE id = '223e4567-e89b-12d3-a456-426614174010'
);
