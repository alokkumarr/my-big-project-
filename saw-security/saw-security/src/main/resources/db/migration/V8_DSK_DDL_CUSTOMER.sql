/*******************************************************************************
 Filename:  V7__DSK_DDL_CUSTOMER.SQL
 Purpose:   To Associate Customer relation with Security Groups(Adding CUSTOMER_ID Ref to SEC_GROUP Table)
 Date:      25-10-2018
********************************************************************************/

	/*******************************************************************************
	 ALTER Table Scripts Starts
	********************************************************************************/

	ALTER TABLE SEC_GROUP
    	 ADD COLUMN CUSTOMER_SYS_ID bigint(20) AFTER `SEC_GROUP_SYS_ID`;

  ALTER TABLE SEC_GROUP
      ADD CONSTRAINT FK_CUSTOMER_SYS_ID FOREIGN KEY (`CUSTOMER_SYS_ID`) REFERENCES `CUSTOMERS` (`CUSTOMER_SYS_ID`)
      ON DELETE CASCADE ON UPDATE CASCADE;

  ALTER TABLE SEC_GROUP
      ADD CONSTRAINT UQ_CUSTOMER UNIQUE (`CUSTOMER_SYS_ID`,`SEC_GROUP_NAME`);

  ALTER TABLE SEC_GROUP_DSK_ATTRIBUTE
      ADD CONSTRAINT UQ_DSK_ATTR UNIQUE (`SEC_GROUP_SYS_ID`,`ATTRIBUTE_NAME`);


	/*******************************************************************************
	 ALTER Table Scripts Ends
	********************************************************************************/

/*******************************************************************************
 TABLE Scripts Ends
********************************************************************************/
