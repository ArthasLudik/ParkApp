-- Тестовые данные для пользователей
INSERT INTO app_user (username, password_hash, role) VALUES 
('admin', 'admin123', 'ADMIN'),
('operator1', 'op123', 'OPERATOR'),
('operator2', 'op456', 'OPERATOR');

-- Тестовые данные для парковочных мест
INSERT INTO parking_spot (number, spot_type, is_occupied, hourly_rate) VALUES 
('A1', 'STANDARD', false, 5.00),
('A2', 'STANDARD', false, 5.00),
('A3', 'ELECTRIC', false, 7.50),
('B1', 'DISABLED', false, 3.00),
('B2', 'TRUCK', false, 10.00);

-- Тестовые данные для транспортных средств
INSERT INTO vehicle (license_plate, model, color, vehicle_type) VALUES 
('ABC123', 'Toyota Camry', 'Red', 'CAR'),
('XYZ789', 'Honda Civic', 'Blue', 'CAR'),
('DEF456', 'Harley Davidson', 'Black', 'MOTORCYCLE'),
('GHI789', 'Ford F-150', 'White', 'TRUCK');

-- Тестовые данные для парковочных сессий (используем совместимый с H2 синтаксис)
INSERT INTO parking_session (entry_time, vehicle_id, spot_id, operator_id) VALUES 
(DATEADD('HOUR', -2, CURRENT_TIMESTAMP), 1, 1, 2),
(DATEADD('HOUR', -1, CURRENT_TIMESTAMP), 2, 2, 2),
(DATEADD('MINUTE', -30, CURRENT_TIMESTAMP), 3, 3, 3);
