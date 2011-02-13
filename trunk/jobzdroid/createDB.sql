CREATE DATABASE IF NOT EXISTS jobzdroid;
USE jobzdroid;

DROP TABLE IF EXISTS AccountTable;
CREATE TABLE AccountTable (
    AccountID       INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, 
    Password        VARCHAR(15) NOT NULL,
    Email           VARCHAR(50) NOT NULL,
    SecondaryEmail  VARCHAR(50) DEFAULT '',
    Name            VARCHAR(50) NOT NULL,
    Type            CHAR(10) NOT NULL,
    Status          VARCHAR(7) NOT NULL,
    VerificationNum CHAR(32) DEFAULT NULL
);