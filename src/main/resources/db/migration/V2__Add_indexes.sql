CREATE INDEX idx_parking_spot_occupied ON parking_spot(is_occupied) WHERE NOT is_occupied;

CREATE INDEX idx_parking_session_active ON parking_session(vehicle_id) WHERE exit_time IS NULL;