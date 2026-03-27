-- Hotel Management System Database
-- DACS 3203 - Secure Software Development
-- Run this script once to set up the database

CREATE DATABASE IF NOT EXISTS hotel_db;
USE hotel_db;

-- Users table (staff accounts)
CREATE TABLE IF NOT EXISTS users (
    userId INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    passwordHash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL
);

-- Rooms table
CREATE TABLE IF NOT EXISTS rooms (
    roomId INT AUTO_INCREMENT PRIMARY KEY,
    roomNumber VARCHAR(10) NOT NULL UNIQUE,
    roomType VARCHAR(30) NOT NULL,
    rate DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Available',
    cleaningStatus VARCHAR(20) NOT NULL DEFAULT 'Clean'
);

-- Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    bookingId INT AUTO_INCREMENT PRIMARY KEY,
    guestName VARCHAR(100) NOT NULL,
    guestPhone VARCHAR(20) NOT NULL,
    roomId INT NOT NULL,
    checkInDate DATE NOT NULL,
    checkOutDate DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Confirmed',
    FOREIGN KEY (roomId) REFERENCES rooms(roomId)
);

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    paymentId INT AUTO_INCREMENT PRIMARY KEY,
    bookingId INT NOT NULL,
    amount DOUBLE NOT NULL,
    method VARCHAR(30) NOT NULL,
    timestamp DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Paid',
    FOREIGN KEY (bookingId) REFERENCES bookings(bookingId)
);

-- Audit logs table
CREATE TABLE IF NOT EXISTS audit_logs (
    logId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    action VARCHAR(100) NOT NULL,
    details VARCHAR(255),
    timestamp DATETIME NOT NULL
);

-- Insert sample rooms
INSERT INTO rooms (roomNumber, roomType, rate, status, cleaningStatus) VALUES
('101', 'Single', 150.00, 'Available', 'Clean'),
('102', 'Single', 150.00, 'Available', 'Clean'),
('201', 'Double', 250.00, 'Available', 'Clean'),
('202', 'Double', 250.00, 'Available', 'Clean'),
('301', 'Suite', 500.00, 'Available', 'Clean'),
('302', 'Suite', 500.00, 'Available', 'Clean');

-- Insert default manager account
-- Default password is: Admin1234!
-- This hash must be replaced with a real BCrypt hash when BCrypt is added
INSERT INTO users (username, passwordHash, role, firstName, lastName) VALUES
('manager', 'REPLACE_WITH_BCRYPT_HASH', 'Manager', 'Hotel', 'Manager');