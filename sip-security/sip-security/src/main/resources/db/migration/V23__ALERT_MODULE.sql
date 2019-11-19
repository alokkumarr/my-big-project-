/*******************************************************************************
 Add default category and subcategory to Alert Module
********************************************************************************/
DROP PROCEDURE IF EXISTS alerts_insert;

DELIMITER //
CREATE PROCEDURE alerts_insert()

BEGIN

DECLARE n INT;
DECLARE i INT;
DECLARE l_PRODUCT_SYS_ID INT;
DECLARE l_MODULE_SYS_ID INT;
DECLARE l_PROD_MOD_SYS_ID INT;
DECLARE l_CUST_PROD_MOD_SYS_ID INT;
DECLARE l_CUSTOMER_PROD_MOD_SYS_ID_1 INT;
DECLARE l_CUST_PROD_MOD_FEATURE_SYS_ID_1 INT;
DECLARE l_product_code varchar(50);
DECLARE l_customer_sys_id INT;
DECLARE l_customer_prod_sys_id INT;
DECLARE l_role_sys_id INT;

UPDATE MODULES SET MODULE_NAME='ALERTS' WHERE MODULE_NAME= 'ALERT';

SELECT MODULE_SYS_ID into l_MODULE_SYS_ID from MODULES WHERE MODULE_CODE = 'ALRT000001';
select l_MODULE_SYS_ID;

SELECT PROD_MOD_SYS_ID into l_PROD_MOD_SYS_ID FROM `PRODUCT_MODULES` WHERE MODULE_SYS_ID = l_MODULE_SYS_ID;
select l_PROD_MOD_SYS_ID;

-- Delete the dummy entries which were made till date for alerts and re insert them properly based on the Product and customers.
DELETE FROM PRODUCT_MODULES where MODULE_SYS_ID = l_MODULE_SYS_ID;

DELETE FROM CUSTOMER_PRODUCT_MODULES where PROD_MOD_SYS_ID = l_PROD_MOD_SYS_ID;

INSERT INTO MODULE_PRIVILEGES (`MODULE_SYS_ID`,`PRIVILEGE_CODES_SYS_ID`) SELECT M.MODULE_SYS_ID ,PC.PRIVILEGE_CODES_SYS_ID FROM MODULES M , privilege_codes PC WHERE M.MODULE_NAME = 'ALERTS' and PC.PRIVILEGE_CODES_NAME = 'ACCESS';

SELECT COUNT(*) FROM PRODUCTS p inner join customer_products cp on p.PRODUCT_SYS_ID=cp.PRODUCT_SYS_ID INTO n ;
select n;

SET i = 0;
select i;

WHILE i < n DO

SELECT p.PRODUCT_SYS_ID into l_PRODUCT_SYS_ID from PRODUCTS p inner join customer_products cp on p.PRODUCT_SYS_ID=cp.PRODUCT_SYS_ID LIMIT i, 1;
select l_PRODUCT_SYS_ID;

SELECT cp.CUSTOMER_SYS_ID into l_customer_sys_id from PRODUCTS p inner join customer_products cp on p.PRODUCT_SYS_ID=cp.PRODUCT_SYS_ID LIMIT i, 1;
select l_customer_sys_id;

SELECT cp.CUST_PROD_SYS_ID into l_customer_prod_sys_id from PRODUCTS p inner join customer_products cp on p.PRODUCT_SYS_ID=cp.PRODUCT_SYS_ID LIMIT i, 1;
select l_customer_prod_sys_id;

SELECT PRODUCT_CODE into l_product_code from PRODUCTS where PRODUCT_SYS_ID = l_PRODUCT_SYS_ID;
select l_product_code;

INSERT INTO `PRODUCT_MODULES` (`PRODUCT_SYS_ID`,`MODULE_SYS_ID`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`)
SELECT l_PRODUCT_SYS_ID PRODUCT_SYS_ID, l_MODULE_SYS_ID MODULE_SYS_ID,1 ACTIVE_STATUS_IND,now() CREATED_DATE,'admin' CREATED_BY;

SELECT PROD_MOD_SYS_ID into l_PROD_MOD_SYS_ID FROM `PRODUCT_MODULES` WHERE PRODUCT_SYS_ID = l_PRODUCT_SYS_ID AND MODULE_SYS_ID = l_MODULE_SYS_ID;
select l_PROD_MOD_SYS_ID;

INSERT INTO `CUSTOMER_PRODUCT_MODULES`(`CUST_PROD_SYS_ID`,`PROD_MOD_SYS_ID`,`CUSTOMER_SYS_ID`,`ACTIVE_STATUS_IND`,`MODULE_URL`,`DEFAULT`,`CREATED_DATE`,`CREATED_BY`)
SELECT l_customer_prod_sys_id CUST_PROD_SYS_ID,l_PROD_MOD_SYS_ID PROD_MOD_SYS_ID,l_customer_sys_id CUSTOMER_SYS_ID,1 ACTIVE_STATUS_IND,'/' MODULE_URL,1 'DEFAULT',now() CREATED_DATE,'admin' CREATED_BY;

SELECT CUST_PROD_MOD_SYS_ID into l_CUST_PROD_MOD_SYS_ID FROM CUSTOMER_PRODUCT_MODULES WHERE PROD_MOD_SYS_ID = l_PROD_MOD_SYS_ID ;
select l_CUST_PROD_MOD_SYS_ID;

INSERT INTO `CUSTOMER_PRODUCT_MODULE_FEATURES` (`CUST_PROD_MOD_SYS_ID`,`DEFAULT_URL`,`FEATURE_NAME`,`FEATURE_DESC`,`FEATURE_CODE`,`FEATURE_TYPE`,`DEFAULT`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`) SELECT l_CUST_PROD_MOD_SYS_ID CUST_PROD_MOD_SYS_ID,'/' DEFAULT_URL,'Alerts' FEATURE_NAME,'Alerts' FEATURE_DESC,concat(l_product_code, 'ALERT12') FEATURE_CODE,concat('PARENT_',concat(l_product_code,'ALERT12')) FEATURE_TYPE,0 'DEFAULT',1 ACTIVE_STATUS_IND,now() CREATED_DATE,'admin' CREATED_BY;
select l_CUST_PROD_MOD_SYS_ID;

SELECT CUST_PROD_MOD_SYS_ID into l_CUSTOMER_PROD_MOD_SYS_ID_1 FROM CUSTOMER_PRODUCT_MODULE_FEATURES WHERE FEATURE_CODE=concat(l_product_code, 'ALERT12');
select l_CUSTOMER_PROD_MOD_SYS_ID_1;

SELECT CUST_PROD_MOD_FEATURE_SYS_ID into l_CUST_PROD_MOD_FEATURE_SYS_ID_1 FROM CUSTOMER_PRODUCT_MODULE_FEATURES WHERE FEATURE_CODE=concat(l_product_code, 'ALERT12');
select l_CUST_PROD_MOD_FEATURE_SYS_ID_1;

SELECT ROLE_SYS_ID into l_role_sys_id FROM ROLES WHERE CUSTOMER_SYS_ID = l_customer_sys_id AND ROLE_TYPE = 'ADMIN';
select l_role_sys_id;

INSERT INTO `PRIVILEGES` (`CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) SELECT '1' CUST_PROD_SYS_ID, l_CUSTOMER_PROD_MOD_SYS_ID_1 CUST_PROD_MOD_SYS_ID, l_CUST_PROD_MOD_FEATURE_SYS_ID_1 CUST_PROD_MOD_FEATURE_SYS_ID, l_role_sys_id ROLE_SYS_ID, '0' ANALYSIS_SYS_ID, '128' PRIVILEGE_CODE, 'All' PRIVILEGE_DESC, '1' ACTIVE_STATUS_IND, NOW() CREATED_DATE, 'admin' CREATED_BY;

INSERT INTO `CUSTOMER_PRODUCT_MODULE_FEATURES` (`CUST_PROD_MOD_SYS_ID`,`DEFAULT_URL`,`FEATURE_NAME`,`FEATURE_DESC`,`FEATURE_CODE`,`FEATURE_TYPE`,`DEFAULT`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`) SELECT l_CUST_PROD_MOD_SYS_ID CUST_PROD_MOD_SYS_ID,'view' DEFAULT_URL,'View Alerts' FEATURE_NAME,'View Alerts' FEATURE_DESC,concat(l_product_code, 'VIEWALERT') FEATURE_CODE,concat('CHILD_',concat(l_product_code,'ALERT12')) FEATURE_TYPE,0 'DEFAULT',1 ACTIVE_STATUS_IND,now() CREATED_DATE,'admin' CREATED_BY;

SELECT CUST_PROD_MOD_SYS_ID into l_CUSTOMER_PROD_MOD_SYS_ID_1 FROM CUSTOMER_PRODUCT_MODULE_FEATURES WHERE FEATURE_CODE=concat(l_product_code, 'VIEWALERT');
select l_CUSTOMER_PROD_MOD_SYS_ID_1;

SELECT CUST_PROD_MOD_FEATURE_SYS_ID into l_CUST_PROD_MOD_FEATURE_SYS_ID_1 FROM CUSTOMER_PRODUCT_MODULE_FEATURES WHERE FEATURE_CODE=concat(l_product_code, 'VIEWALERT');
select l_CUST_PROD_MOD_FEATURE_SYS_ID_1;

INSERT INTO `PRIVILEGES` (`CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) SELECT '1' CUST_PROD_SYS_ID, l_CUSTOMER_PROD_MOD_SYS_ID_1 CUST_PROD_MOD_SYS_ID, l_CUST_PROD_MOD_FEATURE_SYS_ID_1 CUST_PROD_MOD_FEATURE_SYS_ID, l_role_sys_id ROLE_SYS_ID, '0' ANALYSIS_SYS_ID, '32768' PRIVILEGE_CODE, 'All' PRIVILEGE_DESC, '1' ACTIVE_STATUS_IND, NOW() CREATED_DATE, 'admin' CREATED_BY;

INSERT INTO `CUSTOMER_PRODUCT_MODULE_FEATURES` (`CUST_PROD_MOD_SYS_ID`,`DEFAULT_URL`,`FEATURE_NAME`,`FEATURE_DESC`,`FEATURE_CODE`,`FEATURE_TYPE`,`DEFAULT`,`ACTIVE_STATUS_IND`,`CREATED_DATE`,`CREATED_BY`) SELECT l_CUST_PROD_MOD_SYS_ID CUST_PROD_MOD_SYS_ID,'configure' DEFAULT_URL,'Configure Alerts' FEATURE_NAME,'Configure Alerts' FEATURE_DESC,concat(l_product_code, 'CONFIGUREALERT') FEATURE_CODE,concat('CHILD_',concat(l_product_code,'ALERT12')) FEATURE_TYPE,0 'DEFAULT',1 ACTIVE_STATUS_IND,now() CREATED_DATE,'admin' CREATED_BY;

SELECT CUST_PROD_MOD_SYS_ID into l_CUSTOMER_PROD_MOD_SYS_ID_1 FROM CUSTOMER_PRODUCT_MODULE_FEATURES WHERE FEATURE_CODE=concat(l_product_code, 'CONFIGUREALERT');
select l_CUSTOMER_PROD_MOD_SYS_ID_1;

SELECT CUST_PROD_MOD_FEATURE_SYS_ID into l_CUST_PROD_MOD_FEATURE_SYS_ID_1 FROM CUSTOMER_PRODUCT_MODULE_FEATURES WHERE FEATURE_CODE=concat(l_product_code, 'CONFIGUREALERT');
select l_CUST_PROD_MOD_FEATURE_SYS_ID_1;

INSERT INTO `PRIVILEGES` (`CUST_PROD_SYS_ID`, `CUST_PROD_MOD_SYS_ID`, `CUST_PROD_MOD_FEATURE_SYS_ID`, `ROLE_SYS_ID`, `ANALYSIS_SYS_ID`, `PRIVILEGE_CODE`, `PRIVILEGE_DESC`, `ACTIVE_STATUS_IND`, `CREATED_DATE`, `CREATED_BY`) SELECT '1' CUST_PROD_SYS_ID, l_CUSTOMER_PROD_MOD_SYS_ID_1 CUST_PROD_MOD_SYS_ID, l_CUST_PROD_MOD_FEATURE_SYS_ID_1 CUST_PROD_MOD_FEATURE_SYS_ID, l_role_sys_id ROLE_SYS_ID, '0' ANALYSIS_SYS_ID, '32768' PRIVILEGE_CODE, 'All' PRIVILEGE_DESC, '1' ACTIVE_STATUS_IND, NOW() CREATED_DATE, 'admin' CREATED_BY;


SET i = i + 1;

END WHILE;

End;
//

DELIMITER ;

call alerts_insert();

/*******************************************************************************
   Modifying the onboard_customer procedure
  ********************************************************************************/


DROP PROCEDURE onboard_customer ;

DELIMITER //
CREATE PROCEDURE onboard_customer (IN l_customer_code varchar(50) , IN l_product_name varchar(50), IN l_product_code varchar(50), IN l_email varchar(50), IN l_first_name varchar(50), IN l_middle_name varchar(50), IN l_last_name varchar(50), IN is_jv_customer tinyint(4), IN filter_by_customer_code tinyint(4))

 BEGIN


   DECLARE l_customer_sys_id  INT ;
   DECLARE l_product_sys_id  INT ;
   DECLARE l_module_sys_id_analyze  INT ;
   DECLARE l_module_sys_id_observe  INT ;
   DECLARE l_module_sys_id_workbench  INT ;
   DECLARE l_config_val_sys_id INT ;
   DECLARE l_module_sys_id_alert  INT ;

   DECLARE l_cust_prod_sys_id INT ;
   DECLARE l_prod_mod_sys_id_analyze INT ;
   DECLARE l_prod_mod_sys_id_observe INT ;
   DECLARE l_prod_mod_sys_id_workbench INT ;
   DECLARE l_prod_mod_sys_id_alerts  INT ;


   DECLARE l_cust_prod_mod_sys_id  INT ;
   DECLARE l_cust_prod_mod_feature_sys_id INT;
   DECLARE l_role_sys_id INT;
   DECLARE l_user_sys_id INT;
   DECLARE l_privilege_sys_id INT;
   DECLARE l_incremental_product_sys_id INT;
   DECLARE l_incremental_prod_mod_sys_id INT ;

 DECLARE exit handler for sqlexception

  BEGIN
    -- ERROR
    ROLLBACK;
    SELECT 'Error occured';
  END;

  DECLARE exit handler for sqlwarning
  BEGIN
    -- WARNING
    ROLLBACK;
    SELECT 'Warning occured';
  END;

  START TRANSACTION;


SELECT max(PRODUCT_SYS_ID)+1 into l_incremental_product_sys_id from PRODUCTS;


IF NOT exists(Select PRODUCT_CODE from Products where PRODUCT_CODE=l_product_code)
THEN
INSERT INTO PRODUCTS (PRODUCT_SYS_ID,PRODUCT_NAME,PRODUCT_CODE,PRODUCT_DESC,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
SELECT l_incremental_product_sys_id PRODUCT_SYS_ID,l_product_name PRODUCT_NAME,l_product_code PRODUCT_CODE,l_product_name PRODUCT_DESC,1 ACTIVE_STATUS_IND,Now() CREATED_DATE,'admin' CREATED_BY,NULL INACTIVATED_DATE,NULL INACTIVATED_BY,
NULL MODIFIED_DATE,NULL MODIFIED_BY;
END IF;

SELECT max(PROD_MOD_SYS_ID)+1 into l_incremental_prod_mod_sys_id from PRODUCT_MODULES;

select PRODUCT_SYS_ID into l_product_sys_id
from PRODUCTS
where PRODUCT_CODE = l_product_code;

select MODULE_SYS_ID into l_module_sys_id_analyze from MODULES where MODULE_NAME = 'ANALYZE' ;

select  MODULE_SYS_ID into l_module_sys_id_observe from MODULES where MODULE_NAME = 'OBSERVE' LIMIT 1 ;

select  MODULE_SYS_ID into l_module_sys_id_workbench from MODULES where MODULE_NAME = 'WORKBENCH' ;

select  MODULE_SYS_ID into l_module_sys_id_alert from MODULES where MODULE_NAME = 'ALERTS' ;

INSERT INTO PRODUCT_MODULES (PROD_MOD_SYS_ID,PRODUCT_SYS_ID,MODULE_SYS_ID,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
SELECT l_incremental_prod_mod_sys_id PROD_MOD_SYS_ID,l_product_sys_id PRODUCT_SYS_ID,l_module_sys_id_analyze MODULE_SYS_ID,1 ACTIVE_STATUS_IND,Now() CREATED_DATE,'admin' CREATED_BY,
NULL INACTIVATED_DATE,NULL INACTIVATED_BY,NULL MODIFIED_DATE,NULL MODIFIED_BY;

INSERT INTO PRODUCT_MODULES (PROD_MOD_SYS_ID,PRODUCT_SYS_ID,MODULE_SYS_ID,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
SELECT l_incremental_prod_mod_sys_id+1 PROD_MOD_SYS_ID,l_product_sys_id PRODUCT_SYS_ID,l_module_sys_id_observe MODULE_SYS_ID,1 ACTIVE_STATUS_IND,Now() CREATED_DATE,'admin' CREATED_BY,
NULL INACTIVATED_DATE,NULL INACTIVATED_BY,NULL MODIFIED_DATE,NULL MODIFIED_BY;

INSERT INTO PRODUCT_MODULES (PROD_MOD_SYS_ID,PRODUCT_SYS_ID,MODULE_SYS_ID,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
SELECT l_incremental_prod_mod_sys_id+2 PROD_MOD_SYS_ID,l_product_sys_id PRODUCT_SYS_ID,l_module_sys_id_workbench MODULE_SYS_ID,1 ACTIVE_STATUS_IND,Now() CREATED_DATE,'admin' CREATED_BY,
NULL INACTIVATED_DATE,NULL INACTIVATED_BY,NULL MODIFIED_DATE,NULL MODIFIED_BY;

INSERT INTO PRODUCT_MODULES (PROD_MOD_SYS_ID,PRODUCT_SYS_ID,MODULE_SYS_ID,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
SELECT l_incremental_prod_mod_sys_id+3 PROD_MOD_SYS_ID,l_product_sys_id PRODUCT_SYS_ID,l_module_sys_id_alert MODULE_SYS_ID,1 ACTIVE_STATUS_IND,Now() CREATED_DATE,'admin' CREATED_BY,
NULL INACTIVATED_DATE,NULL INACTIVATED_BY,NULL MODIFIED_DATE,NULL MODIFIED_BY;


select max(customer_sys_id)+1 into l_customer_sys_id  from customers;
select l_customer_sys_id;


INSERT INTO CUSTOMERS (CUSTOMER_SYS_ID,CUSTOMER_CODE,COMPANY_NAME,COMPANY_BUSINESS,LANDING_PROD_SYS_ID,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY,PASSWORD_EXPIRY_DAYS,DOMAIN_NAME, IS_JV_CUSTOMER)
SELECT l_customer_sys_id CUSTOMER_SYS_ID,l_customer_code CUSTOMER_CODE,l_customer_code COMPANY_NAME,'Telecommunication' COMPANY_BUSINESS,
l_product_sys_id LANDING_PROD_SYS_ID,1 ACTIVE_STATUS_IND,Now() CREATED_DATE,'onboard' CREATED_BY,NULL INACTIVATED_DATE,NULL INACTIVATED_BY,
NULL MODIFIED_DATE,NULL MODIFIED_BY,
360 PASSWORD_EXPIRY_DAYS,concat(l_customer_code,'.COM') DOMAIN_NAME,is_jv_customer IS_JV_CUSTOMER;

select max(CONFIG_VAL_SYS_ID)+1 into l_config_val_sys_id  from CONFIG_VAL;

INSERT INTO CONFIG_VAL (CONFIG_VAL_SYS_ID,CONFIG_VAL_CODE,CONFIG_VALUE,CONFIG_VAL_DESC,CONFIG_VAL_OBJ_TYPE,CONFIG_VAL_OBJ_GROUP,ACTIVE_STATUS_IND, CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY,FILTER_BY_CUSTOMER_CODE)
SELECT l_config_val_sys_id CONFIG_VAL_SYS_ID, 'es-analysis-auto-refresh' CONFIG_VAL_CODE,NULL CONFIG_VALUE,'Make Charts,Pivots and ES Reports Execute each time when land on View Analysis Page' CONFIG_VAL_DESC,'CUSTOMER' CONFIG_VAL_OBJ_TYPE,
l_customer_code CONFIG_VAL_OBJ_GROUP, 1 ACTIVE_STATUS_IND, Now() CREATED_DATE,'onboard' CREATED_BY,NULL INACTIVATED_DATE,NULL INACTIVATED_BY,NULL MODIFIED_DATE,NULL MODIFIED_BY,filter_by_customer_code FILTER_BY_CUSTOMER_CODE;

select max(cust_prod_sys_id)+1 into l_cust_prod_sys_id from customer_products;

INSERT INTO CUSTOMER_PRODUCTS (CUST_PROD_SYS_ID,CUSTOMER_SYS_ID,PRODUCT_SYS_ID,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
Select l_cust_prod_sys_id CUST_PROD_SYS_ID,l_customer_sys_id CUSTOMER_SYS_ID,l_product_sys_id PRODUCT_SYS_ID,1 ACTIVE_STATUS_IND,
Now() CREATED_DATE,'onboard' CREATED_BY,NULL INACTIVATED_DATE,NULL INACTIVATED_BY,NULL MODIFIED_DATE,NULL MODIFIED_BY;



select max(PROD_MOD_SYS_ID)    into l_prod_mod_sys_id_analyze  from PRODUCT_MODULES  where PRODUCT_SYS_ID = l_product_sys_id  and MODULE_SYS_ID = l_module_sys_id_analyze ;


 select max(cust_prod_mod_sys_id)+1 into l_cust_prod_mod_sys_id from customer_product_modules ;

 INSERT INTO CUSTOMER_PRODUCT_MODULES (CUST_PROD_MOD_SYS_ID,CUST_PROD_SYS_ID,PROD_MOD_SYS_ID,CUSTOMER_SYS_ID,ACTIVE_STATUS_IND,MODULE_URL,`DEFAULT`,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
 SELECT  l_cust_prod_mod_sys_id CUST_PROD_MOD_SYS_ID, l_cust_prod_sys_id CUST_PROD_SYS_ID, l_prod_mod_sys_id_analyze PROD_MOD_SYS_ID,
 l_customer_sys_id CUSTOMER_SYS_ID, 1 ACTIVE_STATUS_IND, '/' MODULE_URL, 1  'DEFAULT', now() CREATED_DATE, 'onboard' CREATED_BY,
 NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;

 select max(PROD_MOD_SYS_ID)    into l_prod_mod_sys_id_observe from PRODUCT_MODULES  where PRODUCT_SYS_ID = l_product_sys_id and MODULE_SYS_ID = l_module_sys_id_observe ;


 INSERT INTO CUSTOMER_PRODUCT_MODULES (CUST_PROD_MOD_SYS_ID,CUST_PROD_SYS_ID,PROD_MOD_SYS_ID,CUSTOMER_SYS_ID,ACTIVE_STATUS_IND,MODULE_URL,`DEFAULT`,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
 SELECT  l_cust_prod_mod_sys_id+1 CUST_PROD_MOD_SYS_ID, l_cust_prod_sys_id CUST_PROD_SYS_ID, l_prod_mod_sys_id_observe PROD_MOD_SYS_ID,
 l_customer_sys_id CUSTOMER_SYS_ID, 1 ACTIVE_STATUS_IND, '/' MODULE_URL, 0  'DEFAULT', now() CREATED_DATE,
 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;

 select max(PROD_MOD_SYS_ID)    into l_prod_mod_sys_id_workbench from PRODUCT_MODULES  where PRODUCT_SYS_ID = l_product_sys_id and MODULE_SYS_ID = l_module_sys_id_workbench ;

 INSERT INTO CUSTOMER_PRODUCT_MODULES (CUST_PROD_MOD_SYS_ID,CUST_PROD_SYS_ID,PROD_MOD_SYS_ID,CUSTOMER_SYS_ID,ACTIVE_STATUS_IND,MODULE_URL,`DEFAULT`,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
 SELECT  l_cust_prod_mod_sys_id+2 CUST_PROD_MOD_SYS_ID, l_cust_prod_sys_id CUST_PROD_SYS_ID, l_prod_mod_sys_id_workbench PROD_MOD_SYS_ID,
 l_customer_sys_id CUSTOMER_SYS_ID, 1 ACTIVE_STATUS_IND, '/' MODULE_URL, 0  'DEFAULT', now() CREATED_DATE,
 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;

 select max(PROD_MOD_SYS_ID)    into l_prod_mod_sys_id_alerts from PRODUCT_MODULES  where PRODUCT_SYS_ID = l_product_sys_id and MODULE_SYS_ID = l_module_sys_id_alert ;

 INSERT INTO CUSTOMER_PRODUCT_MODULES (CUST_PROD_MOD_SYS_ID,CUST_PROD_SYS_ID,PROD_MOD_SYS_ID,CUSTOMER_SYS_ID,ACTIVE_STATUS_IND,MODULE_URL,`DEFAULT`,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
 SELECT  l_cust_prod_mod_sys_id+3 CUST_PROD_MOD_SYS_ID, l_cust_prod_sys_id CUST_PROD_SYS_ID, l_prod_mod_sys_id_alerts PROD_MOD_SYS_ID,
 l_customer_sys_id CUSTOMER_SYS_ID, 1 ACTIVE_STATUS_IND, '/' MODULE_URL, 0  'DEFAULT', now() CREATED_DATE,
 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;
 select l_prod_mod_sys_id_alerts;

 select max(cust_prod_mod_feature_sys_id)+1 into l_cust_prod_mod_feature_sys_id from customer_product_module_features;



INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_FEATURE_SYS_ID,CUST_PROD_MOD_SYS_ID,DEFAULT_URL,FEATURE_NAME,FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,`DEFAULT`,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
  select  l_cust_prod_mod_feature_sys_id CUST_PROD_MOD_FEATURE_SYS_ID, l_cust_prod_mod_sys_id CUST_PROD_MOD_SYS_ID,
 '/' DEFAULT_URL, 'Canned Analysis' FEATURE_NAME, 'Standard Category' FEATURE_DESC, concat(l_customer_code, 'CANNEDANALYSIS1') FEATURE_CODE,
 concat ('PARENT_',concat(l_customer_code, 'CANNEDANALYSIS1')) FEATURE_TYPE, 1 'DEFAULT', 1 ACTIVE_STATUS_IND, now() CREATED_DATE,
 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;

 INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_FEATURE_SYS_ID,CUST_PROD_MOD_SYS_ID,DEFAULT_URL,FEATURE_NAME,FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,`DEFAULT`,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
  select  l_cust_prod_mod_feature_sys_id+1 CUST_PROD_MOD_FEATURE_SYS_ID, l_cust_prod_mod_sys_id CUST_PROD_MOD_SYS_ID,
 '/' DEFAULT_URL, 'Optimization' FEATURE_NAME, 'Optimization sub-category' FEATURE_DESC, concat(l_customer_code, 'OPTIMIZATION1') FEATURE_CODE,
 concat ('CHILD_',concat(l_customer_code, 'CANNEDANALYSIS1')) FEATURE_TYPE, 0 'DEFAULT', 1 ACTIVE_STATUS_IND, now() CREATED_DATE, 'onboard' CREATED_BY,
 NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;

 INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_FEATURE_SYS_ID,CUST_PROD_MOD_SYS_ID,DEFAULT_URL,FEATURE_NAME,FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,`DEFAULT`,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
  select  l_cust_prod_mod_feature_sys_id+2 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_cust_prod_mod_sys_id CUST_PROD_MOD_SYS_ID,
 '/' DEFAULT_URL, 'My Analysis' FEATURE_NAME, 'Default Category' FEATURE_DESC,
 concat(l_customer_code, 'MYANALYSIS1') FEATURE_CODE, concat('PARENT_',concat(l_customer_code, 'MYANALYSIS1')) FEATURE_TYPE,
 1 'DEFAULT', 1 ACTIVE_STATUS_IND, now() CREATED_DATE, 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY,
 NULL MODIFIED_DATE, NULL MODIFIED_BY;

 INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_FEATURE_SYS_ID,CUST_PROD_MOD_SYS_ID,DEFAULT_URL,FEATURE_NAME,FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,`DEFAULT`,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
  select  l_cust_prod_mod_feature_sys_id+3 CUST_PROD_MOD_FEATURE_SYS_ID, l_cust_prod_mod_sys_id CUST_PROD_MOD_SYS_ID,
 '/' DEFAULT_URL, 'Drafts' FEATURE_NAME, 'Drafts' FEATURE_DESC, concat(l_customer_code, 'DRAFTS1') FEATURE_CODE, concat('CHILD_',concat(l_customer_code, 'MYANALYSIS1')) FEATURE_TYPE,
 0 'DEFAULT', 1 ACTIVE_STATUS_IND, now() CREATED_DATE, 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;

INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_FEATURE_SYS_ID,CUST_PROD_MOD_SYS_ID,DEFAULT_URL,FEATURE_NAME,FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,`DEFAULT`,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
  select  l_cust_prod_mod_feature_sys_id+4 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_cust_prod_mod_sys_id+1 CUST_PROD_MOD_SYS_ID,
 '/' DEFAULT_URL, 'My DASHBOARD' FEATURE_NAME, 'Default Category' FEATURE_DESC,
 concat(l_customer_code, 'MYDASHBOARD1') FEATURE_CODE, concat('PARENT_',concat(l_customer_code, 'MYDASHBOARD1')) FEATURE_TYPE,
 1 'DEFAULT', 1 ACTIVE_STATUS_IND, now() CREATED_DATE, 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY,
 NULL MODIFIED_DATE, NULL MODIFIED_BY;

 INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_FEATURE_SYS_ID,CUST_PROD_MOD_SYS_ID,DEFAULT_URL,FEATURE_NAME,FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,`DEFAULT`,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
  select  l_cust_prod_mod_feature_sys_id+5 CUST_PROD_MOD_FEATURE_SYS_ID, l_cust_prod_mod_sys_id+1 CUST_PROD_MOD_SYS_ID,
 '/' DEFAULT_URL, 'Drafts' FEATURE_NAME, 'Drafts' FEATURE_DESC, concat(l_customer_code, 'DRAFTS2') FEATURE_CODE, concat('CHILD_',concat(l_customer_code, 'MYDASHBOARD1')) FEATURE_TYPE,
 0 'DEFAULT', 1 ACTIVE_STATUS_IND, now() CREATED_DATE, 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;


INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_FEATURE_SYS_ID,CUST_PROD_MOD_SYS_ID,DEFAULT_URL,FEATURE_NAME,FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,`DEFAULT`,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
  select  l_cust_prod_mod_feature_sys_id+6 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_cust_prod_mod_sys_id+2 CUST_PROD_MOD_SYS_ID,
 '/' DEFAULT_URL, 'Data Ingestion Service' FEATURE_NAME, 'Data Ingestion Service' FEATURE_DESC,
 concat(l_customer_code, 'DIS0000001') FEATURE_CODE, concat('PARENT_',concat(l_customer_code, 'DIS0000001')) FEATURE_TYPE,
 1 'DEFAULT', 1 ACTIVE_STATUS_IND, now() CREATED_DATE, 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY,
 NULL MODIFIED_DATE, NULL MODIFIED_BY;

 INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_FEATURE_SYS_ID,CUST_PROD_MOD_SYS_ID,DEFAULT_URL,FEATURE_NAME,FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,`DEFAULT`,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
  select  l_cust_prod_mod_feature_sys_id+7 CUST_PROD_MOD_FEATURE_SYS_ID, l_cust_prod_mod_sys_id+2 CUST_PROD_MOD_SYS_ID,
 'datasource/create' DEFAULT_URL, 'Channel Management' FEATURE_NAME, 'Channel Management' FEATURE_DESC, concat(l_customer_code, 'CHANNELMANAGE001') FEATURE_CODE, concat('CHILD_',concat(l_customer_code, 'DIS0000001')) FEATURE_TYPE,
 0 'DEFAULT', 1 ACTIVE_STATUS_IND, now() CREATED_DATE, 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;

 INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_FEATURE_SYS_ID,CUST_PROD_MOD_SYS_ID,DEFAULT_URL,FEATURE_NAME,FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,`DEFAULT`,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
  select  l_cust_prod_mod_feature_sys_id+8 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_cust_prod_mod_sys_id+2 CUST_PROD_MOD_SYS_ID,
 '/' DEFAULT_URL, 'RTIS' FEATURE_NAME, 'RTIS' FEATURE_DESC,
 concat(l_customer_code, 'RTIS000001') FEATURE_CODE, concat('PARENT_',concat(l_customer_code, 'RTIS000001')) FEATURE_TYPE,
 1 'DEFAULT', 1 ACTIVE_STATUS_IND, now() CREATED_DATE, 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY,
 NULL MODIFIED_DATE, NULL MODIFIED_BY;

INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_FEATURE_SYS_ID,CUST_PROD_MOD_SYS_ID,DEFAULT_URL,FEATURE_NAME,FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,`DEFAULT`,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
  select  l_cust_prod_mod_feature_sys_id+9 CUST_PROD_MOD_FEATURE_SYS_ID, l_cust_prod_mod_sys_id+2 CUST_PROD_MOD_SYS_ID,
 'rtis/registration' DEFAULT_URL, 'RTIS Registration' FEATURE_NAME, 'RTIS Registration' FEATURE_DESC, concat(l_customer_code, 'RtisReg001') FEATURE_CODE, concat('CHILD_',concat(l_customer_code, 'RTIS000001')) FEATURE_TYPE,
 0 'DEFAULT', 1 ACTIVE_STATUS_IND, now() CREATED_DATE, 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;

INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_FEATURE_SYS_ID,CUST_PROD_MOD_SYS_ID,DEFAULT_URL,FEATURE_NAME,FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,`DEFAULT`,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
  select  l_cust_prod_mod_feature_sys_id+10 CUST_PROD_MOD_FEATURE_SYS_ID, l_cust_prod_mod_sys_id+2 CUST_PROD_MOD_SYS_ID,
 'rtis/appkeys' DEFAULT_URL, 'RTIS App Keys' FEATURE_NAME, 'RTIS App Keys' FEATURE_DESC, concat(l_customer_code, 'RtisAppKey001') FEATURE_CODE, concat('CHILD_',concat(l_customer_code, 'RTIS000001')) FEATURE_TYPE,
 0 'DEFAULT', 1 ACTIVE_STATUS_IND, now() CREATED_DATE, 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;

INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_FEATURE_SYS_ID,CUST_PROD_MOD_SYS_ID,DEFAULT_URL,FEATURE_NAME,FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,`DEFAULT`,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
  select  l_cust_prod_mod_feature_sys_id+11 CUST_PROD_MOD_FEATURE_SYS_ID, l_cust_prod_mod_sys_id+3 CUST_PROD_MOD_SYS_ID,
 '/' DEFAULT_URL, 'Alerts' FEATURE_NAME, 'Alerts' FEATURE_DESC, concat(l_customer_code, 'ALERT12') FEATURE_CODE, concat('PARENT_',concat(l_customer_code, 'ALERT12')) FEATURE_TYPE,
 0 'DEFAULT', 1 ACTIVE_STATUS_IND, now() CREATED_DATE, 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;
select l_cust_prod_mod_feature_sys_id+11;

INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_FEATURE_SYS_ID,CUST_PROD_MOD_SYS_ID,DEFAULT_URL,FEATURE_NAME,FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,`DEFAULT`,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
  select  l_cust_prod_mod_feature_sys_id+12 CUST_PROD_MOD_FEATURE_SYS_ID, l_cust_prod_mod_sys_id+3 CUST_PROD_MOD_SYS_ID,
 'view' DEFAULT_URL, 'View Alerts' FEATURE_NAME, 'Alerts' FEATURE_DESC, concat(l_customer_code, 'VIEWALERT') FEATURE_CODE, concat('CHILD_',concat(l_customer_code, 'ALERT12')) FEATURE_TYPE,
 0 'DEFAULT', 1 ACTIVE_STATUS_IND, now() CREATED_DATE, 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;
select l_cust_prod_mod_feature_sys_id+12;

INSERT INTO CUSTOMER_PRODUCT_MODULE_FEATURES (CUST_PROD_MOD_FEATURE_SYS_ID,CUST_PROD_MOD_SYS_ID,DEFAULT_URL,FEATURE_NAME,FEATURE_DESC,FEATURE_CODE,FEATURE_TYPE,`DEFAULT`,ACTIVE_STATUS_IND,CREATED_DATE,CREATED_BY,INACTIVATED_DATE,INACTIVATED_BY,MODIFIED_DATE,MODIFIED_BY)
  select  l_cust_prod_mod_feature_sys_id+13 CUST_PROD_MOD_FEATURE_SYS_ID, l_cust_prod_mod_sys_id+3 CUST_PROD_MOD_SYS_ID,
 'configure' DEFAULT_URL, 'Configure Alerts' FEATURE_NAME, 'Alerts' FEATURE_DESC, concat(l_customer_code, 'CONFIGUREALERT') FEATURE_CODE, concat('CHILD_',concat(l_customer_code, 'ALERT12')) FEATURE_TYPE,
 0 'DEFAULT', 1 ACTIVE_STATUS_IND, now() CREATED_DATE, 'onboard' CREATED_BY, NULL INACTIVATED_DATE, NULL INACTIVATED_BY, NULL MODIFIED_DATE, NULL MODIFIED_BY;
select l_cust_prod_mod_feature_sys_id+13;

 select max(role_sys_id)+1 into l_role_sys_id from roles;
 select l_role_sys_id;

 INSERT INTO ROLES (ROLE_SYS_ID, CUSTOMER_SYS_ID, ROLE_NAME, ROLE_CODE, ROLE_DESC, ROLE_TYPE, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
 select  l_role_sys_id ROLE_SYS_ID,
 l_customer_sys_id CUSTOMER_SYS_ID,
 'ADMIN' ROLE_NAME,
 concat(l_customer_code,'_ADMIN_USER') ROLE_CODE,
 'Admin User' ROLE_DESC,
 'ADMIN' ROLE_TYPE,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;

 select max(user_sys_id)+1 into l_user_sys_id from users;

 INSERT INTO users (USER_SYS_ID, USER_ID, EMAIL, ROLE_SYS_ID, CUSTOMER_SYS_ID, ENCRYPTED_PASSWORD, FIRST_NAME, MIDDLE_NAME, LAST_NAME, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
 select  l_user_sys_id USER_SYS_ID,
 concat(concat('sawadmin@',lower(l_customer_code),'.com')) USER_ID,
 l_email EMAIL,
 l_role_sys_id ROLE_SYS_ID,
 l_customer_sys_id CUSTOMER_SYS_ID,
 'Y1lw/IcaP7G++mCJ/TlIGXnAM9Ud+b58niTjkxtdc4I=' ENCRYPTED_PASSWORD,
 l_first_name FIRST_NAME,
 l_middle_name MIDDLE_NAME,
 l_last_name LAST_NAME,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;


 Select max(privilege_sys_id)+1 into l_privilege_sys_id from privileges;

 INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
 SELECT l_privilege_sys_id PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 l_cust_prod_mod_sys_id CUST_PROD_MOD_SYS_ID,
 l_cust_prod_mod_feature_sys_id CUST_PROD_MOD_FEATURE_SYS_ID,
 l_role_sys_id ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '128' PRIVILEGE_CODE,
 'All' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;

 INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
 SELECT l_privilege_sys_id+1 PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 l_cust_prod_mod_sys_id CUST_PROD_MOD_SYS_ID,
 l_cust_prod_mod_feature_sys_id+1 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_role_sys_id ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '128' PRIVILEGE_CODE,
 'All' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;

 INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
  SELECT l_privilege_sys_id+2 PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 l_cust_prod_mod_sys_id CUST_PROD_MOD_SYS_ID,
 l_cust_prod_mod_feature_sys_id+2 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_role_sys_id ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '128' PRIVILEGE_CODE,
 'All' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;

 INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
   SELECT l_privilege_sys_id+3 PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 l_cust_prod_mod_sys_id CUST_PROD_MOD_SYS_ID,
 l_cust_prod_mod_feature_sys_id+3 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_role_sys_id ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '128' PRIVILEGE_CODE,
 'All' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;

 INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
    SELECT l_privilege_sys_id+4 PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 l_cust_prod_mod_sys_id CUST_PROD_MOD_SYS_ID,
 0 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_role_sys_id ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '128' PRIVILEGE_CODE,
 'All' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;

 INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
     SELECT l_privilege_sys_id+5 PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 0 CUST_PROD_MOD_SYS_ID,
 0 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_role_sys_id ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '128' PRIVILEGE_CODE,
 'All' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;

 INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
  SELECT l_privilege_sys_id+6 PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 l_cust_prod_mod_sys_id+1 CUST_PROD_MOD_SYS_ID,
 l_cust_prod_mod_feature_sys_id+4 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_role_sys_id ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '128' PRIVILEGE_CODE,
 'All' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;

 INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
   SELECT l_privilege_sys_id+7 PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 l_cust_prod_mod_sys_id+1 CUST_PROD_MOD_SYS_ID,
 l_cust_prod_mod_feature_sys_id+5 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_role_sys_id ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '128' PRIVILEGE_CODE,
 'All' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;

INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
  SELECT l_privilege_sys_id+8 PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 l_cust_prod_mod_sys_id+2 CUST_PROD_MOD_SYS_ID,
 l_cust_prod_mod_feature_sys_id+6 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_role_sys_id ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '128' PRIVILEGE_CODE,
 'All' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;

 INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
   SELECT l_privilege_sys_id+9 PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 l_cust_prod_mod_sys_id+2 CUST_PROD_MOD_SYS_ID,
 l_cust_prod_mod_feature_sys_id+7 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_role_sys_id ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '128' PRIVILEGE_CODE,
 'All' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;

 INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
  SELECT l_privilege_sys_id+10 PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 l_cust_prod_mod_sys_id+2 CUST_PROD_MOD_SYS_ID,
 l_cust_prod_mod_feature_sys_id+8 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_role_sys_id ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '128' PRIVILEGE_CODE,
 'All' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;

 INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
  SELECT l_privilege_sys_id+11 PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 l_cust_prod_mod_sys_id+2 CUST_PROD_MOD_SYS_ID,
 l_cust_prod_mod_feature_sys_id+9 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_role_sys_id ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '128' PRIVILEGE_CODE,
 'All' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;

 INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
  SELECT l_privilege_sys_id+12 PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 l_cust_prod_mod_sys_id+2 CUST_PROD_MOD_SYS_ID,
 l_cust_prod_mod_feature_sys_id+10 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_role_sys_id ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '128' PRIVILEGE_CODE,
 'All' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;


  INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
   SELECT l_privilege_sys_id+13 PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 l_cust_prod_mod_sys_id+3 CUST_PROD_MOD_SYS_ID,
 l_cust_prod_mod_feature_sys_id+11 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_role_sys_id ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '128' PRIVILEGE_CODE,
 'All' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;
 select l_privilege_sys_id+13;

  INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
   SELECT l_privilege_sys_id+14 PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 l_cust_prod_mod_sys_id+3 CUST_PROD_MOD_SYS_ID,
 l_cust_prod_mod_feature_sys_id+12 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_role_sys_id ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '32768' PRIVILEGE_CODE,
 'Access/View' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;
 select l_privilege_sys_id+14;

 INSERT INTO PRIVILEGES (PRIVILEGE_SYS_ID, CUST_PROD_SYS_ID,CUST_PROD_MOD_SYS_ID, CUST_PROD_MOD_FEATURE_SYS_ID, ROLE_SYS_ID, ANALYSIS_SYS_ID, PRIVILEGE_CODE, PRIVILEGE_DESC, ACTIVE_STATUS_IND, CREATED_DATE, CREATED_BY)
   SELECT l_privilege_sys_id+15 PRIVILEGE_SYS_ID,
 l_cust_prod_sys_id CUST_PROD_SYS_ID,
 l_cust_prod_mod_sys_id+3 CUST_PROD_MOD_SYS_ID,
 l_cust_prod_mod_feature_sys_id+13 CUST_PROD_MOD_FEATURE_SYS_ID,
 l_role_sys_id ROLE_SYS_ID,
 '0' ANALYSIS_SYS_ID,
 '32768' PRIVILEGE_CODE,
 'Access/View' PRIVILEGE_DESC,
 1 ACTIVE_STATUS_IND,
 now() CREATED_DATE,
 'onboard' CREATED_BY;
 select l_privilege_sys_id+15;

 COMMIT;
 END;
//
DELIMITER ;

