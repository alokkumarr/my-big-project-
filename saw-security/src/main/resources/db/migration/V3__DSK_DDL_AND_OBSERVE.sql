/*******************************************************************************
 Filename:  V3__DSK_DDL_AND_OBSERVE.SQL
 Purpose:   To Migrate the DDL and to introduce observe module
 Date:      06-12-2017
********************************************************************************/

	/*******************************************************************************
	 ALTER Table Scripts Starts
	********************************************************************************/
  ALTER TABLE ROLES DROP DATA_SECURITY_KEY;
	/*******************************************************************************
	 ALTER Table Scripts Ends
	********************************************************************************/
	
/*******************************************************************************
 TABLE Scripts Ends
********************************************************************************/


/*******************************************************************************
 Create Observe module and "My Dashboard" as default sub category
********************************************************************************/

INSERT INTO `CUSTOMER_PRODUCT_MODULE_FEATURES` (`CUST_PROD_MOD_SYS_ID`,`DEFAULT_URL`,`FEATURE_NAME`,`FEATURE_DESC`,`FEATURE_CODE`,`FEATURE_TYPE`,`DEFAULT`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`) VALUES (2,'/','My Dashboards','My Dashboards','MYDASHBOARDS21','PARENT_MYDASHBOARDS21',0,1,'2017-12-29 08:46:24','admin');
INSERT INTO `CUSTOMER_PRODUCT_MODULE_FEATURES` (`CUST_PROD_MOD_SYS_ID`,`DEFAULT_URL`,`FEATURE_NAME`,`FEATURE_DESC`,`FEATURE_CODE`,`FEATURE_TYPE`,`DEFAULT`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`) VALUES (2,'/','Drafts','Drafts','DRAFTS21','CHILD_MYDASHBOARDS21',0,1,'2017-12-29 08:47:51','admin');

-- take values from above query and put them in privileges
SELECT @CUSTOMER_PROD_MOD_SYS_ID_1 := CUST_PROD_MOD_SYS_ID, @CUST_PROD_MOD_FEATURE_SYS_ID_1 := CUST_PROD_MOD_FEATURE_SYS_ID FROM CUSTOMER_PRODUCT_MODULE_FEATURES WHERE FEATURE_CODE='DRAFTS21';

INSERT INTO `PRIVILEGES` (`CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('1', @CUSTOMER_PROD_MOD_SYS_ID_1, @CUST_PROD_MOD_FEATURE_SYS_ID_1, '1', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');

