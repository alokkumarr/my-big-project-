/*******************************************************************************
 Filename:  V7__DSK_DDL_UPDATE.SQL
 Purpose:   To Migrate the DDL (Adding SEC_GROUP_Name,DESC Attribute to SEC_GROUP Table)
 Date:      09-10-2018
********************************************************************************/

	/*******************************************************************************
	 ALTER Table Scripts Starts
	********************************************************************************/

  ALTER TABLE SEC_GROUP CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;

  ALTER TABLE SEC_GROUP_DSK_ATTRIBUTE CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;

  ALTER TABLE SEC_GROUP_DSK_VALUE CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;

	ALTER TABLE SEC_GROUP
    	 ADD COLUMN SEC_GROUP_NAME Varchar(255) AFTER `SEC_GROUP_SYS_ID` ;

  ALTER TABLE SEC_GROUP
	    ADD COLUMN DESCRIPTION Varchar(255) AFTER `SEC_GROUP_NAME`;

  ALTER TABLE SEC_GROUP MODIFY COLUMN SEC_GROUP_NAME varchar(255) NOT NULL ;

  ALTER TABLE SEC_GROUP MODIFY COLUMN DESCRIPTION varchar(255) ;

	ALTER TABLE SEC_GROUP ADD CONSTRAINT SEC_GROUP_SYS_NAME_ID UNIQUE (SEC_GROUP_NAME);

  ALTER TABLE SEC_GROUP_DSK_ATTRIBUTE MODIFY COLUMN ATTRIBUTE_NAME varchar(100) NOT NULL ;


	/*******************************************************************************
	 ALTER Table Scripts Ends
	********************************************************************************/

/*******************************************************************************
 TABLE Scripts Ends
********************************************************************************/
