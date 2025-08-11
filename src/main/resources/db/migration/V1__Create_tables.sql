CREATE TABLE parking_spot (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(10) NOT NULL UNIQUE,
    spot_type VARCHAR(20) NOT NULL CHECK (spot_type IN ('STANDARD', 'DISABLED', 'ELECTRIC', 'TRUCK')),
    is_occupied BOOLEAN NOT NULL DEFAULT FALSE,
    hourly_rate DECIMAL(10, 2) NOT NULL
);

CREATE TABLE vehicle (
    id BIGSERIAL PRIMARY KEY,
    license_plate VARCHAR(15) NOT NULL UNIQUE,
    model VARCHAR(50),
    color VARCHAR(20),
    vehicle_type VARCHAR(20) CHECK (vehicle_type IN ('CAR', 'MOTORCYCLE', 'TRUCK'))
);

CREATE TABLE parking_session (
    id BIGSERIAL PRIMARY KEY,
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP,
    total_cost DECIMAL(10, 2),
    vehicle_id BIGINT NOT NULL REFERENCES vehicle(id),
    spot_id BIGINT NOT NULL REFERENCES parking_spot(id)
);

CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'OPERATOR'))
);