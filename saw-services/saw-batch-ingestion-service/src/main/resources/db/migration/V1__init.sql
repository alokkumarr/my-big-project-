DROP TABLE IF EXISTS `BIS_SOURCE`;
DROP TABLE IF EXISTS `BIS_ROUTE`;
CREATE TABLE `BIS_CHANNEL`
(
  BIS_CHANNEL_SYS_ID BIGINT  NOT NULL AUTO_INCREMENT,
  MODIFIED_DATE     DATETIME,
  MODIFIED_BY       VARCHAR(255),
  CREATED_DATE      DATETIME NULL,
  CREATED_BY        VARCHAR(255) NOT NULL,
  PRODUCT_CODE      VARCHAR(50) NOT NULL,
  CHANNEL_TYPE      VARCHAR(50) NOT NULL,
  PROJECT_CODE      VARCHAR(50) NOT NULL,
  CUSTOMER_CODE     VARCHAR(50) NOT NULL,
  CHANNEL_METADATA  JSON NOT NULL,
  CHECK (JSON_VALID(CHANNEL_METADATA)),
  CONSTRAINT CHANNEL_PK PRIMARY KEY (BIS_CHANNEL_SYS_ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `BIS_ROUTE`
(
  BIS_ROUTE_SYS_ID BIGINT  NOT NULL AUTO_INCREMENT,
  BIS_CHANNEL_SYS_ID BIGINT ,
  MODIFIED_DATE     DATETIME,
  MODIFIED_BY       VARCHAR(255),
  CREATED_DATE      DATETIME NULL,
  CREATED_BY        VARCHAR(255) NOT NULL,
  PRODUCT_CODE      VARCHAR(50) NOT NULL,
  PROJECT_CODE      VARCHAR(50) NOT NULL,
  CUSTOMER_CODE     VARCHAR(50) NOT NULL,
  ROUTE_METADATA    JSON NOT NULL,
  CHECK (JSON_VALID(ROUTE_METADATA)),
  CONSTRAINT ROUTE_PK PRIMARY KEY (BIS_ROUTE_SYS_ID),
  KEY `FK_BIS_CHANNEL_SYS_IDX` (`BIS_CHANNEL_SYS_ID`),
  CONSTRAINT `FK_BIS_CHANNEL_SYS_ID` FOREIGN KEY (`BIS_CHANNEL_SYS_ID`) REFERENCES `BIS_CHANNEL` (`BIS_CHANNEL_SYS_ID`) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
