/*******************************************************************************
 Filename:  V31__INSPECT_STREAM.sql
 Purpose:   To add new stream inspector in Workbench Module
 Date:      03-19-2020
********************************************************************************/


/*******************************************************************************
TABLE Scripts Starts
********************************************************************************/
-- Querying for appropriate ID's required to insert under Workbench module
select @l_cust_prod_mod_feature_sys_id := max(cust_prod_mod_feature_sys_id) FROM CUSTOMER_PRODUCT_MODULE_FEATURES;
Select @l_privilege_sys_id := max(privilege_sys_id) from privileges;

-- Storing rtis provision script in CUSTOMER_PRODUCT_MODULE_FEATURES and PRIVILEGES table
DELETE FROM CUSTOMER_PRODUCT_MODULE_FEATURES WHERE FEATURE_CODE like '%%STREAMINSPECTOR411';
INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_FEATURE_SYS_ID,CUST_PROD_MOD_SYS_ID,DEFAULT_URL,FEATURE_NAME,FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,`DEFAULT`,ACTIVE_STATUS_IND,SYSTEM_CATEGORY, CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
SELECT  @l_cust_prod_mod_feature_sys_id + 1 CUST_PROD_MOD_FEATURE_SYS_ID, 4 CUST_PROD_MOD_SYS_ID, 'stream/inspector' DEFAULT_URL, 'Stream Inspector' FEATURE_NAME, 'STREAM INSPECTOR' FEATURE_DESC, concat('SYNCHRONOSS', 'STREAMINSPECTOR411') FEATURE_CODE, concat('CHILD_','DSM0000001') FEATURE_TYPE,
0 'DEFAULT', 1 ACTIVE_STATUS_IND, 0 SYSTEM_CATEGORY, now() CREATED_DATE, 'admin' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;

DELETE FROM PRIVILEGES WHERE CUST_PROD_MOD_FEATURE_SYS_ID = @l_cust_prod_mod_feature_sys_id + 1;
INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
  SELECT @l_privilege_sys_id+1 PRIVILEGE_SYS_ID, '1' CUST_PROD_SYS_ID, 4 CUST_PROD_MOD_SYS_ID, @l_cust_prod_mod_feature_sys_id + 1 CUST_PROD_MOD_FEATURE_SYS_ID, '1' ROLE_SYS_ID, '0' ANALYSIS_SYS_ID, '32768' PRIVILEGE_CODE, 'All' PRIVILEGE_DESC, '1' ACTIVE_STATUS_IND, now() CREATED_DATE, 'admin' CREATED_BY;



/*******************************************************************************
Inserting categories and sub-categories for Workbench module
********************************************************************************/

/*******************************************************************************
TABLE Scripts Ends
********************************************************************************/
