-- V3__Add_unique_constraints_to_users.sql

ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);
ALTER TABLE users ADD CONSTRAINT uk_users_login UNIQUE (login);