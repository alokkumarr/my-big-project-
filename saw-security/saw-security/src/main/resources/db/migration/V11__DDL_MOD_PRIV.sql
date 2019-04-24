/*******************************************************************************
 Filename:  V11__DDL_MOD_PRIV.SQL
 Purpose:   To Associate Module wise privileges.
 Date:      07-01-2019
********************************************************************************/

  /*******************************************************************************
    DROP Scripts Starts
  ********************************************************************************/

  DROP TABLE IF EXISTS MODULE_PRIVILEGES;

  /*******************************************************************************
    DROP Scripts Ends
  ********************************************************************************/

  /*******************************************************************************
	 CREATE Table Scripts Starts
	********************************************************************************/

	Create Table MODULE_PRIVILEGES
  (
	  MODULE_PRIV_SYS_ID	BIGINT  NOT NULL,
	  MODULE_SYS_ID 	BIGINT  NOT NULL,
	  PRIVILEGE_CODES_SYS_ID BIGINT NOT NULL

  )ENGINE=InnoDB COMMENT='Purpose : MODULE_PRIVILEGES table provides a list of privileges for every module in the system';

  /*******************************************************************************
	 CREATE Table Scripts Ends
	********************************************************************************/

	/*******************************************************************************
	 ALTER Table Scripts Starts
	********************************************************************************/

  ALTER TABLE MODULE_PRIVILEGES ADD CONSTRAINT MODULE_PRIV_SYS_ID_PK PRIMARY KEY (MODULE_PRIV_SYS_ID);

  ALTER TABLE MODULE_PRIVILEGES CHANGE COLUMN MODULE_PRIV_SYS_ID MODULE_PRIV_SYS_ID BIGINT AUTO_INCREMENT NOT NULL;

  ALTER TABLE MODULE_PRIVILEGES AUTO_INCREMENT = 1;

	ALTER TABLE MODULE_PRIVILEGES ADD CONSTRAINT FK_MODULE_SYS_ID FOREIGN KEY (`MODULE_SYS_ID`) REFERENCES `MODULES` (`MODULE_SYS_ID`) ON DELETE CASCADE ON UPDATE CASCADE;

  ALTER TABLE MODULE_PRIVILEGES ADD CONSTRAINT FK_PRIVILEGE_CODES_SYS_ID FOREIGN KEY (`PRIVILEGE_CODES_SYS_ID`) REFERENCES `privilege_codes` (`PRIVILEGE_CODES_SYS_ID`) ON DELETE CASCADE ON UPDATE CASCADE;

  ALTER TABLE MODULE_PRIVILEGES ADD CONSTRAINT UQ_MOD_PRIV UNIQUE (`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`);


	/*******************************************************************************
	 ALTER Table Scripts Ends
	********************************************************************************/

	/*******************************************************************************
	 INSERT Table Scripts Starts..
	 Here the reason for doing inner join is to fetch the correct module sys id being referenced in existing installations.
	 FYI : We were having a duplicate instance of Observe module in our STATIC_DATA.sql and we are removing the duplicate as part of SIP-5373.
	********************************************************************************/

	INSERT INTO `MODULE_PRIVILEGES` (`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES (
  (SELECT MODULE_SYS_ID FROM MODULES WHERE MODULE_NAME = 'ANALYZE'),
  (SELECT PRIVILEGE_CODES_SYS_ID FROM privilege_codes WHERE PRIVILEGE_CODES_NAME = 'ACCESS'));
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES (
  (SELECT MODULE_SYS_ID FROM MODULES WHERE MODULE_NAME = 'ANALYZE'),
  (SELECT PRIVILEGE_CODES_SYS_ID FROM privilege_codes WHERE PRIVILEGE_CODES_NAME = 'CREATE'));
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES (
  (SELECT MODULE_SYS_ID FROM MODULES WHERE MODULE_NAME = 'ANALYZE'),
  (SELECT PRIVILEGE_CODES_SYS_ID FROM privilege_codes WHERE PRIVILEGE_CODES_NAME = 'EXECUTE'));
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES (
  (SELECT MODULE_SYS_ID FROM MODULES WHERE MODULE_NAME = 'ANALYZE'),
  (SELECT PRIVILEGE_CODES_SYS_ID FROM privilege_codes WHERE PRIVILEGE_CODES_NAME = 'PUBLISH'));
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES (
  (SELECT MODULE_SYS_ID FROM MODULES WHERE MODULE_NAME = 'ANALYZE'),
  (SELECT PRIVILEGE_CODES_SYS_ID FROM privilege_codes WHERE PRIVILEGE_CODES_NAME = 'FORK'));
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES (
  (SELECT MODULE_SYS_ID FROM MODULES WHERE MODULE_NAME = 'ANALYZE'),
  (SELECT PRIVILEGE_CODES_SYS_ID FROM privilege_codes WHERE PRIVILEGE_CODES_NAME = 'EDIT'));
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES (
  (SELECT MODULE_SYS_ID FROM MODULES WHERE MODULE_NAME = 'ANALYZE'),
  (SELECT PRIVILEGE_CODES_SYS_ID FROM privilege_codes WHERE PRIVILEGE_CODES_NAME = 'EXPORT'));
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES (
  (SELECT MODULE_SYS_ID FROM MODULES WHERE MODULE_NAME = 'ANALYZE'),
  (SELECT PRIVILEGE_CODES_SYS_ID FROM privilege_codes WHERE PRIVILEGE_CODES_NAME = 'DELETE'));

  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES (
  (SELECT distinct m.MODULE_SYS_ID FROM MODULES m INNER JOIN PRODUCT_MODULES p on (m.MODULE_SYS_ID = p.MODULE_SYS_ID) WHERE m.MODULE_NAME = 'OBSERVE'),
  (SELECT PRIVILEGE_CODES_SYS_ID FROM privilege_codes WHERE PRIVILEGE_CODES_NAME = 'ACCESS'));
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES (
  (SELECT distinct m.MODULE_SYS_ID FROM MODULES m INNER JOIN PRODUCT_MODULES p on (m.MODULE_SYS_ID = p.MODULE_SYS_ID) WHERE m.MODULE_NAME = 'OBSERVE'),
  (SELECT PRIVILEGE_CODES_SYS_ID FROM privilege_codes WHERE PRIVILEGE_CODES_NAME = 'CREATE'));
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES (
  (SELECT distinct m.MODULE_SYS_ID FROM MODULES m INNER JOIN PRODUCT_MODULES p on (m.MODULE_SYS_ID = p.MODULE_SYS_ID) WHERE m.MODULE_NAME = 'OBSERVE'),
  (SELECT PRIVILEGE_CODES_SYS_ID FROM privilege_codes WHERE PRIVILEGE_CODES_NAME = 'EDIT'));
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES (
  (SELECT distinct m.MODULE_SYS_ID FROM MODULES m INNER JOIN PRODUCT_MODULES p on (m.MODULE_SYS_ID = p.MODULE_SYS_ID) WHERE m.MODULE_NAME = 'OBSERVE'),
  (SELECT PRIVILEGE_CODES_SYS_ID FROM privilege_codes WHERE PRIVILEGE_CODES_NAME = 'EXPORT'));
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES (
  (SELECT distinct m.MODULE_SYS_ID FROM MODULES m INNER JOIN PRODUCT_MODULES p on (m.MODULE_SYS_ID = p.MODULE_SYS_ID) WHERE m.MODULE_NAME = 'OBSERVE'),
  (SELECT PRIVILEGE_CODES_SYS_ID FROM privilege_codes WHERE PRIVILEGE_CODES_NAME = 'DELETE'));





	/*******************************************************************************
	 INSERT Table Scripts Ends
	********************************************************************************/

/*******************************************************************************
 TABLE Scripts Ends
********************************************************************************/
