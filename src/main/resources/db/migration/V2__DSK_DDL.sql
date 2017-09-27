/*******************************************************************************
 Filename:  V2__DSK_DDL.SQL
 Purpose:   To Migrate the DDL
 Date:      25-09-2017
********************************************************************************/

/*******************************************************************************
 DROP Scripts Starts
********************************************************************************/
DROP TABLE IF EXISTS SEC_GROUP_DSK_VALUE;
DROP TABLE IF EXISTS SEC_GROUP_DSK_ATTRIBUTE;
DROP TABLE IF EXISTS SEC_GROUP;


/*******************************************************************************
 DROP Scripts Ends
********************************************************************************/


/*******************************************************************************
TABLE Scripts Starts
********************************************************************************/

	/*******************************************************************************
	 CREATE Table Scripts Starts
	********************************************************************************/

	CREATE TABLE `sec_group` (
	  `SEC_GROUP_SYS_ID` bigint(20) NOT NULL AUTO_INCREMENT,
	  `ACTIVE_STATUS_IND` tinyint(4) NOT NULL,
	  `CREATED_DATE` datetime NOT NULL,
	  `CREATED_BY` varchar(255) NOT NULL,
	  `INACTIVATED_DATE` datetime DEFAULT NULL,
	  `INACTIVATED_BY` varchar(255) DEFAULT NULL,
	  `MODIFIED_DATE` datetime DEFAULT NULL,
	  `MODIFIED_BY` varchar(255) DEFAULT NULL,
	  PRIMARY KEY (`SEC_GROUP_SYS_ID`)
	  ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Purpose : SEC_GROUP table provides feature for Filter analyses using the data security key (row-level security) at a user level. When a user has a DSK associated with their account, only rows allowed by the user''s DSK are included in the analysis result.';
	  
	CREATE TABLE `sec_group_dsk_attribute` (
	  `SEC_GROUP_DSK_ATTRIBUTE_SYS_ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
	  `SEC_GROUP_SYS_ID` BIGINT(20) NOT NULL,
	  `ATTRIBUTE_NAME` VARCHAR(100) NOT NULL,
	  PRIMARY KEY (`SEC_GROUP_DSK_ATTRIBUTE_SYS_ID`),
	  INDEX `SEC_GROUP_ID_idx` (`SEC_GROUP_SYS_ID` ASC),
	  CONSTRAINT `FK_SEC_GROUP_ID`
		FOREIGN KEY (`SEC_GROUP_SYS_ID`)
		REFERENCES `sec_group` (`SEC_GROUP_SYS_ID`)
		ON DELETE CASCADE
		ON UPDATE CASCADE);
		
	CREATE TABLE `sec_group_dsk_value` (
	  `SEC_GROUP_DSK_VALUE_SYS_ID` BIGINT(20) NOT NULL,
	  `SEC_GROUP_DSK_ATTRIBUTE_SYS_ID` BIGINT(20) NULL,
	  `DSK_VALUE` VARCHAR(45) NULL,
	  PRIMARY KEY (`SEC_GROUP_DSK_VALUE_SYS_ID`),
	  INDEX `FK_SEC_GROUP_DSK_ATTRIBUTE_SYS_ID_idx` (`SEC_GROUP_DSK_ATTRIBUTE_SYS_ID` ASC),
	  CONSTRAINT `FK_SEC_GROUP_DSK_ATTRIBUTE_SYS_ID`
		FOREIGN KEY (`SEC_GROUP_DSK_ATTRIBUTE_SYS_ID`)
		REFERENCES `sec_group_dsk_attribute` (`SEC_GROUP_DSK_ATTRIBUTE_SYS_ID`)
		ON DELETE CASCADE
		ON UPDATE CASCADE);
	
	/*******************************************************************************
	 CREATE Table Scripts Ends
	********************************************************************************/
	
		
	/*******************************************************************************
	 ALTER Table Scripts Starts
	********************************************************************************/
    ALTER TABLE `users` 
     DROP COLUMN IF EXISTS `SEC_GROUP_SYS_ID`;

	ALTER TABLE `users` 
     ADD COLUMN `SEC_GROUP_SYS_ID` BIGINT(20) NULL AFTER `ACTIVE_STATUS_IND`;

	/*******************************************************************************
	 ALTER Table Scripts Ends
	********************************************************************************/
	
/*******************************************************************************
 TABLE Scripts Ends
********************************************************************************/