/*******************************************************************************
 Filename:  V24__DSK_ELIGIBLE_FIELDS.SQL
 Purpose:   Creation of dsk_eligible_fields table
 Date:      13-12-2019
********************************************************************************/

DROP TABLE IF EXISTS dsk_eligible_fields;

CREATE TABLE dsk_eligible_fields
(
   CUSTOMER_SYS_ID bigint(20) NOT NULL,
   PRODUCT_ID bigint(20) NOT NULL,
   SEMANTIC_ID varchar(100) NOT NULL,
   COLUMN_NAME varchar(100) NOT NULL,
   DISPLAY_NAME varchar(100) NOT NULL,
   ACTIVE_STATUS_IND tinyint(4) NOT NULL,
   CREATED_TIME datetime NOT NULL,
   CREATED_BY varchar(255) NOT NULL,
   MODIFIED_TIME datetime NOT NULL,
   MODIFIED_BY varchar(255) NOT NULL
) ENGINE=InnoDB;
