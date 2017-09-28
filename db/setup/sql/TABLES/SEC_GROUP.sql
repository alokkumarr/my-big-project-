DROP TABLE IF EXISTS SEC_GROUP;

CREATE TABLE SEC_GROUP (
  SEC_GROUP_SYS_ID bigint(20) NOT NULL ,
  ACTIVE_STATUS_IND tinyint(4) NOT NULL,
  CREATED_DATE datetime NOT NULL,
  CREATED_BY varchar(255) NOT NULL,
  INACTIVATED_DATE datetime DEFAULT NULL,
  INACTIVATED_BY varchar(255) DEFAULT NULL,
  MODIFIED_DATE datetime DEFAULT NULL,
  MODIFIED_BY varchar(255) DEFAULT NULL
)
ENGINE=InnoDB COMMENT='Purpose : SEC_GROUP table provides feature for Filter analyses using the data security key (row-level security) at a user level. When a user has a DSK associated with their account, only rows allowed by the user''s DSK are included in the analysis result.';
