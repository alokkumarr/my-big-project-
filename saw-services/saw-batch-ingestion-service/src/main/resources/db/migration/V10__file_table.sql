DROP TABLE IF EXISTS `SIP_JOB`;
DROP TABLE IF EXISTS `SIP_BIS_TRANSFER`;

CREATE TABLE `SIP_JOB`
(
  JOB_ID BIGINT  NOT NULL AUTO_INCREMENT COLLATE utf8_unicode_ci,
  JOB_NAME  VARCHAR(50),
  START_TIME DATETIME,
  END_TIME DATETIME,
  JOB_STATUS VARCHAR(50),
  TOTAL_FILES_COUNT BIGINT NOT NULL,
  SUCCESS_FILES_COUNT BIGINT NOT NULL,
  FILE_PATTERN VARCHAR(255),
  JOB_TYPE VARCHAR(50) NOT NULL,
  CREATED_DATE  DATETIME NULL,
  CREATED_BY   VARCHAR(255),
  UPDATED_DATE DATETIME,
  UPDATED_BY   VARCHAR(255),
  CONSTRAINT SIP_JOB_PK PRIMARY KEY (JOB_ID)
)ENGINE=InnoDB;

CREATE TABLE `SIP_BIS_TRANSFER`
(
  TRANSFER_ID BIGINT  NOT NULL AUTO_INCREMENT,
  JOB_ID BIGINT NOT NULL,
  FILE_NAME VARCHAR(50),
  FILE_PATH  VARCHAR(255),
  START_TIME DATETIME,
  END_TIME DATETIME,
  DURATION BIGINT,
  STATUS VARCHAR(50),
  FILE_PATTERN VARCHAR(255),
  TARGET_FILE_PATH VARCHAR(255),
  CREATED_TIME DATETIME,
  UPDATED_TIME DATETIME,
  STATE_REASON TEXT,
  CONSTRAINT SIP_BIS_TRANSFER_PK PRIMARY KEY (TRANSFER_ID)
)ENGINE=InnoDB;

ALTER TABLE ROLES ADD CONSTRAINT SIP_BIS_TRANSFER_FK FOREIGN KEY (JOB_ID) REFERENCES SIP_JOB(JOB_ID) ON DELETE CASCADE ON UPDATE CASCADE;