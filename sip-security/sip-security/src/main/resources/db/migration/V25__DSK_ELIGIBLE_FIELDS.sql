/*******************************************************************************
 Filename:  V25__DSK_ELIGIBLE_FIELDS.SQL
 Purpose:   Creation of dsk_eligible_fields table
 Date:      13-12-2019
********************************************************************************/

DROP TABLE IF EXISTS DSK_ELIGIBLE_FIELDS;

CREATE TABLE DSK_ELIGIBLE_FIELDS
(
   CUSTOMER_SYS_ID bigint(20) NOT NULL COMMENT 'CUSTOMER_SYS_ID refers to the Id of the customer to which the fields belong to',
   PRODUCT_ID bigint(20) NOT NULL COMMENT 'PRODUCT_ID refers to the product to which the fields will belong to which product',
   SEMANTIC_ID varchar(100) NOT NULL COMMENT 'SEMANTIC_ID refers to the id of the semantic node for which these fields are added',
   COLUMN_NAME varchar(100) NOT NULL COMMENT 'COLUMN_NAME gives the details of the column',
   DISPLAY_NAME varchar(100) COMMENT 'DISPLAY_NAME is the value which gets displayed in the UI',
   ACTIVE_STATUS_IND tinyint(4) NOT NULL COMMENT 'ACTIVE_STATUS_IND indicates if the field is deleted or not',
   CREATED_TIME datetime NOT NULL COMMENT 'CREATED_TIME specifies the time at which the entry is created',
   CREATED_BY varchar(255) NOT NULL COMMENT 'CREATED_BY specifies the user who created this entry',
   MODIFIED_TIME datetime COMMENT 'MODIFIED_TIME indicates the time at which the entry was modified',
   MODIFIED_BY varchar(255) COMMENT 'MODIFIED_BY indicates the time at which the entry was modified'
) ENGINE=InnoDB COMMENT='PURPOSE: DSK_ELIGIBLE_FIELDS is used to hold the list of fields which are DSK eligible in a given semantic node';

/*******************************************************************************
	 ALTER Table Scripts Starts
	********************************************************************************/
ALTER TABLE DSK_ELIGIBLE_FIELDS
  ADD CONSTRAINT FK_CUSTOMER_SYS_ID_DSK_ELIGIBLE_FIELDS
  FOREIGN KEY (CUSTOMER_SYS_ID) REFERENCES CUSTOMERS (CUSTOMER_SYS_ID)
  ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE DSK_ELIGIBLE_FIELDS
  ADD CONSTRAINT FK_PRODUCT_ID_DSK_ELIGIBLE_FIELDS
  FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCTS (PRODUCT_SYS_ID)
  ON DELETE CASCADE ON UPDATE CASCADE;

