DROP TABLE IF EXISTS PRIVILEGE_GROUP_CODES;
CREATE TABLE PRIVILEGE_GROUP_CODES (
  PRIVILEGE_GROUP_CODES_SYS_ID BIGINT NOT NULL,
  PRIVILEGE_CODES_SYS_ID BIGINT NOT NULL,
  PRIVILEGE_GRP_SYS_ID BIGINT NOT NULL
) ENGINE=INNODB;