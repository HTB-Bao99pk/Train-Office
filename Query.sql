-- =====================================================================
-- RailJet (Train-Office) - Seed / demo data script for SQL Server
-- Idempotent: safe to re-run any time, only inserts rows that are missing.
-- Run this AFTER the Spring Boot app has started at least once so that
-- Hibernate (spring.jpa.hibernate.ddl-auto=update) has created/updated
-- every table for you. This script only INSERTs data, it does not try
-- to redefine your schema (except for one safety-net patch below).
-- =====================================================================

SET NOCOUNT ON;

-- ---------------------------------------------------------------------
-- 0. Safety net for bookings.departure_station_id / arrival_station_id
--    Hibernate normally adds these columns by itself once the Booking
--    entity has the @JoinColumn fields. This block only fires if, for
--    some reason, the app has not been booted against this DB yet.
-- ---------------------------------------------------------------------
IF COL_LENGTH('dbo.bookings', 'departure_station_id') IS NULL
    BEGIN
        ALTER TABLE dbo.bookings ADD departure_station_id BIGINT NULL;
    END;

IF COL_LENGTH('dbo.bookings', 'arrival_station_id') IS NULL
    BEGIN
        ALTER TABLE dbo.bookings ADD arrival_station_id BIGINT NULL;
    END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'fk_bookings_departure_station' AND parent_object_id = OBJECT_ID('dbo.bookings'))
    BEGIN
        ALTER TABLE dbo.bookings ADD CONSTRAINT fk_bookings_departure_station FOREIGN KEY (departure_station_id) REFERENCES dbo.stations(station_id);
    END;

IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'fk_bookings_arrival_station' AND parent_object_id = OBJECT_ID('dbo.bookings'))
    BEGIN
        ALTER TABLE dbo.bookings ADD CONSTRAINT fk_bookings_arrival_station FOREIGN KEY (arrival_station_id) REFERENCES dbo.stations(station_id);
    END;

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_bookings_departure_station' AND object_id = OBJECT_ID('dbo.bookings'))
CREATE INDEX idx_bookings_departure_station ON dbo.bookings(departure_station_id);

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_bookings_arrival_station' AND object_id = OBJECT_ID('dbo.bookings'))
CREATE INDEX idx_bookings_arrival_station ON dbo.bookings(arrival_station_id);

-- NOTE: the old "passengers.full_name -> NVARCHAR" ALTER block was removed
-- from this script because DatabaseEncodingInitializer.java already does
-- that exact check/fix automatically every time the app starts.

-- ---------------------------------------------------------------------
-- 1. Users (1 admin + 4 customers, mixed statuses)
-- ---------------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@gmail.local')
    INSERT INTO users (email, password, full_name, identity_number, date_of_birth, gender, role, status, created_at, updated_at)
    VALUES ('admin@gmail.local', '123', N'Quản trị viên', '000000000001', '1990-01-01', 'MALE', 'ADMIN', 'ACTIVE', GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'customer1@railjet.local')
    INSERT INTO users (email, password, full_name, identity_number, date_of_birth, gender, role, status, created_at, updated_at)
    VALUES ('customer1@railjet.local', '123', N'Nguyễn Văn An', '012345678901', '1998-02-10', 'MALE', 'CUSTOMER', 'ACTIVE', GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'customer2@railjet.local')
    INSERT INTO users (email, password, full_name, identity_number, date_of_birth, gender, role, status, created_at, updated_at)
    VALUES ('customer2@railjet.local', '123', N'Trần Thị Bình', '012345678902', '1999-03-12', 'FEMALE', 'CUSTOMER', 'ACTIVE', GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'customer3@railjet.local')
    INSERT INTO users (email, password, full_name, identity_number, date_of_birth, gender, role, status, created_at, updated_at)
    VALUES ('customer3@railjet.local', '123', N'Lê Văn Cường', '012345678903', '2000-04-18', 'MALE', 'CUSTOMER', 'ACTIVE', GETDATE(), GETDATE());

IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'customer4@railjet.local')
    INSERT INTO users (email, password, full_name, identity_number, date_of_birth, gender, role, status, created_at, updated_at)
    VALUES ('customer4@railjet.local', '123', N'Phạm Thị Dung', '012345678904', '1995-07-22', 'FEMALE', 'CUSTOMER', 'LOCKED', GETDATE(), GETDATE());

-- ---------------------------------------------------------------------
-- 2. Stations (North -> South line, 8 stops)
-- ---------------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM stations WHERE station_code='HAN') INSERT INTO stations (station_code, station_name, city) VALUES ('HAN', N'Hà Nội', N'Hà Nội');
IF NOT EXISTS (SELECT 1 FROM stations WHERE station_code='NDH') INSERT INTO stations (station_code, station_name, city) VALUES ('NDH', N'Nam Định', N'Nam Định');
IF NOT EXISTS (SELECT 1 FROM stations WHERE station_code='VIN') INSERT INTO stations (station_code, station_name, city) VALUES ('VIN', N'Vinh', N'Nghệ An');
IF NOT EXISTS (SELECT 1 FROM stations WHERE station_code='DHO') INSERT INTO stations (station_code, station_name, city) VALUES ('DHO', N'Đồng Hới', N'Quảng Bình');
IF NOT EXISTS (SELECT 1 FROM stations WHERE station_code='HUE') INSERT INTO stations (station_code, station_name, city) VALUES ('HUE', N'Huế', N'Thừa Thiên Huế');
IF NOT EXISTS (SELECT 1 FROM stations WHERE station_code='DAD') INSERT INTO stations (station_code, station_name, city) VALUES ('DAD', N'Đà Nẵng', N'Đà Nẵng');
IF NOT EXISTS (SELECT 1 FROM stations WHERE station_code='NTR') INSERT INTO stations (station_code, station_name, city) VALUES ('NTR', N'Nha Trang', N'Khánh Hòa');
IF NOT EXISTS (SELECT 1 FROM stations WHERE station_code='SGN') INSERT INTO stations (station_code, station_name, city) VALUES ('SGN', N'Sài Gòn', N'TP. Hồ Chí Minh');

-- ---------------------------------------------------------------------
-- 3. Routes + route stations
-- ---------------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM routes WHERE route_code='R001') INSERT INTO routes (route_code, route_name, distance_km) VALUES ('R001', N'Hà Nội - Sài Gòn', 1726);
IF NOT EXISTS (SELECT 1 FROM routes WHERE route_code='R002') INSERT INTO routes (route_code, route_name, distance_km) VALUES ('R002', N'Hà Nội - Đà Nẵng', 791);

DECLARE @route_id BIGINT;

SET @route_id = (SELECT route_id FROM routes WHERE route_code='R001');
IF NOT EXISTS (SELECT 1 FROM route_stations WHERE route_id=@route_id AND station_order=1) INSERT INTO route_stations (route_id, station_id, station_order) VALUES (@route_id, (SELECT station_id FROM stations WHERE station_code='HAN'), 1);
IF NOT EXISTS (SELECT 1 FROM route_stations WHERE route_id=@route_id AND station_order=2) INSERT INTO route_stations (route_id, station_id, station_order) VALUES (@route_id, (SELECT station_id FROM stations WHERE station_code='NDH'), 2);
IF NOT EXISTS (SELECT 1 FROM route_stations WHERE route_id=@route_id AND station_order=3) INSERT INTO route_stations (route_id, station_id, station_order) VALUES (@route_id, (SELECT station_id FROM stations WHERE station_code='VIN'), 3);
IF NOT EXISTS (SELECT 1 FROM route_stations WHERE route_id=@route_id AND station_order=4) INSERT INTO route_stations (route_id, station_id, station_order) VALUES (@route_id, (SELECT station_id FROM stations WHERE station_code='DHO'), 4);
IF NOT EXISTS (SELECT 1 FROM route_stations WHERE route_id=@route_id AND station_order=5) INSERT INTO route_stations (route_id, station_id, station_order) VALUES (@route_id, (SELECT station_id FROM stations WHERE station_code='HUE'), 5);
IF NOT EXISTS (SELECT 1 FROM route_stations WHERE route_id=@route_id AND station_order=6) INSERT INTO route_stations (route_id, station_id, station_order) VALUES (@route_id, (SELECT station_id FROM stations WHERE station_code='DAD'), 6);
IF NOT EXISTS (SELECT 1 FROM route_stations WHERE route_id=@route_id AND station_order=7) INSERT INTO route_stations (route_id, station_id, station_order) VALUES (@route_id, (SELECT station_id FROM stations WHERE station_code='NTR'), 7);
IF NOT EXISTS (SELECT 1 FROM route_stations WHERE route_id=@route_id AND station_order=8) INSERT INTO route_stations (route_id, station_id, station_order) VALUES (@route_id, (SELECT station_id FROM stations WHERE station_code='SGN'), 8);

SET @route_id = (SELECT route_id FROM routes WHERE route_code='R002');
IF NOT EXISTS (SELECT 1 FROM route_stations WHERE route_id=@route_id AND station_order=1) INSERT INTO route_stations (route_id, station_id, station_order) VALUES (@route_id, (SELECT station_id FROM stations WHERE station_code='HAN'), 1);
IF NOT EXISTS (SELECT 1 FROM route_stations WHERE route_id=@route_id AND station_order=2) INSERT INTO route_stations (route_id, station_id, station_order) VALUES (@route_id, (SELECT station_id FROM stations WHERE station_code='NDH'), 2);
IF NOT EXISTS (SELECT 1 FROM route_stations WHERE route_id=@route_id AND station_order=3) INSERT INTO route_stations (route_id, station_id, station_order) VALUES (@route_id, (SELECT station_id FROM stations WHERE station_code='VIN'), 3);
IF NOT EXISTS (SELECT 1 FROM route_stations WHERE route_id=@route_id AND station_order=4) INSERT INTO route_stations (route_id, station_id, station_order) VALUES (@route_id, (SELECT station_id FROM stations WHERE station_code='DHO'), 4);
IF NOT EXISTS (SELECT 1 FROM route_stations WHERE route_id=@route_id AND station_order=5) INSERT INTO route_stations (route_id, station_id, station_order) VALUES (@route_id, (SELECT station_id FROM stations WHERE station_code='HUE'), 5);
IF NOT EXISTS (SELECT 1 FROM route_stations WHERE route_id=@route_id AND station_order=6) INSERT INTO route_stations (route_id, station_id, station_order) VALUES (@route_id, (SELECT station_id FROM stations WHERE station_code='DAD'), 6);

-- ---------------------------------------------------------------------
-- 4. Trains
-- ---------------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM trains WHERE train_code='SE1')  INSERT INTO trains (train_code, train_name, train_type, status) VALUES ('SE1',  N'SE1 - Hà Nội - Sài Gòn',  'EXPRESS',  'AVAILABLE');
IF NOT EXISTS (SELECT 1 FROM trains WHERE train_code='SE3')  INSERT INTO trains (train_code, train_name, train_type, status) VALUES ('SE3',  N'SE3 - Hà Nội - Sài Gòn',  'EXPRESS',  'AVAILABLE');
IF NOT EXISTS (SELECT 1 FROM trains WHERE train_code='SE19') INSERT INTO trains (train_code, train_name, train_type, status) VALUES ('SE19', N'SE19 - Hà Nội - Đà Nẵng', 'REGIONAL', 'ON_TRIP');
IF NOT EXISTS (SELECT 1 FROM trains WHERE train_code='TN1')  INSERT INTO trains (train_code, train_name, train_type, status) VALUES ('TN1',  N'TN1 - Thống Nhất',        'EXPRESS',  'MAINTENANCE');

-- ---------------------------------------------------------------------
-- 5. Seat types (lookup list used by the Seat Type admin screen)
-- ---------------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM seat_types WHERE name=N'WINDOW') INSERT INTO seat_types (name, price_per_km) VALUES (N'WINDOW', 1.10);
IF NOT EXISTS (SELECT 1 FROM seat_types WHERE name=N'AISLE')  INSERT INTO seat_types (name, price_per_km) VALUES (N'AISLE', 1.00);
IF NOT EXISTS (SELECT 1 FROM seat_types WHERE name=N'MIDDLE') INSERT INTO seat_types (name, price_per_km) VALUES (N'MIDDLE', 0.95);

-- ---------------------------------------------------------------------
-- 6. Coaches + seats per train
-- ---------------------------------------------------------------------
DECLARE @train_id BIGINT, @coach_id BIGINT;

-- SE1: 3 coaches (soft seat, hard seat, sleeper)
SET @train_id = (SELECT train_id FROM trains WHERE train_code='SE1');

IF NOT EXISTS (SELECT 1 FROM coaches WHERE train_id=@train_id AND coach_number='C01') INSERT INTO coaches (train_id, coach_number, coach_type, capacity) VALUES (@train_id, 'C01', 'SOFT_SEAT', 56);
SET @coach_id = (SELECT coach_id FROM coaches WHERE train_id=@train_id AND coach_number='C01');
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A1') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A1', 'WINDOW', 20000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A2') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A2', 'AISLE', 15000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A3') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A3', 'MIDDLE', 10000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A4') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A4', 'WINDOW', 20000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='B1') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'B1', 'AISLE', 15000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='B2') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'B2', 'WINDOW', 20000);

IF NOT EXISTS (SELECT 1 FROM coaches WHERE train_id=@train_id AND coach_number='C02') INSERT INTO coaches (train_id, coach_number, coach_type, capacity) VALUES (@train_id, 'C02', 'HARD_SEAT', 64);
SET @coach_id = (SELECT coach_id FROM coaches WHERE train_id=@train_id AND coach_number='C02');
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A1') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A1', 'WINDOW', 15000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A2') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A2', 'AISLE', 10000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A3') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A3', 'MIDDLE', 8000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A4') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A4', 'WINDOW', 15000);

IF NOT EXISTS (SELECT 1 FROM coaches WHERE train_id=@train_id AND coach_number='C03') INSERT INTO coaches (train_id, coach_number, coach_type, capacity) VALUES (@train_id, 'C03', 'SLEEPER', 28);
SET @coach_id = (SELECT coach_id FROM coaches WHERE train_id=@train_id AND coach_number='C03');
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='T1') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'T1', 'WINDOW', 50000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='T2') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'T2', 'AISLE', 45000);

-- SE3: 2 coaches (soft seat, sleeper)
SET @train_id = (SELECT train_id FROM trains WHERE train_code='SE3');

IF NOT EXISTS (SELECT 1 FROM coaches WHERE train_id=@train_id AND coach_number='C01') INSERT INTO coaches (train_id, coach_number, coach_type, capacity) VALUES (@train_id, 'C01', 'SOFT_SEAT', 56);
SET @coach_id = (SELECT coach_id FROM coaches WHERE train_id=@train_id AND coach_number='C01');
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A1') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A1', 'WINDOW', 20000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A2') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A2', 'AISLE', 15000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A3') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A3', 'MIDDLE', 10000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A4') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A4', 'WINDOW', 20000);

IF NOT EXISTS (SELECT 1 FROM coaches WHERE train_id=@train_id AND coach_number='C02') INSERT INTO coaches (train_id, coach_number, coach_type, capacity) VALUES (@train_id, 'C02', 'SLEEPER', 28);
SET @coach_id = (SELECT coach_id FROM coaches WHERE train_id=@train_id AND coach_number='C02');
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='T1') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'T1', 'WINDOW', 50000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='T2') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'T2', 'AISLE', 45000);

-- SE19: 1 coach (soft seat)
SET @train_id = (SELECT train_id FROM trains WHERE train_code='SE19');
IF NOT EXISTS (SELECT 1 FROM coaches WHERE train_id=@train_id AND coach_number='C01') INSERT INTO coaches (train_id, coach_number, coach_type, capacity) VALUES (@train_id, 'C01', 'SOFT_SEAT', 56);
SET @coach_id = (SELECT coach_id FROM coaches WHERE train_id=@train_id AND coach_number='C01');
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A1') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A1', 'WINDOW', 18000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A2') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A2', 'AISLE', 13000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A3') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A3', 'MIDDLE', 9000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A4') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A4', 'WINDOW', 18000);

-- TN1: under maintenance, 1 small coach just so it shows up with data too
SET @train_id = (SELECT train_id FROM trains WHERE train_code='TN1');
IF NOT EXISTS (SELECT 1 FROM coaches WHERE train_id=@train_id AND coach_number='C01') INSERT INTO coaches (train_id, coach_number, coach_type, capacity) VALUES (@train_id, 'C01', 'SOFT_SEAT', 56);
SET @coach_id = (SELECT coach_id FROM coaches WHERE train_id=@train_id AND coach_number='C01');
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A1') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A1', 'WINDOW', 20000);
IF NOT EXISTS (SELECT 1 FROM seats WHERE coach_id=@coach_id AND seat_number='A2') INSERT INTO seats (coach_id, seat_number, seat_type, extra_price) VALUES (@coach_id, 'A2', 'AISLE', 15000);

-- ---------------------------------------------------------------------
-- 7. Train trips (8 trips: 1 completed, 6 upcoming/scheduled, 1 cancelled)
-- ---------------------------------------------------------------------
DECLARE @route1_id BIGINT = (SELECT route_id FROM routes WHERE route_code='R001');
DECLARE @route2_id BIGINT = (SELECT route_id FROM routes WHERE route_code='R002');
DECLARE @se1_id  BIGINT = (SELECT train_id FROM trains WHERE train_code='SE1');
DECLARE @se3_id  BIGINT = (SELECT train_id FROM trains WHERE train_code='SE3');
DECLARE @se19_id BIGINT = (SELECT train_id FROM trains WHERE train_code='SE19');

DECLARE @today DATETIME = CAST(CAST(GETDATE() AS date) AS datetime);
DECLARE @depA DATETIME = DATEADD(HOUR, 6,  DATEADD(DAY, -1, @today));  -- yesterday  06:00 - SE1 / R001 (completed)
DECLARE @depB DATETIME = DATEADD(HOUR, 19, @today);                   -- today      19:00 - SE1 / R001
DECLARE @depC DATETIME = DATEADD(HOUR, 6,  DATEADD(DAY, 1, @today));   -- tomorrow   06:00 - SE3 / R001
DECLARE @depD DATETIME = DATEADD(HOUR, 19, DATEADD(DAY, 3, @today));   -- +3 days    19:00 - SE3 / R001
DECLARE @depE DATETIME = DATEADD(HOUR, 8,  @today);                   -- today      08:00 - SE19 / R002
DECLARE @depF DATETIME = DATEADD(HOUR, 8,  DATEADD(DAY, 2, @today));   -- +2 days    08:00 - SE19 / R002
DECLARE @depG DATETIME = DATEADD(HOUR, 6,  DATEADD(DAY, 7, @today));   -- +7 days    06:00 - SE1 / R001
DECLARE @depH DATETIME = DATEADD(HOUR, 14, DATEADD(DAY, 1, @today));   -- +1 day     14:00 - SE3 / R002 (cancelled)

IF NOT EXISTS (SELECT 1 FROM train_trips WHERE train_id=@se1_id AND route_id=@route1_id AND departure_time=@depA)
    INSERT INTO train_trips (train_id, route_id, departure_time, arrival_time, base_price, status) VALUES (@se1_id, @route1_id, @depA, DATEADD(MINUTE, 1790, @depA), 850000, 'COMPLETED');

IF NOT EXISTS (SELECT 1 FROM train_trips WHERE train_id=@se1_id AND route_id=@route1_id AND departure_time=@depB)
    INSERT INTO train_trips (train_id, route_id, departure_time, arrival_time, base_price, status) VALUES (@se1_id, @route1_id, @depB, DATEADD(MINUTE, 1790, @depB), 880000, 'SCHEDULED');

IF NOT EXISTS (SELECT 1 FROM train_trips WHERE train_id=@se3_id AND route_id=@route1_id AND departure_time=@depC)
    INSERT INTO train_trips (train_id, route_id, departure_time, arrival_time, base_price, status) VALUES (@se3_id, @route1_id, @depC, DATEADD(MINUTE, 1790, @depC), 870000, 'SCHEDULED');

IF NOT EXISTS (SELECT 1 FROM train_trips WHERE train_id=@se3_id AND route_id=@route1_id AND departure_time=@depD)
    INSERT INTO train_trips (train_id, route_id, departure_time, arrival_time, base_price, status) VALUES (@se3_id, @route1_id, @depD, DATEADD(MINUTE, 1790, @depD), 890000, 'SCHEDULED');

IF NOT EXISTS (SELECT 1 FROM train_trips WHERE train_id=@se19_id AND route_id=@route2_id AND departure_time=@depE)
    INSERT INTO train_trips (train_id, route_id, departure_time, arrival_time, base_price, status) VALUES (@se19_id, @route2_id, @depE, DATEADD(MINUTE, 760, @depE), 450000, 'SCHEDULED');

IF NOT EXISTS (SELECT 1 FROM train_trips WHERE train_id=@se19_id AND route_id=@route2_id AND departure_time=@depF)
    INSERT INTO train_trips (train_id, route_id, departure_time, arrival_time, base_price, status) VALUES (@se19_id, @route2_id, @depF, DATEADD(MINUTE, 760, @depF), 460000, 'SCHEDULED');

IF NOT EXISTS (SELECT 1 FROM train_trips WHERE train_id=@se1_id AND route_id=@route1_id AND departure_time=@depG)
    INSERT INTO train_trips (train_id, route_id, departure_time, arrival_time, base_price, status) VALUES (@se1_id, @route1_id, @depG, DATEADD(MINUTE, 1790, @depG), 900000, 'SCHEDULED');

IF NOT EXISTS (SELECT 1 FROM train_trips WHERE train_id=@se3_id AND route_id=@route2_id AND departure_time=@depH)
    INSERT INTO train_trips (train_id, route_id, departure_time, arrival_time, base_price, status) VALUES (@se3_id, @route2_id, @depH, DATEADD(MINUTE, 760, @depH), 470000, 'CANCELLED');

-- ---------------------------------------------------------------------
-- 8. Trip stations (intermediate stops for each trip above)
-- ---------------------------------------------------------------------
DECLARE @trip_id BIGINT;

-- Trip A stations
SET @trip_id = (SELECT trip_id FROM train_trips WHERE train_id=@se1_id AND route_id=@route1_id AND departure_time=@depA);
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=1) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='HAN'), 1, NULL, DATEADD(MINUTE,0,@depA));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=2) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='NDH'), 2, DATEADD(MINUTE,130,@depA), DATEADD(MINUTE,135,@depA));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=3) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='VIN'), 3, DATEADD(MINUTE,330,@depA), DATEADD(MINUTE,335,@depA));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=4) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='DHO'), 4, DATEADD(MINUTE,540,@depA), DATEADD(MINUTE,545,@depA));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=5) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='HUE'), 5, DATEADD(MINUTE,700,@depA), DATEADD(MINUTE,705,@depA));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=6) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='DAD'), 6, DATEADD(MINUTE,760,@depA), DATEADD(MINUTE,765,@depA));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=7) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='NTR'), 7, DATEADD(MINUTE,1400,@depA), DATEADD(MINUTE,1405,@depA));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=8) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='SGN'), 8, DATEADD(MINUTE,1790,@depA), NULL);

-- Trip B stations
SET @trip_id = (SELECT trip_id FROM train_trips WHERE train_id=@se1_id AND route_id=@route1_id AND departure_time=@depB);
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=1) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='HAN'), 1, NULL, DATEADD(MINUTE,0,@depB));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=2) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='NDH'), 2, DATEADD(MINUTE,130,@depB), DATEADD(MINUTE,135,@depB));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=3) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='VIN'), 3, DATEADD(MINUTE,330,@depB), DATEADD(MINUTE,335,@depB));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=4) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='DHO'), 4, DATEADD(MINUTE,540,@depB), DATEADD(MINUTE,545,@depB));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=5) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='HUE'), 5, DATEADD(MINUTE,700,@depB), DATEADD(MINUTE,705,@depB));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=6) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='DAD'), 6, DATEADD(MINUTE,760,@depB), DATEADD(MINUTE,765,@depB));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=7) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='NTR'), 7, DATEADD(MINUTE,1400,@depB), DATEADD(MINUTE,1405,@depB));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=8) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='SGN'), 8, DATEADD(MINUTE,1790,@depB), NULL);

-- Trip C stations
SET @trip_id = (SELECT trip_id FROM train_trips WHERE train_id=@se3_id AND route_id=@route1_id AND departure_time=@depC);
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=1) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='HAN'), 1, NULL, DATEADD(MINUTE,0,@depC));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=2) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='NDH'), 2, DATEADD(MINUTE,130,@depC), DATEADD(MINUTE,135,@depC));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=3) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='VIN'), 3, DATEADD(MINUTE,330,@depC), DATEADD(MINUTE,335,@depC));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=4) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='DHO'), 4, DATEADD(MINUTE,540,@depC), DATEADD(MINUTE,545,@depC));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=5) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='HUE'), 5, DATEADD(MINUTE,700,@depC), DATEADD(MINUTE,705,@depC));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=6) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='DAD'), 6, DATEADD(MINUTE,760,@depC), DATEADD(MINUTE,765,@depC));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=7) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='NTR'), 7, DATEADD(MINUTE,1400,@depC), DATEADD(MINUTE,1405,@depC));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=8) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='SGN'), 8, DATEADD(MINUTE,1790,@depC), NULL);

-- Trip D stations
SET @trip_id = (SELECT trip_id FROM train_trips WHERE train_id=@se3_id AND route_id=@route1_id AND departure_time=@depD);
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=1) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='HAN'), 1, NULL, DATEADD(MINUTE,0,@depD));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=2) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='NDH'), 2, DATEADD(MINUTE,130,@depD), DATEADD(MINUTE,135,@depD));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=3) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='VIN'), 3, DATEADD(MINUTE,330,@depD), DATEADD(MINUTE,335,@depD));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=4) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='DHO'), 4, DATEADD(MINUTE,540,@depD), DATEADD(MINUTE,545,@depD));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=5) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='HUE'), 5, DATEADD(MINUTE,700,@depD), DATEADD(MINUTE,705,@depD));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=6) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='DAD'), 6, DATEADD(MINUTE,760,@depD), DATEADD(MINUTE,765,@depD));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=7) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='NTR'), 7, DATEADD(MINUTE,1400,@depD), DATEADD(MINUTE,1405,@depD));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=8) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='SGN'), 8, DATEADD(MINUTE,1790,@depD), NULL);

-- Trip G stations
SET @trip_id = (SELECT trip_id FROM train_trips WHERE train_id=@se1_id AND route_id=@route1_id AND departure_time=@depG);
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=1) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='HAN'), 1, NULL, DATEADD(MINUTE,0,@depG));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=2) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='NDH'), 2, DATEADD(MINUTE,130,@depG), DATEADD(MINUTE,135,@depG));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=3) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='VIN'), 3, DATEADD(MINUTE,330,@depG), DATEADD(MINUTE,335,@depG));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=4) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='DHO'), 4, DATEADD(MINUTE,540,@depG), DATEADD(MINUTE,545,@depG));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=5) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='HUE'), 5, DATEADD(MINUTE,700,@depG), DATEADD(MINUTE,705,@depG));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=6) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='DAD'), 6, DATEADD(MINUTE,760,@depG), DATEADD(MINUTE,765,@depG));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=7) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='NTR'), 7, DATEADD(MINUTE,1400,@depG), DATEADD(MINUTE,1405,@depG));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=8) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='SGN'), 8, DATEADD(MINUTE,1790,@depG), NULL);

-- Trip E stations
SET @trip_id = (SELECT trip_id FROM train_trips WHERE train_id=@se19_id AND route_id=@route2_id AND departure_time=@depE);
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=1) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='HAN'), 1, NULL, DATEADD(MINUTE,0,@depE));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=2) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='NDH'), 2, DATEADD(MINUTE,130,@depE), DATEADD(MINUTE,135,@depE));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=3) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='VIN'), 3, DATEADD(MINUTE,330,@depE), DATEADD(MINUTE,335,@depE));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=4) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='DHO'), 4, DATEADD(MINUTE,540,@depE), DATEADD(MINUTE,545,@depE));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=5) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='HUE'), 5, DATEADD(MINUTE,700,@depE), DATEADD(MINUTE,705,@depE));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=6) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='DAD'), 6, DATEADD(MINUTE,760,@depE), NULL);

-- Trip F stations
SET @trip_id = (SELECT trip_id FROM train_trips WHERE train_id=@se19_id AND route_id=@route2_id AND departure_time=@depF);
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=1) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='HAN'), 1, NULL, DATEADD(MINUTE,0,@depF));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=2) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='NDH'), 2, DATEADD(MINUTE,130,@depF), DATEADD(MINUTE,135,@depF));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=3) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='VIN'), 3, DATEADD(MINUTE,330,@depF), DATEADD(MINUTE,335,@depF));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=4) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='DHO'), 4, DATEADD(MINUTE,540,@depF), DATEADD(MINUTE,545,@depF));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=5) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='HUE'), 5, DATEADD(MINUTE,700,@depF), DATEADD(MINUTE,705,@depF));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=6) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='DAD'), 6, DATEADD(MINUTE,760,@depF), NULL);

-- Trip H stations
SET @trip_id = (SELECT trip_id FROM train_trips WHERE train_id=@se3_id AND route_id=@route2_id AND departure_time=@depH);
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=1) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='HAN'), 1, NULL, DATEADD(MINUTE,0,@depH));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=2) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='NDH'), 2, DATEADD(MINUTE,130,@depH), DATEADD(MINUTE,135,@depH));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=3) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='VIN'), 3, DATEADD(MINUTE,330,@depH), DATEADD(MINUTE,335,@depH));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=4) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='DHO'), 4, DATEADD(MINUTE,540,@depH), DATEADD(MINUTE,545,@depH));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=5) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='HUE'), 5, DATEADD(MINUTE,700,@depH), DATEADD(MINUTE,705,@depH));
IF NOT EXISTS (SELECT 1 FROM trip_stations WHERE trip_id=@trip_id AND station_order=6) INSERT INTO trip_stations (trip_id, station_id, station_order, arrival_time, departure_time) VALUES (@trip_id, (SELECT station_id FROM stations WHERE station_code='DAD'), 6, DATEADD(MINUTE,760,@depH), NULL);
-- ---------------------------------------------------------------------
-- 9. Bookings, passengers & tickets
--    10 bookings covering every BookingStatus / TicketStatus value so
--    every list/detail screen has something interesting to show.
-- ---------------------------------------------------------------------
DECLARE @han_id BIGINT = (SELECT station_id FROM stations WHERE station_code='HAN');
DECLARE @ndh_id BIGINT = (SELECT station_id FROM stations WHERE station_code='NDH');
DECLARE @vin_id BIGINT = (SELECT station_id FROM stations WHERE station_code='VIN');
DECLARE @hue_id BIGINT = (SELECT station_id FROM stations WHERE station_code='HUE');
DECLARE @dad_id BIGINT = (SELECT station_id FROM stations WHERE station_code='DAD');
DECLARE @ntr_id BIGINT = (SELECT station_id FROM stations WHERE station_code='NTR');
DECLARE @sgn_id BIGINT = (SELECT station_id FROM stations WHERE station_code='SGN');

DECLARE @u_customer1 BIGINT = (SELECT user_id FROM users WHERE email='customer1@railjet.local');
DECLARE @u_customer2 BIGINT = (SELECT user_id FROM users WHERE email='customer2@railjet.local');
DECLARE @u_customer3 BIGINT = (SELECT user_id FROM users WHERE email='customer3@railjet.local');
DECLARE @u_customer4 BIGINT = (SELECT user_id FROM users WHERE email='customer4@railjet.local');

DECLARE @trip_a BIGINT = (SELECT trip_id FROM train_trips WHERE train_id=@se1_id  AND route_id=@route1_id AND departure_time=@depA);
DECLARE @trip_b BIGINT = (SELECT trip_id FROM train_trips WHERE train_id=@se1_id  AND route_id=@route1_id AND departure_time=@depB);
DECLARE @trip_c BIGINT = (SELECT trip_id FROM train_trips WHERE train_id=@se3_id  AND route_id=@route1_id AND departure_time=@depC);
DECLARE @trip_d BIGINT = (SELECT trip_id FROM train_trips WHERE train_id=@se3_id  AND route_id=@route1_id AND departure_time=@depD);
DECLARE @trip_e BIGINT = (SELECT trip_id FROM train_trips WHERE train_id=@se19_id AND route_id=@route2_id AND departure_time=@depE);
DECLARE @trip_f BIGINT = (SELECT trip_id FROM train_trips WHERE train_id=@se19_id AND route_id=@route2_id AND departure_time=@depF);
DECLARE @trip_g BIGINT = (SELECT trip_id FROM train_trips WHERE train_id=@se1_id  AND route_id=@route1_id AND departure_time=@depG);
DECLARE @trip_h BIGINT = (SELECT trip_id FROM train_trips WHERE train_id=@se3_id  AND route_id=@route2_id AND departure_time=@depH);

DECLARE @seat_se1c01_a1 BIGINT = (SELECT seat_id FROM seats WHERE coach_id=(SELECT coach_id FROM coaches WHERE train_id=@se1_id AND coach_number='C01') AND seat_number='A1');
DECLARE @seat_se1c01_a2 BIGINT = (SELECT seat_id FROM seats WHERE coach_id=(SELECT coach_id FROM coaches WHERE train_id=@se1_id AND coach_number='C01') AND seat_number='A2');
DECLARE @seat_se1c01_a3 BIGINT = (SELECT seat_id FROM seats WHERE coach_id=(SELECT coach_id FROM coaches WHERE train_id=@se1_id AND coach_number='C01') AND seat_number='A3');
DECLARE @seat_se1c01_a4 BIGINT = (SELECT seat_id FROM seats WHERE coach_id=(SELECT coach_id FROM coaches WHERE train_id=@se1_id AND coach_number='C01') AND seat_number='A4');
DECLARE @seat_se1c01_b1 BIGINT = (SELECT seat_id FROM seats WHERE coach_id=(SELECT coach_id FROM coaches WHERE train_id=@se1_id AND coach_number='C01') AND seat_number='B1');
DECLARE @seat_se1c02_a1 BIGINT = (SELECT seat_id FROM seats WHERE coach_id=(SELECT coach_id FROM coaches WHERE train_id=@se1_id AND coach_number='C02') AND seat_number='A1');
DECLARE @seat_se3c01_a1 BIGINT = (SELECT seat_id FROM seats WHERE coach_id=(SELECT coach_id FROM coaches WHERE train_id=@se3_id AND coach_number='C01') AND seat_number='A1');
DECLARE @seat_se3c01_a2 BIGINT = (SELECT seat_id FROM seats WHERE coach_id=(SELECT coach_id FROM coaches WHERE train_id=@se3_id AND coach_number='C01') AND seat_number='A2');
DECLARE @seat_se3c01_a3 BIGINT = (SELECT seat_id FROM seats WHERE coach_id=(SELECT coach_id FROM coaches WHERE train_id=@se3_id AND coach_number='C01') AND seat_number='A3');
DECLARE @seat_se3c02_t1 BIGINT = (SELECT seat_id FROM seats WHERE coach_id=(SELECT coach_id FROM coaches WHERE train_id=@se3_id AND coach_number='C02') AND seat_number='T1');
DECLARE @seat_se19c01_a1 BIGINT = (SELECT seat_id FROM seats WHERE coach_id=(SELECT coach_id FROM coaches WHERE train_id=@se19_id AND coach_number='C01') AND seat_number='A1');
DECLARE @seat_se19c01_a2 BIGINT = (SELECT seat_id FROM seats WHERE coach_id=(SELECT coach_id FROM coaches WHERE train_id=@se19_id AND coach_number='C01') AND seat_number='A2');

DECLARE @booking_id BIGINT, @passenger_id BIGINT;

-- BK0001 - Trip A (completed), VIN -> DAD, 2 passengers, PAID
IF NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK0001')
    INSERT INTO bookings (booking_code, trip_id, departure_station_id, arrival_station_id, user_id, booker_name, booker_phone, booker_email, total_amount, booking_status, created_at)
    VALUES ('BK0001', @trip_a, @vin_id, @dad_id, @u_customer1, N'Nguyễn Văn An', '0901111111', 'customer1@railjet.local', 950000, 'PAID', DATEADD(DAY,-2,@depA));
SET @booking_id = (SELECT booking_id FROM bookings WHERE booking_code='BK0001');

IF NOT EXISTS (SELECT 1 FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678901')
    INSERT INTO passengers (booking_id, full_name, identity_number, date_of_birth, gender) VALUES (@booking_id, N'Nguyễn Văn An', '012345678901', '1998-02-10', 'MALE');
IF NOT EXISTS (SELECT 1 FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678905')
    INSERT INTO passengers (booking_id, full_name, identity_number, date_of_birth, gender) VALUES (@booking_id, N'Đỗ Thị Hoa', '012345678905', '1997-05-20', 'FEMALE');

SET @passenger_id = (SELECT passenger_id FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678901');
IF NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_code='TK0001')
    INSERT INTO tickets (ticket_code, booking_id, passenger_id, trip_id, seat_id, ticket_price, ticket_status) VALUES ('TK0001', @booking_id, @passenger_id, @trip_a, @seat_se1c01_a1, 470000, 'CHECKED_IN');

SET @passenger_id = (SELECT passenger_id FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678905');
IF NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_code='TK0002')
    INSERT INTO tickets (ticket_code, booking_id, passenger_id, trip_id, seat_id, ticket_price, ticket_status) VALUES ('TK0002', @booking_id, @passenger_id, @trip_a, @seat_se1c01_a2, 480000, 'EXPIRED');

-- BK0002 - Trip B (today), HAN -> HUE, 1 passenger, PAID
IF NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK0002')
    INSERT INTO bookings (booking_code, trip_id, departure_station_id, arrival_station_id, user_id, booker_name, booker_phone, booker_email, total_amount, booking_status, created_at)
    VALUES ('BK0002', @trip_b, @han_id, @hue_id, @u_customer2, N'Trần Thị Bình', '0902222222', 'customer2@railjet.local', 420000, 'PAID', GETDATE());
SET @booking_id = (SELECT booking_id FROM bookings WHERE booking_code='BK0002');

IF NOT EXISTS (SELECT 1 FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678902')
    INSERT INTO passengers (booking_id, full_name, identity_number, date_of_birth, gender) VALUES (@booking_id, N'Trần Thị Bình', '012345678902', '1999-03-12', 'FEMALE');
SET @passenger_id = (SELECT passenger_id FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678902');
IF NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_code='TK0003')
    INSERT INTO tickets (ticket_code, booking_id, passenger_id, trip_id, seat_id, ticket_price, ticket_status) VALUES ('TK0003', @booking_id, @passenger_id, @trip_b, @seat_se1c01_a3, 420000, 'ACTIVE');

-- BK0003 - Trip C (tomorrow), HAN -> SGN, 2 passengers, PENDING_PAYMENT
IF NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK0003')
    INSERT INTO bookings (booking_code, trip_id, departure_station_id, arrival_station_id, user_id, booker_name, booker_phone, booker_email, total_amount, booking_status, created_at)
    VALUES ('BK0003', @trip_c, @han_id, @sgn_id, @u_customer3, N'Lê Văn Cường', '0903333333', 'customer3@railjet.local', 1910000, 'PENDING_PAYMENT', GETDATE());
SET @booking_id = (SELECT booking_id FROM bookings WHERE booking_code='BK0003');

IF NOT EXISTS (SELECT 1 FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678903')
    INSERT INTO passengers (booking_id, full_name, identity_number, date_of_birth, gender) VALUES (@booking_id, N'Lê Văn Cường', '012345678903', '2000-04-18', 'MALE');
IF NOT EXISTS (SELECT 1 FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678906')
    INSERT INTO passengers (booking_id, full_name, identity_number, date_of_birth, gender) VALUES (@booking_id, N'Vũ Văn Khoa', '012345678906', '1996-11-02', 'MALE');

SET @passenger_id = (SELECT passenger_id FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678903');
IF NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_code='TK0004')
    INSERT INTO tickets (ticket_code, booking_id, passenger_id, trip_id, seat_id, ticket_price, ticket_status) VALUES ('TK0004', @booking_id, @passenger_id, @trip_c, @seat_se3c01_a1, 950000, 'ACTIVE');
SET @passenger_id = (SELECT passenger_id FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678906');
IF NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_code='TK0005')
    INSERT INTO tickets (ticket_code, booking_id, passenger_id, trip_id, seat_id, ticket_price, ticket_status) VALUES ('TK0005', @booking_id, @passenger_id, @trip_c, @seat_se3c01_a2, 960000, 'ACTIVE');

-- BK0004 - Trip E (today), HAN -> DAD, guest booking (no user_id), PAID
IF NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK0004')
    INSERT INTO bookings (booking_code, trip_id, departure_station_id, arrival_station_id, user_id, booker_name, booker_phone, booker_email, total_amount, booking_status, created_at)
    VALUES ('BK0004', @trip_e, @han_id, @dad_id, NULL, N'Hoàng Thị Mai', '0904444444', 'guest1@railjet.local', 468000, 'PAID', GETDATE());
SET @booking_id = (SELECT booking_id FROM bookings WHERE booking_code='BK0004');

IF NOT EXISTS (SELECT 1 FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678907')
    INSERT INTO passengers (booking_id, full_name, identity_number, date_of_birth, gender) VALUES (@booking_id, N'Hoàng Thị Mai', '012345678907', '1994-09-09', 'FEMALE');
SET @passenger_id = (SELECT passenger_id FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678907');
IF NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_code='TK0006')
    INSERT INTO tickets (ticket_code, booking_id, passenger_id, trip_id, seat_id, ticket_price, ticket_status) VALUES ('TK0006', @booking_id, @passenger_id, @trip_e, @seat_se19c01_a1, 468000, 'ACTIVE');

-- BK0005 - Trip D (+3 days), VIN -> NTR, sleeper berth, CANCELLED before payment
IF NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK0005')
    INSERT INTO bookings (booking_code, trip_id, departure_station_id, arrival_station_id, user_id, booker_name, booker_phone, booker_email, total_amount, booking_status, created_at)
    VALUES ('BK0005', @trip_d, @vin_id, @ntr_id, @u_customer1, N'Nguyễn Văn An', '0901111111', 'customer1@railjet.local', 1200000, 'CANCELLED', GETDATE());
SET @booking_id = (SELECT booking_id FROM bookings WHERE booking_code='BK0005');

IF NOT EXISTS (SELECT 1 FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678901')
    INSERT INTO passengers (booking_id, full_name, identity_number, date_of_birth, gender) VALUES (@booking_id, N'Nguyễn Văn An', '012345678901', '1998-02-10', 'MALE');
SET @passenger_id = (SELECT passenger_id FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678901');
IF NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_code='TK0007')
    INSERT INTO tickets (ticket_code, booking_id, passenger_id, trip_id, seat_id, ticket_price, ticket_status) VALUES ('TK0007', @booking_id, @passenger_id, @trip_d, @seat_se3c02_t1, 1200000, 'CANCELLED');

-- BK0006 - Trip B (today), NDH -> HUE, PAID then cancelled -> refund PENDING
IF NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK0006')
    INSERT INTO bookings (booking_code, trip_id, departure_station_id, arrival_station_id, user_id, booker_name, booker_phone, booker_email, total_amount, booking_status, created_at)
    VALUES ('BK0006', @trip_b, @ndh_id, @hue_id, @u_customer2, N'Trần Thị Bình', '0902222222', 'customer2@railjet.local', 460000, 'REFUNDED', GETDATE());
SET @booking_id = (SELECT booking_id FROM bookings WHERE booking_code='BK0006');

IF NOT EXISTS (SELECT 1 FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678902')
    INSERT INTO passengers (booking_id, full_name, identity_number, date_of_birth, gender) VALUES (@booking_id, N'Trần Thị Bình', '012345678902', '1999-03-12', 'FEMALE');
SET @passenger_id = (SELECT passenger_id FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678902');
IF NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_code='TK0008')
    INSERT INTO tickets (ticket_code, booking_id, passenger_id, trip_id, seat_id, ticket_price, ticket_status) VALUES ('TK0008', @booking_id, @passenger_id, @trip_b, @seat_se1c01_a4, 460000, 'CANCELLED');

-- BK0007 - Trip F (+2 days), HAN -> DAD, PAID then cancelled -> refund APPROVED
IF NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK0007')
    INSERT INTO bookings (booking_code, trip_id, departure_station_id, arrival_station_id, user_id, booker_name, booker_phone, booker_email, total_amount, booking_status, created_at)
    VALUES ('BK0007', @trip_f, @han_id, @dad_id, @u_customer3, N'Lê Văn Cường', '0903333333', 'customer3@railjet.local', 463000, 'REFUNDED', GETDATE());
SET @booking_id = (SELECT booking_id FROM bookings WHERE booking_code='BK0007');

IF NOT EXISTS (SELECT 1 FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678903b')
    INSERT INTO passengers (booking_id, full_name, identity_number, date_of_birth, gender) VALUES (@booking_id, N'Lê Văn Cường', '012345678903b', '2000-04-18', 'MALE');
SET @passenger_id = (SELECT passenger_id FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678903b');
IF NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_code='TK0009')
    INSERT INTO tickets (ticket_code, booking_id, passenger_id, trip_id, seat_id, ticket_price, ticket_status) VALUES ('TK0009', @booking_id, @passenger_id, @trip_f, @seat_se19c01_a2, 463000, 'CANCELLED');

-- BK0008 - Trip H (operator-cancelled trip), HAN -> DAD, refund COMPLETED
IF NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK0008')
    INSERT INTO bookings (booking_code, trip_id, departure_station_id, arrival_station_id, user_id, booker_name, booker_phone, booker_email, total_amount, booking_status, created_at)
    VALUES ('BK0008', @trip_h, @han_id, @dad_id, @u_customer4, N'Phạm Thị Dung', '0904555555', 'customer4@railjet.local', 465000, 'REFUNDED', GETDATE());
SET @booking_id = (SELECT booking_id FROM bookings WHERE booking_code='BK0008');

IF NOT EXISTS (SELECT 1 FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678904')
    INSERT INTO passengers (booking_id, full_name, identity_number, date_of_birth, gender) VALUES (@booking_id, N'Phạm Thị Dung', '012345678904', '1995-07-22', 'FEMALE');
SET @passenger_id = (SELECT passenger_id FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678904');
IF NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_code='TK0010')
    INSERT INTO tickets (ticket_code, booking_id, passenger_id, trip_id, seat_id, ticket_price, ticket_status) VALUES ('TK0010', @booking_id, @passenger_id, @trip_h, @seat_se3c01_a3, 465000, 'CANCELLED');

-- BK0009 - Trip G (+7 days), HAN -> SGN, guest booking, payment FAILED
IF NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK0009')
    INSERT INTO bookings (booking_code, trip_id, departure_station_id, arrival_station_id, user_id, booker_name, booker_phone, booker_email, total_amount, booking_status, created_at)
    VALUES ('BK0009', @trip_g, @han_id, @sgn_id, NULL, N'Ngô Văn Phúc', '0905666666', 'guest2@railjet.local', 920000, 'PENDING_PAYMENT', GETDATE());
SET @booking_id = (SELECT booking_id FROM bookings WHERE booking_code='BK0009');

IF NOT EXISTS (SELECT 1 FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678908')
    INSERT INTO passengers (booking_id, full_name, identity_number, date_of_birth, gender) VALUES (@booking_id, N'Ngô Văn Phúc', '012345678908', '1993-12-30', 'MALE');
SET @passenger_id = (SELECT passenger_id FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678908');
IF NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_code='TK0011')
    INSERT INTO tickets (ticket_code, booking_id, passenger_id, trip_id, seat_id, ticket_price, ticket_status) VALUES ('TK0011', @booking_id, @passenger_id, @trip_g, @seat_se1c01_b1, 920000, 'ACTIVE');

-- BK0010 - Trip A (completed), HUE -> DAD, hard-seat coach, refund REJECTED
IF NOT EXISTS (SELECT 1 FROM bookings WHERE booking_code='BK0010')
    INSERT INTO bookings (booking_code, trip_id, departure_station_id, arrival_station_id, user_id, booker_name, booker_phone, booker_email, total_amount, booking_status, created_at)
    VALUES ('BK0010', @trip_a, @hue_id, @dad_id, @u_customer1, N'Nguyễn Văn An', '0901111111', 'customer1@railjet.local', 95000, 'REFUNDED', DATEADD(DAY,-3,@depA));
SET @booking_id = (SELECT booking_id FROM bookings WHERE booking_code='BK0010');

IF NOT EXISTS (SELECT 1 FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678901')
    INSERT INTO passengers (booking_id, full_name, identity_number, date_of_birth, gender) VALUES (@booking_id, N'Nguyễn Văn An', '012345678901', '1998-02-10', 'MALE');
SET @passenger_id = (SELECT passenger_id FROM passengers WHERE booking_id=@booking_id AND identity_number='012345678901');
IF NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_code='TK0012')
    INSERT INTO tickets (ticket_code, booking_id, passenger_id, trip_id, seat_id, ticket_price, ticket_status) VALUES ('TK0012', @booking_id, @passenger_id, @trip_a, @seat_se1c02_a1, 95000, 'CANCELLED');

-- ---------------------------------------------------------------------
-- 10. Payments (skips BK0005 - cancelled before any payment was made)
-- ---------------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM payments WHERE transaction_code='PAY-BK0001')
    INSERT INTO payments (booking_id, payment_method, amount, transaction_code, otp_code, payment_status, paid_at)
    VALUES ((SELECT booking_id FROM bookings WHERE booking_code='BK0001'), 'BANK_TRANSFER', 950000, 'PAY-BK0001', '111111', 'SUCCESS', DATEADD(DAY,-2,@depA));

IF NOT EXISTS (SELECT 1 FROM payments WHERE transaction_code='PAY-BK0002')
    INSERT INTO payments (booking_id, payment_method, amount, transaction_code, otp_code, payment_status, paid_at)
    VALUES ((SELECT booking_id FROM bookings WHERE booking_code='BK0002'), 'CASH', 420000, 'PAY-BK0002', NULL, 'SUCCESS', GETDATE());

IF NOT EXISTS (SELECT 1 FROM payments WHERE transaction_code='PAY-BK0003')
    INSERT INTO payments (booking_id, payment_method, amount, transaction_code, otp_code, payment_status)
    VALUES ((SELECT booking_id FROM bookings WHERE booking_code='BK0003'), 'BANK_TRANSFER', 1910000, 'PAY-BK0003', NULL, 'PENDING');

IF NOT EXISTS (SELECT 1 FROM payments WHERE transaction_code='PAY-BK0004')
    INSERT INTO payments (booking_id, payment_method, amount, transaction_code, otp_code, payment_status, paid_at)
    VALUES ((SELECT booking_id FROM bookings WHERE booking_code='BK0004'), 'BANK_TRANSFER', 468000, 'PAY-BK0004', '222222', 'SUCCESS', GETDATE());

IF NOT EXISTS (SELECT 1 FROM payments WHERE transaction_code='PAY-BK0006')
    INSERT INTO payments (booking_id, payment_method, amount, transaction_code, otp_code, payment_status, paid_at)
    VALUES ((SELECT booking_id FROM bookings WHERE booking_code='BK0006'), 'BANK_TRANSFER', 460000, 'PAY-BK0006', '333333', 'SUCCESS', GETDATE());

IF NOT EXISTS (SELECT 1 FROM payments WHERE transaction_code='PAY-BK0007')
    INSERT INTO payments (booking_id, payment_method, amount, transaction_code, otp_code, payment_status, paid_at)
    VALUES ((SELECT booking_id FROM bookings WHERE booking_code='BK0007'), 'CASH', 463000, 'PAY-BK0007', NULL, 'SUCCESS', GETDATE());

IF NOT EXISTS (SELECT 1 FROM payments WHERE transaction_code='PAY-BK0008')
    INSERT INTO payments (booking_id, payment_method, amount, transaction_code, otp_code, payment_status, paid_at)
    VALUES ((SELECT booking_id FROM bookings WHERE booking_code='BK0008'), 'BANK_TRANSFER', 465000, 'PAY-BK0008', '444444', 'SUCCESS', GETDATE());

IF NOT EXISTS (SELECT 1 FROM payments WHERE transaction_code='PAY-BK0009')
    INSERT INTO payments (booking_id, payment_method, amount, transaction_code, otp_code, payment_status)
    VALUES ((SELECT booking_id FROM bookings WHERE booking_code='BK0009'), 'BANK_TRANSFER', 920000, 'PAY-BK0009', NULL, 'FAILED');

IF NOT EXISTS (SELECT 1 FROM payments WHERE transaction_code='PAY-BK0010')
    INSERT INTO payments (booking_id, payment_method, amount, transaction_code, otp_code, payment_status, paid_at)
    VALUES ((SELECT booking_id FROM bookings WHERE booking_code='BK0010'), 'CASH', 95000, 'PAY-BK0010', NULL, 'SUCCESS', DATEADD(DAY,-3,@depA));

-- ---------------------------------------------------------------------
-- 11. Invoices (only for SUCCESSful payments)
-- ---------------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_code='INV0001')
    INSERT INTO invoices (invoice_code, payment_id, total_amount, issued_at) VALUES ('INV0001', (SELECT payment_id FROM payments WHERE transaction_code='PAY-BK0001'), 950000, DATEADD(DAY,-2,@depA));
IF NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_code='INV0002')
    INSERT INTO invoices (invoice_code, payment_id, total_amount, issued_at) VALUES ('INV0002', (SELECT payment_id FROM payments WHERE transaction_code='PAY-BK0002'), 420000, GETDATE());
IF NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_code='INV0003')
    INSERT INTO invoices (invoice_code, payment_id, total_amount, issued_at) VALUES ('INV0003', (SELECT payment_id FROM payments WHERE transaction_code='PAY-BK0004'), 468000, GETDATE());
IF NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_code='INV0004')
    INSERT INTO invoices (invoice_code, payment_id, total_amount, issued_at) VALUES ('INV0004', (SELECT payment_id FROM payments WHERE transaction_code='PAY-BK0006'), 460000, GETDATE());
IF NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_code='INV0005')
    INSERT INTO invoices (invoice_code, payment_id, total_amount, issued_at) VALUES ('INV0005', (SELECT payment_id FROM payments WHERE transaction_code='PAY-BK0007'), 463000, GETDATE());
IF NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_code='INV0006')
    INSERT INTO invoices (invoice_code, payment_id, total_amount, issued_at) VALUES ('INV0006', (SELECT payment_id FROM payments WHERE transaction_code='PAY-BK0008'), 465000, GETDATE());
IF NOT EXISTS (SELECT 1 FROM invoices WHERE invoice_code='INV0007')
    INSERT INTO invoices (invoice_code, payment_id, total_amount, issued_at) VALUES ('INV0007', (SELECT payment_id FROM payments WHERE transaction_code='PAY-BK0010'), 95000, DATEADD(DAY,-3,@depA));

-- ---------------------------------------------------------------------
-- 12. Refunds (one for each RefundStatus value: PENDING / APPROVED / COMPLETED / REJECTED)
-- ---------------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM refunds WHERE refund_code='RF0001')
    INSERT INTO refunds (refund_code, ticket_id, refund_amount, refund_reason, refund_status, refunded_at)
    VALUES ('RF0001', (SELECT ticket_id FROM tickets WHERE ticket_code='TK0008'), 460000, N'Thay đổi kế hoạch công việc', 'PENDING', NULL);

IF NOT EXISTS (SELECT 1 FROM refunds WHERE refund_code='RF0002')
    INSERT INTO refunds (refund_code, ticket_id, refund_amount, refund_reason, refund_status, refunded_at)
    VALUES ('RF0002', (SELECT ticket_id FROM tickets WHERE ticket_code='TK0009'), 440000, N'Đổi lịch trình cá nhân', 'APPROVED', GETDATE());

IF NOT EXISTS (SELECT 1 FROM refunds WHERE refund_code='RF0003')
    INSERT INTO refunds (refund_code, ticket_id, refund_amount, refund_reason, refund_status, refunded_at)
    VALUES ('RF0003', (SELECT ticket_id FROM tickets WHERE ticket_code='TK0010'), 465000, N'Chuyến đi bị hủy bởi nhà ga', 'COMPLETED', GETDATE());

IF NOT EXISTS (SELECT 1 FROM refunds WHERE refund_code='RF0004')
    INSERT INTO refunds (refund_code, ticket_id, refund_amount, refund_reason, refund_status, refunded_at)
    VALUES ('RF0004', (SELECT ticket_id FROM tickets WHERE ticket_code='TK0012'), 95000, N'Hủy vé trễ hạn quy định', 'REJECTED', NULL);

-- ---------------------------------------------------------------------
-- 13. Quick sanity check - row counts per table
-- ---------------------------------------------------------------------
SELECT 'users' AS tbl, COUNT(*) AS rows FROM users
UNION ALL SELECT 'stations', COUNT(*) FROM stations
UNION ALL SELECT 'routes', COUNT(*) FROM routes
UNION ALL SELECT 'route_stations', COUNT(*) FROM route_stations
UNION ALL SELECT 'trains', COUNT(*) FROM trains
UNION ALL SELECT 'coaches', COUNT(*) FROM coaches
UNION ALL SELECT 'seat_types', COUNT(*) FROM seat_types
UNION ALL SELECT 'seats', COUNT(*) FROM seats
UNION ALL SELECT 'train_trips', COUNT(*) FROM train_trips
UNION ALL SELECT 'trip_stations', COUNT(*) FROM trip_stations
UNION ALL SELECT 'bookings', COUNT(*) FROM bookings
UNION ALL SELECT 'passengers', COUNT(*) FROM passengers
UNION ALL SELECT 'tickets', COUNT(*) FROM tickets
UNION ALL SELECT 'payments', COUNT(*) FROM payments
UNION ALL SELECT 'invoices', COUNT(*) FROM invoices
UNION ALL SELECT 'refunds', COUNT(*) FROM refunds;