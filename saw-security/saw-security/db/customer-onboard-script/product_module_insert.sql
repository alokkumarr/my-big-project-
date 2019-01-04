DROP PROCEDURE product_module_insert ;

DELIMITER //
CREATE PROCEDURE product_module_insert (IN l_customer_code varchar(50) , IN l_product_name varchar(50), IN l_product_code varchar(50), IN l_module_name varchar(50), IN l_module_code varchar(50), IN l_module_desc varchar(50))

 BEGIN

	DECLARE l_customer_sys_id  INT ;
  	DECLARE l_product_sys_id  INT ;
   	DECLARE l_module_sys_id_analyze  INT ;
   	DECLARE l_incremental_product_sys_id INT;
   	DECLARE l_incremental_module_sys_id INT;
   	DECLARE l_incremental_prod_mod_sys_id INT;
   	DECLARE l_new_module_sys_id INT;
   	DECLARE l_new_prod_mod_sys_id INT;
   	DECLARE l_cust_prod_mod_sys_id INT;
   	DECLARE l_cust_prod_sys_id INT;
    DECLARE l_cust_prod_mod_feature_sys_id INT;
    DECLARE l_privilege_sys_id INT;


SELECT max(PRODUCT_SYS_ID)+1 into l_incremental_product_sys_id from PRODUCTS;
select l_incremental_product_sys_id;

select customer_sys_id into l_customer_sys_id  from customers where CUSTOMER_CODE = l_customer_code;
select l_customer_sys_id;


IF NOT exists(Select PRODUCT_NAME from Products where PRODUCT_NAME=l_product_name)
THEN
INSERT INTO PRODUCTS (PRODUCT_SYS_ID,PRODUCT_NAME,PRODUCT_CODE,PRODUCT_DESC,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
SELECT l_incremental_product_sys_id PRODUCT_SYS_ID,l_product_name PRODUCT_NAME,l_product_code PRODUCT_CODE,l_product_name PRODUCT_DESC,1 ACTIVE_STATUS_IND,Now() CREATED_DATE,'admin' CREATED_BY,NULL INACTIVATED_DATE,NULL INACTIVATED_BY,
NULL MODIFIED_DATE,NULL MODIFIED_BY;
select l_incremental_product_sys_id;

select max(cust_prod_sys_id)+1 into l_cust_prod_sys_id from customer_products;
select l_cust_prod_sys_id;

Select PRODUCT_SYS_ID into l_product_sys_id
from PRODUCTS
where PRODUCT_NAME = l_product_name;
select l_product_sys_id;


INSERT INTO CUSTOMER_PRODUCTS (CUST_PROD_SYS_ID,CUSTOMER_SYS_ID,PRODUCT_SYS_ID,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
Select l_cust_prod_sys_id CUST_PROD_SYS_ID,l_customer_sys_id CUSTOMER_SYS_ID,l_product_sys_id PRODUCT_SYS_ID,1 ACTIVE_STATUS_IND,
Now() CREATED_DATE,'onboard' CREATED_BY,NULL INACTIVATED_DATE,NULL INACTIVATED_BY,NULL MODIFIED_DATE,NULL MODIFIED_BY;
Select l_cust_prod_sys_id;
END IF;

Select PRODUCT_SYS_ID into l_product_sys_id
from PRODUCTS
where PRODUCT_NAME = l_product_name;
Select l_product_sys_id;

Select max(MODULE_SYS_ID)+1 into l_incremental_module_sys_id
from MODULES;
Select l_incremental_module_sys_id;

IF NOT exists(Select MODULE_CODE from MODULES where MODULE_CODE=l_module_code)
THEN
INSERT INTO MODULES (MODULE_SYS_ID,MODULE_NAME,MODULE_CODE,MODULE_DESC,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
SELECT l_incremental_module_sys_id MODULE_SYS_ID ,l_module_name MODULE_NAME,l_module_code MODULE_CODE,l_module_desc MODULE_DESC,1 ACTIVE_STATUS_IND, NOW() CREATED_DATE,'admin' CREATED_BY,NULL INACTIVATED_DATE,NULL INACTIVATED_BY,
NULL MODIFIED_DATE,NULL MODIFIED_BY;
Select l_incremental_module_sys_id;
END IF;

SELECT max(PROD_MOD_SYS_ID)+1 into l_incremental_prod_mod_sys_id from PRODUCT_MODULES;
Select l_incremental_prod_mod_sys_id;

select MODULE_SYS_ID into l_new_module_sys_id from MODULES where MODULE_CODE = l_module_code ;
Select l_new_module_sys_id;


INSERT INTO PRODUCT_MODULES (PROD_MOD_SYS_ID,PRODUCT_SYS_ID,MODULE_SYS_ID,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
SELECT l_incremental_prod_mod_sys_id PROD_MOD_SYS_ID,l_product_sys_id PRODUCT_SYS_ID,l_new_module_sys_id MODULE_SYS_ID,1 ACTIVE_STATUS_IND,Now() CREATED_DATE,'admin' CREATED_BY,
NULL INACTIVATED_DATE,NULL INACTIVATED_BY,NULL MODIFIED_DATE,NULL MODIFIED_BY;
Select l_incremental_prod_mod_sys_id;


 select PROD_MOD_SYS_ID into l_new_prod_mod_sys_id  from PRODUCT_MODULES  where PRODUCT_SYS_ID = l_product_sys_id  and MODULE_SYS_ID = l_new_module_sys_id ;
 Select l_new_prod_mod_sys_id;

 select max(cust_prod_mod_sys_id)+1 into l_cust_prod_mod_sys_id from customer_product_modules ;
 select l_cust_prod_mod_sys_id;

 select cust_prod_sys_id into l_cust_prod_sys_id from customer_products where CUSTOMER_SYS_ID = l_customer_sys_id and PRODUCT_SYS_ID = l_product_sys_id;
 select l_cust_prod_sys_id;


 INSERT INTO CUSTOMER_PRODUCT_MODULES (CUST_PROD_MOD_SYS_ID,CUST_PROD_SYS_ID,PROD_MOD_SYS_ID,CUSTOMER_SYS_ID,ACTIVE_STATUS_IND,MODULE_URL,`DEFAULT`,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
 SELECT  l_cust_prod_mod_sys_id CUST_PROD_MOD_SYS_ID, l_cust_prod_sys_id CUST_PROD_SYS_ID, l_new_prod_mod_sys_id PROD_MOD_SYS_ID,
 l_customer_sys_id CUSTOMER_SYS_ID, 1 ACTIVE_STATUS_IND, '/' MODULE_URL, 1  'DEFAULT', now() CREATED_DATE, 'onboard' CREATED_BY,
 NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;
 Select l_cust_prod_mod_sys_id;


 select max(cust_prod_mod_feature_sys_id)+1 into l_cust_prod_mod_feature_sys_id from customer_product_module_features;
 select l_cust_prod_mod_feature_sys_id;

INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_FEATURE_SYS_ID,CUST_PROD_MOD_SYS_ID,DEFAULT_URL,FEATURE_NAME,FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,`DEFAULT`,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
  select  l_cust_prod_mod_feature_sys_id CUST_PROD_MOD_FEATURE_SYS_ID, l_cust_prod_mod_sys_id CUST_PROD_MOD_SYS_ID,
 'correlation' DEFAULT_URL, 'Correlation' FEATURE_NAME, 'Correlation Viewer' FEATURE_DESC, concat(l_customer_code, 'CORR0000001') FEATURE_CODE,
 concat ('PARENT_',concat(l_customer_code, 'CORR0000001')) FEATURE_TYPE, 0 'DEFAULT', 1 ACTIVE_STATUS_IND, now() CREATED_DATE,
 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;
 Select l_cust_prod_mod_feature_sys_id;

 Select max(privilege_sys_id)+1 into l_privilege_sys_id from privileges;
 Select l_privilege_sys_id;

INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
  SELECT l_privilege_sys_id PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 l_cust_prod_mod_sys_id CUST_PROD_MOD_SYS_ID,
 l_cust_prod_mod_feature_sys_id CUST_PROD_MOD_FEATURE_SYS_ID,
 '1' ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '128' PRIVILEGE_CODE,
 'All' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;
 Select l_privilege_sys_id;

 END;
//
DELIMITER ;
