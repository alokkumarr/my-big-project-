/*******************************************************************************
 Filename:  V12__DDL_RM_CREATE_PRIV.sql
 Purpose:   To Remove Create Privilege from Analyze module .
 Date:      30-01-2019
********************************************************************************/

	/*******************************************************************************
	 DELETE Table Scripts Starts
	 ** The whole idea of removing Access Privilege from Analyze is to allow all users to create irrespective of privilege.
	 FYI: The created analysis will default goes to Private Folders (Drafts)
	********************************************************************************/

  DELETE from MODULE_PRIVILEGES where MODULE_SYS_ID = (Select MODULE_SYS_ID FROM MODULES WHERE MODULE_NAME = 'ANALYZE') and PRIVILEGE_CODES_SYS_ID = (Select PRIVILEGE_CODES_SYS_ID FROM privilege_codes where PRIVILEGE_CODES_NAME = 'CREATE');

	/*******************************************************************************
	 DELETE Table Scripts Ends
	********************************************************************************/
