-- Crear secuencias explícitas para cada tabla con tipo BIGINT
CREATE SEQUENCE IF NOT EXISTS tenants_id_seq AS BIGINT;
CREATE SEQUENCE IF NOT EXISTS address_id_seq AS BIGINT;
CREATE SEQUENCE IF NOT EXISTS users_id_seq AS BIGINT;

-- Crear la tabla tenants con BIGINT explícito
CREATE TABLE IF NOT EXISTS "tenants"
(
    id           BIGINT PRIMARY KEY DEFAULT nextval('tenants_id_seq')::BIGINT,
    name         VARCHAR(255) NOT NULL UNIQUE,
    email        VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);




CREATE TABLE IF NOT EXISTS address (
                                       id BIGINT PRIMARY KEY DEFAULT nextval('address_id_seq')::BIGINT,
                                       street VARCHAR(255) NOT NULL,
                                       city VARCHAR(100) NOT NULL,
                                       state VARCHAR(100) NOT NULL,
                                       country VARCHAR(100) NOT NULL,
                                       zip_code VARCHAR(20) NOT NULL,
                                       created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- INSERT INTO address (street, city, state, country, zip_code, tenant_id)
-- SELECT
--     '1600 Pennsylvania Avenue NW',  -- street
--     'Washington',                   -- city
--     'DC',                           -- state
--     'USA',                          -- country
--     '20500',                        -- zip_code
--     (SELECT id FROM tenants WHERE email = 'contacto@empresa-a.com')  -- tenant_id
-- WHERE NOT EXISTS (
--     SELECT 1 FROM address WHERE street = '1600 Pennsylvania Avenue NW' AND city = 'Washington'
-- );


-- Crear tabla de usuarios con DEFAULT nextval para ID
CREATE TABLE IF NOT EXISTS "users" (
                                       id BIGINT PRIMARY KEY DEFAULT nextval('users_id_seq')::BIGINT,
                                       email VARCHAR(255) UNIQUE NOT NULL,
                                       date_of_birth DATE NOT NULL,
                                       first_name VARCHAR(255),
                                       last_name VARCHAR(255),
                                       password VARCHAR(255) NOT NULL,
                                       role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',
                                       type VARCHAR(50) NOT NULL DEFAULT 'USER_DEFAULT',
                                       phone_number VARCHAR(20),
                                       is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                       tenant_id BIGINT,
                                       address_id BIGINT,
                                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
