DROP TABLE IF EXISTS CONFIG_VAL;

CREATE TABLE CONFIG_VAL
(
  CONFIG_VAL_SYS_ID     BIGINT  NOT NULL,
  CONFIG_VAL_CODE      	VARCHAR(100)  NOT NULL COMMENT 'CONFIG_VAL_CODE uniquely identify the configuration name',
  CONFIG_VAL_DESC     	VARCHAR(255)  NOT NULL COMMENT 'CONFIG_VAL_DESC provides details about the configuration description',
  CONFIG_VAL_OBJ_TYPE	  VARCHAR(100) NOT NULL  COMMENT 'CONFIG_VAL_OBJ_TYPE column will be used to configure like which object type configuration belongs.For Ex. Customer ,User ,Product ,modules etc',
  CONFIG_VAL_OBJ_GROUP  VARCHAR(100) NOT NULL COMMENT 'CONFIG_VAL_OBJ_GROUP provides Object values like customer name if configuration is customer specific , Product name if configuration belongs to Product etc. ',
  ACTIVE_STATUS_IND     TINYINT NOT NULL COMMENT 'ACTIVE_STATUS_IND is set to 1 if configuration enabled, 0 for disable',
  CREATED_DATE    			DATETIME NOT NULL,
  CREATED_BY       			VARCHAR(255)NOT NULL,
  INACTIVATED_DATE   	  DATETIME,
  INACTIVATED_BY        VARCHAR(255),
  MODIFIED_DATE     		DATETIME,
  MODIFIED_BY        		VARCHAR(255)
)ENGINE=InnoDB COMMENT='Purpose : CONFIG_VAL table provides feature for additional configurable properties for the SAW';
