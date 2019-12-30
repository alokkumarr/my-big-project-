/*******************************************************************************
 Filename:  V25__DSK_DDL_SAW_SECURITY.SQL
 Purpose:   Create Security group DSK attribute model table
 Date:      26-12-2019
********************************************************************************/

/*******************************************************************************
 DROP Scripts Starts
********************************************************************************/
DROP TABLE IF EXISTS SEC_GROUP_DSK_ATTRIBUTE_MODEL;


/******************************************************************************
 Create SEC_GROUP_DSK_ATTRIBUTE_MODEL table
 */
CREATE TABLE SEC_GROUP_DSK_ATTRIBUTE_MODEL (
	SEC_GROUP_DSK_ATTRIBUTE_SYS_ID VARCHAR(50) NOT NULL,
	SEC_GROUP_SYS_ID BIGINT REFERENCES SEC_GROUP(SEC_GROUP_SYS_ID),
	SEC_GROUP_DSK_PARENT_ID VARCHAR(50),
	BOOLEAN_CRITERIA VARCHAR(20),
	COLUMN_NAME VARCHAR(20),
	OPERATOR VARCHAR(20),
	ATTRIBUTE_VALUES TEXT
) ENGINE=InnoDB ;

/**********************************************************************************
  Add table constraints
 **********************************************************************************/

ALTER TABLE SEC_GROUP_DSK_ATTRIBUTE_MODEL
      ADD CONSTRAINT PK_SEC_GROUP_DSK_ATTRIBUTE_SYS_ID PRIMARY KEY (SEC_GROUP_DSK_ATTRIBUTE_SYS_ID);

ALTER TABLE SEC_GROUP_DSK_ATTRIBUTE_MODEL
      ADD CONSTRAINT FK_SEC_GROUP_SYS_ID FOREIGN KEY
      SEC_GROUP_SYS_ID REFERENCES SEC_GROUP(SEC_GROUP_SYS_ID)
      ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE SEC_GROUP_DSK_ATTRIBUTE_MODEL
      ADD CONSTRAINT FK_SEC_GROUP_DSK_PARENT_ID FOREIGN KEY
      SEC_GROUP_DSK_PARENT_ID REFERENCES (SEC_GROUP_DSK_ATTRIBUTE_SYS_ID)
      ON DELETE CASCADE ON UPDATE CASCADE;

/*******************************************************************************
 TABLE Scripts Ends
********************************************************************************/
