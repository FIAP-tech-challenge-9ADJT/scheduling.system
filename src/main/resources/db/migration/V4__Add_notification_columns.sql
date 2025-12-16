ALTER TABLE consultation_history 
    ADD COLUMN notification_status VARCHAR(20) NULL,
    ADD COLUMN notification_sent_at DATETIME NULL,
    ADD COLUMN notification_attempts INT NOT NULL DEFAULT 0;

