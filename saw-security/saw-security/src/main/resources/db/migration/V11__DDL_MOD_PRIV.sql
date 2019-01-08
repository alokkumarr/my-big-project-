/*******************************************************************************
 Filename:  v11__DDL_MOD_PRIV.SQL
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
	 INSERT Table Scripts Starts
	********************************************************************************/

	INSERT INTO `MODULE_PRIVILEGES` (`MODULE_PRIV_SYS_ID`,`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES ('1', '1', '3');
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_PRIV_SYS_ID`,`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES ('2', '1', '5');
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_PRIV_SYS_ID`,`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES ('3', '1', '7');
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_PRIV_SYS_ID`,`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES ('4', '1', '9');
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_PRIV_SYS_ID`,`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES ('5', '1', '11');
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_PRIV_SYS_ID`,`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES ('6', '1', '13');
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_PRIV_SYS_ID`,`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES ('7', '1', '15');
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_PRIV_SYS_ID`,`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES ('8', '1', '17');
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_PRIV_SYS_ID`,`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES ('9', '2', '3');
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_PRIV_SYS_ID`,`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES ('10', '2', '5');
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_PRIV_SYS_ID`,`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES ('11', '2', '11');
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_PRIV_SYS_ID`,`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES ('12', '2', '13');
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_PRIV_SYS_ID`,`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES ('13', '2', '15');
  INSERT INTO `MODULE_PRIVILEGES` (`MODULE_PRIV_SYS_ID`,`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) VALUES ('14', '2', '17');

	/*******************************************************************************
	 INSERT Table Scripts Ends
	********************************************************************************/

/*******************************************************************************
 TABLE Scripts Ends
********************************************************************************/
