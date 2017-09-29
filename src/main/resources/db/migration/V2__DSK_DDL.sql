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

	CREATE TABLE SEC_GROUP (
        SEC_GROUP_SYS_ID bigint(20) NOT NULL ,
        ACTIVE_STATUS_IND tinyint(4) NOT NULL,
        CREATED_DATE datetime NOT NULL,
        CREATED_BY varchar(255) NOT NULL,
        INACTIVATED_DATE datetime DEFAULT NULL,
        INACTIVATED_BY varchar(255) DEFAULT NULL,
        MODIFIED_DATE datetime DEFAULT NULL,
        MODIFIED_BY varchar(255) DEFAULT NULL )
	ENGINE=InnoDB COMMENT='Purpose : SEC_GROUP table provides feature for Filter analyses using the data security key (row-level security) at a user level. When a user has a DSK associated with their account, only rows allowed by the user''s DSK are included in the analysis result.';
	
	CREATE TABLE SEC_GROUP_DSK_ATTRIBUTE (
  	SEC_GROUP_DSK_ATTRIBUTE_SYS_ID BIGINT(20) NOT NULL ,
  	SEC_GROUP_SYS_ID BIGINT(20) NOT NULL,
 	 ATTRIBUTE_NAME VARCHAR(100) NOT NULL
  	)
  	ENGINE=InnoDB ;

	CREATE TABLE SEC_GROUP_DSK_VALUE (
 	 SEC_GROUP_DSK_VALUE_SYS_ID BIGINT(20) NOT NULL,
 	 SEC_GROUP_DSK_ATTRIBUTE_SYS_ID BIGINT(20) NULL,
 	 DSK_VALUE VARCHAR(45) NULL
  	)
  	ENGINE=InnoDB ;		
		
	/*******************************************************************************
	 CREATE Table Scripts Ends
	********************************************************************************/
	
		
	/*******************************************************************************
	 ALTER Table Scripts Starts
	********************************************************************************/
    ALTER TABLE `users` 
     DROP COLUMN IF EXISTS `SEC_GROUP_SYS_ID`;
	ALTER TABLE USERS 
    	 ADD COLUMN SEC_GROUP_SYS_ID BIGINT(20) NULL AFTER `ACTIVE_STATUS_IND`;

	ALTER TABLE SEC_GROUP ADD CONSTRAINT SEC_GROUP_SYS_ID_PK PRIMARY KEY (SEC_GROUP_SYS_ID);
	ALTER TABLE SEC_GROUP CHANGE COLUMN SEC_GROUP_SYS_ID SEC_GROUP_SYS_ID BIGINT AUTO_INCREMENT NOT NULL;
	ALTER TABLE SEC_GROUP AUTO_INCREMENT = 1;

	ALTER TABLE SEC_GROUP_DSK_ATTRIBUTE ADD CONSTRAINT SEC_GROUP_DSK_ATTRIBUTE_SYS_ID_PK PRIMARY KEY (SEC_GROUP_DSK_ATTRIBUTE_SYS_ID);
	ALTER TABLE SEC_GROUP_DSK_ATTRIBUTE CHANGE COLUMN SEC_GROUP_DSK_ATTRIBUTE_SYS_ID SEC_GROUP_DSK_ATTRIBUTE_SYS_ID BIGINT AUTO_INCREMENT NOT NULL;
	ALTER TABLE SEC_GROUP_DSK_ATTRIBUTE AUTO_INCREMENT = 1;
	ALTER TABLE SEC_GROUP_DSK_ATTRIBUTE ADD CONSTRAINT FK_SEC_GROUP_ID FOREIGN KEY (SEC_GROUP_SYS_ID) REFERENCES SEC_GROUP (SEC_GROUP_SYS_ID) ON DELETE CASCADE ON UPDATE CASCADE;

	ALTER TABLE SEC_GROUP_DSK_VALUE ADD CONSTRAINT SEC_GROUP_DSK_VALUE_SYS_ID_PK PRIMARY KEY (SEC_GROUP_DSK_VALUE_SYS_ID);
	ALTER TABLE SEC_GROUP_DSK_VALUE CHANGE COLUMN SEC_GROUP_DSK_VALUE_SYS_ID SEC_GROUP_DSK_VALUE_SYS_ID BIGINT AUTO_INCREMENT NOT NULL;
	ALTER TABLE SEC_GROUP_DSK_VALUE AUTO_INCREMENT = 1;
	ALTER TABLE SEC_GROUP_DSK_VALUE ADD CONSTRAINT FK_SEC_GROUP_DSK_ATTRIBUTE_SYS_ID FOREIGN KEY (SEC_GROUP_DSK_ATTRIBUTE_SYS_ID) REFERENCES SEC_GROUP_DSK_ATTRIBUTE (SEC_GROUP_DSK_ATTRIBUTE_SYS_ID) ON DELETE CASCADE ON UPDATE CASCADE;


	/*******************************************************************************
	 ALTER Table Scripts Ends
	********************************************************************************/
	
/*******************************************************************************
 TABLE Scripts Ends
********************************************************************************/