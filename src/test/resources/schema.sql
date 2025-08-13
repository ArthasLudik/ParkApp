-- Схема для тестовой базы данных
CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'OPERATOR'))
);

CREATE TABLE IF NOT EXISTS parking_spot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(10) NOT NULL UNIQUE,
    spot_type VARCHAR(20) NOT NULL CHECK (spot_type IN ('STANDARD', 'DISABLED', 'ELECTRIC', 'TRUCK')),
    is_occupied BOOLEAN NOT NULL DEFAULT FALSE,
    hourly_rate DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS vehicle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(15) NOT NULL UNIQUE,
    model VARCHAR(50),
    color VARCHAR(20),
    vehicle_type VARCHAR(20) CHECK (vehicle_type IN ('CAR', 'MOTORCYCLE', 'TRUCK'))
);

CREATE TABLE IF NOT EXISTS parking_session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP,
    total_cost DECIMAL(10, 2),
    vehicle_id BIGINT NOT NULL,
    spot_id BIGINT NOT NULL,
    operator_id BIGINT NOT NULL,
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(id),
    FOREIGN KEY (spot_id) REFERENCES parking_spot(id)
);
