-- Crear la secuencia para los IDs si no existe
CREATE SEQUENCE IF NOT EXISTS patients_id_seq AS BIGINT;

-- Crear la tabla de pacientes si no existe
CREATE TABLE IF NOT EXISTS patients (
    -- Campos de BaseModel
                                        id BIGINT PRIMARY KEY DEFAULT nextval('patients_id_seq'),
                                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Campos específicos de Patient
                                        user_id BIGINT NOT NULL,
                                        first_name VARCHAR(255) NOT NULL,
                                        last_name VARCHAR(255) NOT NULL,
                                        blood_type VARCHAR(10),
                                        gender VARCHAR(20),
                                        weight DECIMAL(10, 2),
                                        height DECIMAL(10, 2),
                                        medical_history TEXT,
                                        emergency_contact_phone VARCHAR(20),
                                        insurance_provider VARCHAR(255),
                                        insurance_number VARCHAR(100)
);

-- Crear un índice para búsquedas rápidas por user_id
CREATE INDEX IF NOT EXISTS idx_patients_user_id ON patients(user_id);
