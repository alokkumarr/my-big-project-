/******************************** Schema_version Initial DML script ******************/

/** Do not Delete
This is added to skip the migration execution for version, if database schema has created using same initial-setup version.
Entry needs to be made here in case of new version of migartion release ***/

INSERT INTO schema_version VALUES ('1', '1', '<< Flyway Baseline >>', 'BASELINE', '<< Flyway Baseline >>', NULL, 'system', NOW() , '0', '1') ;
INSERT INTO schema_version VALUES ('2', '2', '<< Flyway Baseline >>', 'BASELINE', '<< Flyway Baseline >>', NULL, 'system', NOW(), '0', '1') ;

/**************************************** Initial Schema_version DML script Ends here  *******************************************/
INSERT INTO `PRODUCTS` (`PRODUCT_SYS_ID`,`PRODUCT_NAME`,`PRODUCT_CODE`,`PRODUCT_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (1,'MCT Insights','MCTI000001','MCT Insights',1,'2017-05-23 06:51:34','admin','','','','');
INSERT INTO `PRODUCTS` (`PRODUCT_SYS_ID`,`PRODUCT_NAME`,`PRODUCT_CODE`,`PRODUCT_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (2,'SnT Insighjts','SNT0000001','SnT Insighjts',1,'2017-05-23 06:51:42','admin','','','','');
INSERT INTO `PRODUCTS` (`PRODUCT_SYS_ID`,`PRODUCT_NAME`,`PRODUCT_CODE`,`PRODUCT_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (3,'Smart Care Insights','SCI0000001','Smart Care Insights',1,'2017-05-23 06:51:48','admin','','','','');
INSERT INTO `PRODUCTS` (`PRODUCT_SYS_ID`,`PRODUCT_NAME`,`PRODUCT_CODE`,`PRODUCT_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (4,'SAW Demo','SAWD000001','SAW Demo',1,'2017-05-23 06:51:53','admin','','','','');
INSERT INTO `PRODUCTS` (`PRODUCT_SYS_ID`,`PRODUCT_NAME`,`PRODUCT_CODE`,`PRODUCT_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (5,'Channel Insights','CI00000001','Channel Insights',1,'2017-05-23 06:51:58','admin','','','','');

INSERT INTO `MODULES` (`MODULE_SYS_ID`,`MODULE_NAME`,`MODULE_CODE`,`MODULE_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (1,'ANALYZE','ANLYS00001','Analyze Modukle',1,'2017-05-23 06:52:05','admin','','','','');
INSERT INTO `MODULES` (`MODULE_SYS_ID`,`MODULE_NAME`,`MODULE_CODE`,`MODULE_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (2,'OBSERVE','OBSR000001','Observe Module',1,'2017-05-23 06:52:10','admin','','','','');
INSERT INTO `MODULES` (`MODULE_SYS_ID`,`MODULE_NAME`,`MODULE_CODE`,`MODULE_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (3,'ALERT','ALRT000001','Alert Module',1,'2017-05-23 06:52:16','admin','','','','');
INSERT INTO `MODULES` (`MODULE_SYS_ID`,`MODULE_NAME`,`MODULE_CODE`,`MODULE_DESC`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (4,'OBSERVE','OBSR000001','Observe Module',1,'2017-05-23 06:52:10','admin','','','','');

INSERT INTO `PRODUCT_MODULES` (`PROD_MOD_SYS_ID`,`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (1,1,2,1,'2017-05-23 06:55:10','admin','','','','');
INSERT INTO `PRODUCT_MODULES` (`PROD_MOD_SYS_ID`,`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (2,2,2,1,'2017-05-23 06:55:24','admin','','','','');
INSERT INTO `PRODUCT_MODULES` (`PROD_MOD_SYS_ID`,`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (3,3,2,1,'2017-05-23 06:55:30','admin','','','','');
INSERT INTO `PRODUCT_MODULES` (`PROD_MOD_SYS_ID`,`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (4,4,1,1,'2017-05-23 06:55:37','admin','','','','');
INSERT INTO `PRODUCT_MODULES` (`PROD_MOD_SYS_ID`,`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (5,5,2,1,'2017-05-23 06:55:43','admin','','','','');
INSERT INTO `PRODUCT_MODULES` (`PROD_MOD_SYS_ID`,`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (6,1,1,1,'2017-05-23 06:55:48','admin','','','','');
INSERT INTO `PRODUCT_MODULES` (`PROD_MOD_SYS_ID`,`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (7,4,3,1,'2017-05-23 06:55:59','admin','','','','');
INSERT INTO `PRODUCT_MODULES` (`PROD_MOD_SYS_ID`,`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (8,4,2,1,'2017-05-23 06:59:29','admin','','','','');

INSERT INTO `CUSTOMERS` (`CUSTOMER_SYS_ID`,`CUSTOMER_CODE`,`COMPANY_NAME`,`COMPANY_BUSINESS`,`LANDING_PROD_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`,`PASSWORD_EXPIRY_DAYS`,`DOMAIN_NAME`) VALUES (1,'SYNCHRONOSS','Synchronoss','Telecommunication',4,1,'2017-05-23 06:57:00','admin','','','','',360,'SYNCHRONOSS.COM');

INSERT INTO `CUSTOMER_PRODUCTS` (`CUST_PROD_SYS_ID`,`CUSTOMER_SYS_ID`,`PRODUCT_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (1,1,4,1,'2017-05-23 06:57:47','admin','','','','');

INSERT INTO `CUSTOMER_PRODUCT_MODULES` (`CUST_PROD_MOD_SYS_ID`,`CUST_PROD_SYS_ID`,`PROD_MOD_SYS_ID`,`CUSTOMER_SYS_ID`,`ACTIVE_STATUS_IND`,`MODULE_URL`,`DEFAULT`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (1,1,4,1,1,'/',1,'2017-05-23 06:59:37','admin','','','','');
INSERT INTO `CUSTOMER_PRODUCT_MODULES` (`CUST_PROD_MOD_SYS_ID`,`CUST_PROD_SYS_ID`,`PROD_MOD_SYS_ID`,`CUSTOMER_SYS_ID`,`ACTIVE_STATUS_IND`,`MODULE_URL`,`DEFAULT`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (2,1,8,1,1,'/',0,'2017-05-23 06:59:43','admin','','','','');
INSERT INTO `CUSTOMER_PRODUCT_MODULES` (`CUST_PROD_MOD_SYS_ID`,`CUST_PROD_SYS_ID`,`PROD_MOD_SYS_ID`,`CUSTOMER_SYS_ID`,`ACTIVE_STATUS_IND`,`MODULE_URL`,`DEFAULT`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (3,1,7,1,1,'/',0,'2017-05-23 06:59:51','admin','','','','');

INSERT INTO `CUSTOMER_PRODUCT_MODULE_FEATURES` (`CUST_PROD_MOD_FEATURE_SYS_ID`,`CUST_PROD_MOD_SYS_ID`,`DEFAULT_URL`,`FEATURE_NAME`,`FEATURE_DESC`,`FEATURE_CODE`,`FEATURE_TYPE`,`DEFAULT`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (2,1,'/','CANNED ANALYSIS','Canned Analysis','F0000000001','PARENT_F0000000001',1,1,'2017-05-23 08:25:15','admin','','','','');
INSERT INTO `CUSTOMER_PRODUCT_MODULE_FEATURES` (`CUST_PROD_MOD_FEATURE_SYS_ID`,`CUST_PROD_MOD_SYS_ID`,`DEFAULT_URL`,`FEATURE_NAME`,`FEATURE_DESC`,`FEATURE_CODE`,`FEATURE_TYPE`,`DEFAULT`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (3,1,'/','My Analysis','My Analysis','F0000000002','PARENT_F0000000002',0,1,'2017-05-23 08:26:05','admin','','','','');
INSERT INTO `CUSTOMER_PRODUCT_MODULE_FEATURES` (`CUST_PROD_MOD_FEATURE_SYS_ID`,`CUST_PROD_MOD_SYS_ID`,`DEFAULT_URL`,`FEATURE_NAME`,`FEATURE_DESC`,`FEATURE_CODE`,`FEATURE_TYPE`,`DEFAULT`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (4,1,'/','OPTIMIZATION','Optimization','F0000000003','CHILD_F0000000001',0,1,'2017-05-23 08:26:58','admin','','','','');
INSERT INTO `CUSTOMER_PRODUCT_MODULE_FEATURES` (`CUST_PROD_MOD_FEATURE_SYS_ID`,`CUST_PROD_MOD_SYS_ID`,`DEFAULT_URL`,`FEATURE_NAME`,`FEATURE_DESC`,`FEATURE_CODE`,`FEATURE_TYPE`,`DEFAULT`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`,`INACTIVATED_DATE`,`INACTIVATED_BY`,`MODIFIED_DATE`,`MODIFIED_BY`) VALUES (5,1,'/','DRAFTS','Drafts','F0000000004','CHILD_F0000000002',0,1,'2017-05-23 08:27:42','admin','','','','');

INSERT INTO `ROLES_TYPE` (`Roles_Type_Sys_Id`,`Roles_Type_Name`,`Roles_Type_Desc`,`ACTIVE_STATUS_IND`) VALUES (1,'ADMIN','Admin',1);
INSERT INTO `ROLES_TYPE` (`Roles_Type_Sys_Id`,`Roles_Type_Name`,`Roles_Type_Desc`,`ACTIVE_STATUS_IND`) VALUES (2,'USER','User',1);

INSERT INTO `ROLES` (`ROLE_SYS_ID`, `CUSTOMER_SYS_ID`, `ROLE_NAME`, `ROLE_CODE`, `ROLE_DESC`, `ROLE_TYPE`, `DATA_SECURITY_KEY`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('1', '1', 'ADMIN', 'SYNCHRONOSS_ADMIN_USER', 'Admin User', 'ADMIN', 'NA', '1', '2017-05-23 08:28:37', 'admin');
INSERT INTO `ROLES` (`ROLE_SYS_ID`, `CUSTOMER_SYS_ID`, `ROLE_NAME`, `ROLE_CODE`, `ROLE_DESC`, `ROLE_TYPE`, `DATA_SECURITY_KEY`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('2', '1', 'REPORT USER', 'SYNCHRONOSS_REPORTUSER_USER', 'Report User', 'USER', 'NA', '1', '2017-05-23 08:28:37', 'admin');
INSERT INTO `ROLES` (`ROLE_SYS_ID`, `CUSTOMER_SYS_ID`, `ROLE_NAME`, `ROLE_CODE`, `ROLE_DESC`, `ROLE_TYPE`, `DATA_SECURITY_KEY`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('3', '1', 'ANALYST', 'SYNCHRONOSS_ANALYST_USER', 'Analyst User', 'USER', 'ATT_ANALYST_DSK', '1', '2017-05-23 08:28:37', 'admin');
INSERT INTO `ROLES` (`ROLE_SYS_ID`, `CUSTOMER_SYS_ID`, `ROLE_NAME`, `ROLE_CODE`, `ROLE_DESC`, `ROLE_TYPE`, `DATA_SECURITY_KEY`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('4', '1', 'REVIEWER', 'SYNCHRONOSS_REVIEWER_USER', 'Reviewer User', 'USER', 'ATT_ANALYST_DSK', '1', '2017-05-23 08:28:37', 'admin');

INSERT INTO `users` (`USER_SYS_ID`, `USER_ID`, `EMAIL`, `ROLE_SYS_ID`, `CUSTOMER_SYS_ID`, `ENCRYPTED_PASSWORD`, `FIRST_NAME`, `MIDDLE_NAME`, `LAST_NAME`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('1', 'sawadmin@synchronoss.com', 'shwetha.somayaji@synchronoss.com', '1', '1', 'Y1lw/IcaP7G++mCJ/TlIGXnAM9Ud+b58niTjkxtdc4I=', 'shwetha', 'p', 'somayaji', '1', '2017-06-05 09:25:15', 'sawadmin@synchronoss.com');
INSERT INTO `users` (`USER_SYS_ID`, `USER_ID`, `EMAIL`, `ROLE_SYS_ID`, `CUSTOMER_SYS_ID`, `ENCRYPTED_PASSWORD`, `FIRST_NAME`, `MIDDLE_NAME`, `LAST_NAME`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('2', 'reportuser@synchronoss.com', 'shwetha.somayaji@synchronoss.com', '2', '1', 'Y1lw/IcaP7G++mCJ/TlIGXnAM9Ud+b58niTjkxtdc4I=', 'shwetha', 'p', 'somayaji', '1', '2017-06-05 09:25:15', 'sawadmin@synchronoss.com');
INSERT INTO `users` (`USER_SYS_ID`, `USER_ID`, `EMAIL`, `ROLE_SYS_ID`, `CUSTOMER_SYS_ID`, `ENCRYPTED_PASSWORD`, `FIRST_NAME`, `MIDDLE_NAME`, `LAST_NAME`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('3', 'analyst@synchronoss.com', 'shwetha.somayaji@synchronoss.com', '3', '1', 'Y1lw/IcaP7G++mCJ/TlIGXnAM9Ud+b58niTjkxtdc4I=', 'shwetha', 'p', 'somayaji', '1', '2017-06-05 09:25:15', 'sawadmin@synchronoss.com');
INSERT INTO `users` (`USER_SYS_ID`, `USER_ID`, `EMAIL`, `ROLE_SYS_ID`, `CUSTOMER_SYS_ID`, `ENCRYPTED_PASSWORD`, `FIRST_NAME`, `MIDDLE_NAME`, `LAST_NAME`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('4', 'reviewer@synchronoss.com', 'shwetha.somayaji@synchronoss.com', '4', '1', 'Y1lw/IcaP7G++mCJ/TlIGXnAM9Ud+b58niTjkxtdc4I=', 'shwetha', 'p', 'somayaji', '1', '2017-06-05 09:25:15', 'sawadmin@synchronoss.com');


INSERT INTO `privilege_codes` (`PRIVILEGE_CODES_NAME`, `PRIVILEGE_CODES_DESC`) VALUES ( 'ACCESS', 'Access/View');
INSERT INTO `privilege_codes` (`PRIVILEGE_CODES_NAME`, `PRIVILEGE_CODES_DESC`) VALUES ( 'CREATE', 'Allow Creation');
INSERT INTO `privilege_codes` (`PRIVILEGE_CODES_NAME`, `PRIVILEGE_CODES_DESC`) VALUES ( 'EXECUTE', 'Allow to Execute');
INSERT INTO `privilege_codes` (`PRIVILEGE_CODES_NAME`, `PRIVILEGE_CODES_DESC`) VALUES ( 'PUBLISH', 'Allow to Publish');
INSERT INTO `privilege_codes` (`PRIVILEGE_CODES_NAME`, `PRIVILEGE_CODES_DESC`) VALUES ( 'FORK', 'Allow Fork');
INSERT INTO `privilege_codes` (`PRIVILEGE_CODES_NAME`, `PRIVILEGE_CODES_DESC`) VALUES ( 'EDIT', 'Allow Editing');
INSERT INTO `privilege_codes` (`PRIVILEGE_CODES_NAME`, `PRIVILEGE_CODES_DESC`) VALUES ( 'EXPORT', 'Allow to Export');
INSERT INTO `privilege_codes` (`PRIVILEGE_CODES_NAME`, `PRIVILEGE_CODES_DESC`) VALUES ( 'DELETE', 'Allow to Delete');
INSERT INTO `privilege_codes` (`PRIVILEGE_CODES_NAME`, `PRIVILEGE_CODES_DESC`) VALUES ( 'ALL', 'Allow All Privileges');

INSERT INTO `privilege_groups` (`PRIVILEGE_GRP_CODE`, `PRIVILEGE_GRP_CODE_DESC`) VALUES ('PROD_PRIV', 'Privilege for product level');
INSERT INTO `privilege_groups` (`PRIVILEGE_GRP_CODE`, `PRIVILEGE_GRP_CODE_DESC`) VALUES ('MOD_PRIV', 'Privilege for module level');
INSERT INTO `privilege_groups` (`PRIVILEGE_GRP_CODE`, `PRIVILEGE_GRP_CODE_DESC`) VALUES ('FEAT_PRIV', 'Privilege for feature level');
INSERT INTO `privilege_groups` (`PRIVILEGE_GRP_CODE`, `PRIVILEGE_GRP_CODE_DESC`) VALUES ('ANLY_PRIV', 'Privilege for analysis level');


INSERT INTO PRIVILEGE_GROUP_CODES(PRIVILEGE_GRP_SYS_ID,PRIVILEGE_CODES_SYS_ID) VALUES (1,1);
INSERT INTO PRIVILEGE_GROUP_CODES(PRIVILEGE_GRP_SYS_ID,PRIVILEGE_CODES_SYS_ID) VALUES (2,1);
INSERT INTO PRIVILEGE_GROUP_CODES(PRIVILEGE_GRP_SYS_ID,PRIVILEGE_CODES_SYS_ID) VALUES (3,1);
INSERT INTO PRIVILEGE_GROUP_CODES(PRIVILEGE_GRP_SYS_ID,PRIVILEGE_CODES_SYS_ID) VALUES (3,6);
INSERT INTO PRIVILEGE_GROUP_CODES(PRIVILEGE_GRP_SYS_ID,PRIVILEGE_CODES_SYS_ID) VALUES (4,1);
INSERT INTO PRIVILEGE_GROUP_CODES(PRIVILEGE_GRP_SYS_ID,PRIVILEGE_CODES_SYS_ID) VALUES (4,3);
INSERT INTO PRIVILEGE_GROUP_CODES(PRIVILEGE_GRP_SYS_ID,PRIVILEGE_CODES_SYS_ID) VALUES (4,4);
INSERT INTO PRIVILEGE_GROUP_CODES(PRIVILEGE_GRP_SYS_ID,PRIVILEGE_CODES_SYS_ID) VALUES (4,5);
INSERT INTO PRIVILEGE_GROUP_CODES(PRIVILEGE_GRP_SYS_ID,PRIVILEGE_CODES_SYS_ID) VALUES (4,6);

INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`,`CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('25', '1', '1', '2', '1', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');
INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('26', '1', '1', '3', '1', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');
INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('27', '1', '1', '4', '1', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');
INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('28', '1', '1', '5', '1', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');
INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('29', '1', '1', '0', '1', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');
INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('30', '1', '0', '0', '1', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');

INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('36', '1', '1', '2', '2', '0', '32768', 'Access/View', '1', '2017-05-24 08:01:38', 'admin');
INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('37', '1', '1', '4', '2', '0', '35328', 'Access/View,Fork,Export', '1', '2017-05-24 08:01:38', 'admin');
INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('41', '1', '1', '0', '2', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');
INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('42', '1', '0', '0', '2', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');

INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('1', '1', '1', '2', '3', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');
INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('2', '1', '1', '3', '3', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');
INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('3', '1', '1', '4', '3', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');
INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('4', '1', '1', '5', '3', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');
INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('5', '1', '1', '0', '3', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');
INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('6', '1', '0', '0', '3', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');

INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('7', '1', '1', '2', '4', '0', '32768', 'Access/View', '1', '2017-05-24 08:01:38', 'admin');
INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('8', '1', '1', '4', '4', '0', '45568', 'Access/View,Execute,Publish,Export', '1', '2017-05-24 08:01:38', 'admin');
INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('9', '1', '1', '0', '4', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');
INSERT INTO `PRIVILEGES` (`PRIVILEGE_SYS_ID`, `CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) VALUES ('10', '1', '0', '0', '4', '0', '128', 'All', '1', '2017-05-24 08:01:38', 'admin');


insert into analysis(`CUST_PROD_MOD_FEATURE_SYS_ID`,`ANALYSIS_ID`,`ANALYSIS_NAME`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`) values(4,123456785420,'Order Revenue by Customers',1,now(),'admin');
insert into analysis(`CUST_PROD_MOD_FEATURE_SYS_ID`,`ANALYSIS_ID`,`ANALYSIS_NAME`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`) values(4,123456785431,'Incident density by States',1,now(),'admin');
insert into analysis(`CUST_PROD_MOD_FEATURE_SYS_ID`,`ANALYSIS_ID`,`ANALYSIS_NAME`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`) values(4,123456785432,'Incident density by States',1,now(),'admin');
insert into analysis(`CUST_PROD_MOD_FEATURE_SYS_ID`,`ANALYSIS_ID`,`ANALYSIS_NAME`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`) values(5,123456785421,'Cable Orders',1,now(),'admin');
insert into analysis(`CUST_PROD_MOD_FEATURE_SYS_ID`,`ANALYSIS_ID`,`ANALYSIS_NAME`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`) values(5,123456785433,'Service Requests',1,now(),'admin');
