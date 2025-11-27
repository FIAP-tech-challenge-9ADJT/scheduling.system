CREATE TABLE IF NOT EXISTS consultation_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    nurse_id BIGINT,
    date_time DATETIME NOT NULL,
    description TEXT NOT NULL,
    notes TEXT
);
