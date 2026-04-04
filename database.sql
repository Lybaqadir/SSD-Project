
-- lyba did this


-- Hotel Management System Database

DROP DATABASE IF EXISTS hotel_db;
CREATE DATABASE hotel_db;
USE hotel_db;


-- USERS TABLE

CREATE TABLE users (
    userId       INT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    passwordHash VARCHAR(255) NOT NULL,
    role         VARCHAR(20)  NOT NULL,
    firstName    VARCHAR(50)  NOT NULL,
    lastName     VARCHAR(50)  NOT NULL
);


-- ROOMS TABLE
-- status values your Java code uses: available, occupied, reserved
-- cleaningStatus values: clean, dirty, in_progress

CREATE TABLE rooms (
    roomId         INT AUTO_INCREMENT PRIMARY KEY,
    roomNumber     VARCHAR(10) NOT NULL UNIQUE,
    roomType       VARCHAR(30) NOT NULL,
    rate           DOUBLE      NOT NULL,
    status         VARCHAR(20) NOT NULL DEFAULT 'available',
    cleaningStatus VARCHAR(20) NOT NULL DEFAULT 'clean'
);


-- BOOKINGS TABLE
-- status values: booked, checked-in, checked-out, cancelled

CREATE TABLE bookings (
    bookingId    INT AUTO_INCREMENT PRIMARY KEY,
    guestName    VARCHAR(100) NOT NULL,
    guestPhone   VARCHAR(20)  NOT NULL,
    roomId       INT          NOT NULL,
    checkInDate  DATE         NOT NULL,
    checkOutDate DATE         NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'booked',
    FOREIGN KEY (roomId) REFERENCES rooms(roomId)
);


-- PAYMENTS TABLE

CREATE TABLE payments (
    paymentId INT AUTO_INCREMENT PRIMARY KEY,
    bookingId INT          NOT NULL,
    amount    DOUBLE       NOT NULL,
    method    VARCHAR(30)  NOT NULL,
    timestamp DATETIME     NOT NULL,
    status    VARCHAR(20)  NOT NULL DEFAULT 'paid',
    FOREIGN KEY (bookingId) REFERENCES bookings(bookingId)
);


-- AUDIT LOGS TABLE

CREATE TABLE audit_logs (
    logId     INT AUTO_INCREMENT PRIMARY KEY,
    userId    INT          NOT NULL,
    action    VARCHAR(100) NOT NULL,
    details   VARCHAR(255),
    timestamp DATETIME     NOT NULL
);


-- SAMPLE ROOMS

INSERT INTO rooms (roomNumber, roomType, rate, status, cleaningStatus) VALUES
    ('101', 'Single', 150.00, 'available', 'clean'),
    ('102', 'Single', 150.00, 'available', 'clean'),
    ('201', 'Double', 250.00, 'available', 'clean'),
    ('202', 'Double', 250.00, 'available', 'clean'),
    ('301', 'Suite',  500.00, 'available', 'clean'),
    ('302', 'Suite',  500.00, 'available', 'clean');


-- USER ACCOUNTS
--
-- All passwords hashed with jBCrypt ($2a$) — Java compatible
--
-- manager      password: Admin1234!
-- receptionist password: Staff1234!
-- cleaning     password: Clean1234!
--
-- HOW THIS HASH WAS MADE:
--   The $2b$ prefix from Python bcrypt was replaced with $2a$
--   which is what Java's jBCrypt library expects.
--   Both use the same algorithm — only the prefix differs.

INSERT INTO users (username, passwordHash, role, firstName, lastName) VALUES
    (
        'manager',
        '$2a$12$AEFGI7TcsXoOb/uOuLiKTOKJUQ941DGUKztS7A1y.KfaLwsEsRSe.',
        'Manager',
        'Hotel',
        'Manager'
    ),
    (
        'receptionist',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'Receptionist',
        'Front',
        'Desk'
    ),
    (
        'cleaning',
        '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
        'Cleaning Staff',
        'Clean',
        'Staff'
    );

