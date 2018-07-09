/*******************************************************************************
 Filename:  V5__CONFIG_VAL_DDL.sql
 Purpose:   To Migrate the DDL
 Date:      20-06-2018
********************************************************************************/

/*******************************************************************************
 DROP Scripts Starts
********************************************************************************/
DROP TABLE IF EXISTS CONFIG_VAL;


/*******************************************************************************
 DROP Scripts Ends
********************************************************************************/


/*******************************************************************************
TABLE Scripts Starts
********************************************************************************/

	/*******************************************************************************
	 CREATE Table Scripts Starts
	********************************************************************************/

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

	/*******************************************************************************
	 CREATE Table Scripts Ends
	********************************************************************************/


	/*******************************************************************************
	 ALTER Table Scripts Starts
	********************************************************************************/

	ALTER TABLE CONFIG_VAL ADD CONSTRAINT CONFIG_VAL_SYS_ID_PK PRIMARY KEY (CONFIG_VAL_SYS_ID);
	ALTER TABLE CONFIG_VAL CHANGE COLUMN CONFIG_VAL_SYS_ID CONFIG_VAL_SYS_ID BIGINT AUTO_INCREMENT NOT NULL;
	ALTER TABLE CONFIG_VAL AUTO_INCREMENT = 1;
	ALTER TABLE CONFIG_VAL ADD UNIQUE INDEX `CONFIG_VAL_CODE_UNIQUE` (`CONFIG_VAL_CODE`,`CONFIG_VAL_OBJ_TYPE`,`CONFIG_VAL_OBJ_GROUP` ASC);

	/*******************************************************************************
	 ALTER Table Scripts Ends
	********************************************************************************/

/*******************************************************************************
TABLE Scripts Ends
********************************************************************************/

/*******************************************************************************
DML Scripts starts
********************************************************************************/

INSERT INTO CONFIG_VAL (`CONFIG_VAL_SYS_ID`, `CONFIG_VAL_CODE`, `CONFIG_VAL_DESC`, `CONFIG_VAL_OBJ_TYPE`,
`CONFIG_VAL_OBJ_GROUP`, `ACTIVE_STATUS_IND`, `CREATED_DATE` , CREATED_BY )
VALUES ('1', 'es-analysis-auto-refresh', 'Charts,Pivots and ES Reports Execute each time when land on View Analysis Page',
'CUSTOMER', 'SYNCHRONOSS', '1', now(), 'Saw-Admin' );


/*******************************************************************************
 DML Scripts starts
********************************************************************************/
