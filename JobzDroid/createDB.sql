CREATE DATABASE IF NOT EXISTS jobzdroid;
USE jobzdroid;

DROP TABLE IF EXISTS tableEmailVerification;
CREATE TABLE tableEmailVerification (
    idEmailVerification     CHAR(36) NOT NULL PRIMARY KEY,
    idAccount       		INTEGER NOT NULL, 
    expiryTime     			BIGINT NOT NULL,
    emailPending			VARCHAR(254) DEFAULT NULL,
    CONSTRAINT FOREIGN KEY (`idAccount`) REFERENCES `tableAccount` (`idAccount`)
);

DROP TABLE IF EXISTS tableLocationJobAd;
CREATE TABLE tableLocationJobAd (
 	idJobAd					INT NOT NULL,
  	location				VARCHAR(45) NOT NULL,
  	longitude				DOUBLE DEFAULT NULL ,
  	latitude				DOUBLE DEFAULT NULL ,
  	PRIMARY KEY(idJobAd, location),
	CONSTRAINT FOREIGN KEY (`idJobAd`) REFERENCES `tableJobAd` (`idJobAd`) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS tableLocationProfile;
CREATE TABLE tableLocationProfile (
 	idAccount				INT NOT NULL,
  	location				VARCHAR(45) NOT NULL,
  	longitude				DOUBLE DEFAULT NULL ,
  	latitude				DOUBLE DEFAULT NULL ,
  	PRIMARY KEY(idAccount, location),
	CONSTRAINT FOREIGN KEY (`idAccount`) REFERENCES `tableAccount` (`idAccount`) ON DELETE CASCADE ON UPDATE CASCADE
);

