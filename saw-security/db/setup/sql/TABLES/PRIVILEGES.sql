DROP TABLE IF EXISTS `PRIVILEGES`;
CREATE TABLE `PRIVILEGES`
(
  PRIVILEGE_SYS_ID       BIGINT  NOT NULL,
  CUST_PROD_SYS_ID   BIGINT  NOT NULL,
  CUST_PROD_MOD_SYS_ID   BIGINT ,
  CUST_PROD_MOD_FEATURE_SYS_ID   BIGINT ,
  ROLE_SYS_ID BIGINT NOT NULL,
  ANALYSIS_SYS_ID BIGINT ,
  PRIVILEGE_CODE         VARCHAR(50) NOT NULL,
  PRIVILEGE_DESC         VARCHAR(500) NOT NULL,
  ACTIVE_STATUS_IND TINYINT NOT NULL,
  CREATED_DATE     DATETIME NOT NULL,
  CREATED_BY       VARCHAR(255)NOT NULL,
  INACTIVATED_DATE   DATETIME,
  INACTIVATED_BY     VARCHAR(255),
  MODIFIED_DATE     DATETIME,
  MODIFIED_BY        VARCHAR(255)
)ENGINE=InnoDB;