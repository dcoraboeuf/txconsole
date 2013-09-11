-- Initial schema

-- Versionning
CREATE TABLE DBVERSION (
  VALUE INTEGER NOT NULL,
  UPDATED TIMESTAMP NOT NULL
);

-- Configuration table
CREATE TABLE CONFIGURATION (
	NAME VARCHAR(40) NOT NULL,
	VALUE VARCHAR(200) NOT NULL,
	CONSTRAINT PK_CONFIGURATION PRIMARY KEY (NAME)
);

-- Accounts
CREATE TABLE ACCOUNT (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME VARCHAR(16) NOT NULL,
	FULLNAME VARCHAR(40) NOT NULL,
	EMAIL VARCHAR(80) NOT NULL,
	ROLENAME VARCHAR(40) NOT NULL,
	MODE VARCHAR(10) NOT NULL,
	PASSWORD VARCHAR(140) NULL,
	LOCALE VARCHAR(6) NULL,
	CONSTRAINT PK_ACCOUNT PRIMARY KEY (ID),
	CONSTRAINT UQ_ACCOUNT UNIQUE (NAME)
);

-- Project
CREATE TABLE PROJECT (
  ID INTEGER NOT NULL AUTO_INCREMENT,
  NAME VARCHAR(20) NOT NULL,
  FULLNAME VARCHAR(80) NULL,
  TXSOURCE_ID VARCHAR(120) NOT NULL,
  TXSOURCE_CONFIG MEDIUMTEXT NULL,
  CONSTRAINT PROJECT_PK PRIMARY KEY (ID),
  CONSTRAINT PROJECT_UQ UNIQUE (NAME)
);

-- Project authorizations
CREATE TABLE PROJECT_AUTHORIZATION (
  PROJECT INTEGER NOT NULL,
  ACCOUNT INTEGER NOT NULL,
  ROLE VARCHAR(20) NOT NULL,
  CONSTRAINT PROJECT_AUTHORIZATION_PK PRIMARY KEY (PROJECT, ACCOUNT),
  CONSTRAINT PROJECT_AUTHORIZATION_FK_PROJECT FOREIGN KEY (PROJECT) REFERENCES PROJECT (ID) ON DELETE CASCADE,
  CONSTRAINT PROJECT_AUTHORIZATION_FK_ACCOUNT FOREIGN KEY (ACCOUNT) REFERENCES ACCOUNT (ID) ON DELETE CASCADE
);

-- Branch
CREATE TABLE BRANCH (
  ID INTEGER NOT NULL AUTO_INCREMENT,
  PROJECT INTEGER NOT NULL,
  NAME VARCHAR(20) NOT NULL,
  CONSTRAINT BRANCH_PK PRIMARY KEY (ID),
  CONSTRAINT BRANCH_UQ UNIQUE (PROJECT, NAME),
  CONSTRAINT BRANCH_FK_PROJECT FOREIGN KEY (PROJECT) REFERENCES PROJECT (ID) ON DELETE CASCADE
);

-- Branch parameters
CREATE TABLE BRANCH_PARAMETER (
  BRANCH INTEGER NOT NULL,
  PARAMETER VARCHAR(40) NOT NULL,
  VALUE VARCHAR(400) NOT NULL,
  CONSTRAINT BRANCH_PARAMETER_PK PRIMARY KEY (BRANCH, PARAMETER),
  CONSTRAINT BRANCH_PARAMETER_FK_BRANCH FOREIGN KEY (BRANCH) REFERENCES BRANCH (ID) ON DELETE CASCADE
);

-- Signatures for creation & updates (soft link to the account ID)
-- Table with hard links on entities
CREATE TABLE EVENT (
  ID INTEGER NOT NULL AUTO_INCREMENT,
  EVENT_CODE VARCHAR(40) NOT NULL,
  EVENT_PARAMETERS MEDIUMTEXT NULL,
  EVENT_TIMESTAMP TIMESTAMP NOT NULL,
  ACCOUNT_ID INTEGER NULL,
  ACCOUNT_NAME VARCHAR(80) NOT NULL,
  ACCOUNT INTEGER NULL,
  PROJECT INTEGER NULL,
  BRANCH INTEGER NULL,
  CONSTRAINT EVENT_PK PRIMARY KEY (ID),
  CONSTRAINT EVENT_FK_ACCOUNT_ID FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNT (ID) ON DELETE SET NULL,
  CONSTRAINT EVENT_FK_ACCOUNT FOREIGN KEY (ACCOUNT) REFERENCES ACCOUNT (ID) ON DELETE CASCADE,
  CONSTRAINT EVENT_FK_PROJECT FOREIGN KEY (PROJECT) REFERENCES PROJECT (ID) ON DELETE CASCADE,
  CONSTRAINT EVENT_FK_BRANCH FOREIGN KEY (BRANCH) REFERENCES BRANCH (ID) ON DELETE CASCADE
);

-- Initial admin account (admin)
INSERT INTO ACCOUNT (NAME, FULLNAME, EMAIL, ROLENAME, MODE, PASSWORD) VALUES (
    'admin',
    'Administrator',
    '',
    'ROLE_ADMIN',
    'builtin',
    'C7AD44CBAD762A5DA0A452F9E854FDC1E0E7A52A38015F23F3EAB1D80B931DD472634DFAC71CD34EBC35D16AB7FB8A90C81F975113D6C7538DC69DD8DE9077EC');
