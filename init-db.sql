-- Create Keycloak database
CREATE DATABASE keycloak_db;

-- Create separate schemas for better organization
CREATE SCHEMA IF NOT EXISTS customer;
CREATE SCHEMA IF NOT EXISTS loan;
CREATE SCHEMA IF NOT EXISTS credit;
CREATE SCHEMA IF NOT EXISTS audit;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE los_db TO los_user;
GRANT ALL PRIVILEGES ON DATABASE keycloak_db TO los_user;
