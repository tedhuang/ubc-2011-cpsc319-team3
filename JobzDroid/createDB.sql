CREATE DATABASE IF NOT EXISTS jobzdroid;
USE jobzdroid;

DROP TABLE IF EXISTS TableEmailVerification;
CREATE TABLE TableEmailVerification (
    idEmailVerification       CHAR(36) NOT NULL PRIMARY KEY,
    idAccount       		INTEGER NOT NULL, 
    expiryTime     		BIGINT NOT NULL,
    CONSTRAINT FOREIGN KEY (`idAccount`) REFERENCES `TableAccount` (`idAccount`)
);