/*******************************************************************************
 Filename:  V7__DSK_DDL_UPDATE.SQL
 Purpose:   To Migrate the DDL (Adding SEC_GROUP_Name Attribute to SEC_GROUP Table)
 Date:      09-10-2018
********************************************************************************/

	/*******************************************************************************
	 ALTER Table Scripts Starts
	********************************************************************************/

	ALTER TABLE SEC_GROUP
    	 ADD COLUMN SEC_GROUP_NAME Varchar(255) AFTER `SEC_GROUP_SYS_ID`;

 ALTER TABLE SEC_GROUP
        MODIFY SEC_GROUP_NAME Varchar(255) NOT NULL;

	ALTER TABLE SEC_GROUP ADD CONSTRAINT SEC_GROUP_SYS_NAME_ID UNIQUE (SEC_GROUP_NAME);

	ALTER TABLE SEC_GROUP
	    ADD COLUMN DESCRIPTION Varchar(255) AFTER `SEC_GROUP_NAME`;

	/*******************************************************************************
	 ALTER Table Scripts Ends
	********************************************************************************/

/*******************************************************************************
 TABLE Scripts Ends
********************************************************************************/
