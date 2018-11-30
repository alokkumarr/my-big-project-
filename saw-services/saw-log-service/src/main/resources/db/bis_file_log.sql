CREATE SCHEMA IF NOT EXISTS SIP_BATCH_INGESTION;

DROP TABLE IF EXISTS `BIS_FILE_LOGS`;
CREATE TABLE `BIS_FILE_LOGS`
(
MFL_PID	TEXT
BIS_ROUTE_SYS_ID	BIGINT  NOT NULL
BIS_CHANNEL_SYS_ID BIGINT NOT NULL
BIS_CHANNEL_TYPE
BIS_FILE_PATTERN	TEXT
BIS_FILE_NAME	TEXT
BIS_ACTUAL_FILE_RCV_DATE	DATETIME NOT NULL
BIS_RECD_FILE_NAME	TEXT
BIS_RECD_FILE_SIZE_BYTES	DOUBLE(12,4)
MFL_FILE_VALID_STATUS	VARCHAR(255)
DATE_OF_ENTRY	DATETIME NOT NULL
DATE_OF_CHANGE	DATETIME NOT NULL
MFLD_PROCESS_STATE TEXT
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE = 'utf8_unicode_ci';


INSERT INTO `BIS_FILE_LOGS` (MFL_PID, BIS_ROUTE_SYS_ID, BIS_CHANNEL_SYS_ID,  BIS_CHANNEL_TYPE, BIS_FILE_PATTERN, BIS_FILE_NAME, BIS_ACTUAL_FILE_RCV_DATE, BIS_RECD_FILE_NAME, BIS_RECD_FILE_SIZE_BYTES, MFL_FILE_VALID_STATUS, DATE_OF_ENTRY, DATE_OF_CHANGE, MFLD_PROCESS_STATE  ) VALUES (2,1,1,"TEST","TEST","TEST",NOW(),"TEST",1.0,"TEST",NOW(),NOW(),"TEST");