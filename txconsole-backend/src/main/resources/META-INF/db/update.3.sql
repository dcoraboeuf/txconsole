-- Contributions

CREATE TABLE CONTRIBUTION (
  ID        INTEGER      NOT NULL AUTO_INCREMENT,
  BRANCH    INTEGER      NOT NULL,
  MESSAGE   VARCHAR(400) NOT NULL,
  ACCOUNT   INTEGER      NOT NULL,
  TIMESTAMP TIMESTAMP    NOT NULL,
  CONSTRAINT CONTRIBUTION_PK PRIMARY KEY (ID),
  CONSTRAINT CONTRIBUTION_FK_BRANCH FOREIGN KEY (BRANCH) REFERENCES BRANCH (ID)
    ON DELETE CASCADE,
  CONSTRAINT CONTRIBUTION_FK_ACCOUNT FOREIGN KEY (ACCOUNT) REFERENCES ACCOUNT (ID)
    ON DELETE CASCADE
);

CREATE TABLE CONTRIBUTION_ENTRY (
  ID           INTEGER      NOT NULL AUTO_INCREMENT,
  CONTRIBUTION INTEGER      NOT NULL,
  BUNDLE       VARCHAR(40)  NOT NULL,
  SECTION      VARCHAR(80)  NOT NULL,
  NAME         VARCHAR(200) NOT NULL,
  LOCALE       VARCHAR(40)  NOT NULL,
  VALUE        MEDIUMTEXT,
  CONSTRAINT CONTRIBUTION_ENTRY_PK PRIMARY KEY (ID),
  CONSTRAINT CONTRIBUTION_ENTRY_UQ UNIQUE (CONTRIBUTION, BUNDLE, SECTION, NAME, LOCALE),
  CONSTRAINT CONTRIBUTION_ENTRY_FK_CONTRIBUTION FOREIGN KEY (CONTRIBUTION) REFERENCES CONTRIBUTION (ID)
    ON DELETE CASCADE
);
