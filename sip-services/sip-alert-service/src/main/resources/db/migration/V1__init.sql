/*******************************************************************************
 Filename:  V1__init.sql
 Purpose:   Creating of notification subscriber table
 Date:      26-03-2010
********************************************************************************/

DROP TABLE IF EXISTS `MODULE_SUBSCRIBER_MAPPING`;
DROP TABLE IF EXISTS `NOTIFICATION_SUBSCRIBER`;

CREATE TABLE `NOTIFICATION_SUBSCRIBER`
(
  ID                VARCHAR(50)  NOT NULL COMMENT 'Unique ID for the row',
  SUBSCRIBER_ID     VARCHAR(50)  NOT NULL COMMENT 'Unique subscriber id',
  SUBSCRIBER_NAME   VARCHAR(100) COMMENT 'Name of the subscriber',
  CHANNEL_TYPE      VARCHAR(20) NOT NULL COMMENT 'Type of the notification channel',
  CHANNEL_VALUE     VARCHAR(1000) UNIQUE NOT NULL COMMENT 'Channel value in stringified json form',
  CUSTOMER_CODE     VARCHAR(50) NOT NULL COMMENT 'Customer for which subscriber is associated with',
  ACTIVE            TINYINT(4) NOT NULL DEFAULT 1 COMMENT 'Indicates whether subscriber is active or not',
  CREATED_TIME      DATETIME NULL COMMENT 'Created timestamp',
  MODIFIED_TIME     DATETIME COMMENT 'Modified timestamp'
)ENGINE=InnoDB COMMENT 'PURPOSE: NOTIFICATION_SUBSCRIBERS is used to hold the list of all subscribers';

ALTER TABLE `NOTIFICATION_SUBSCRIBER`
ADD CONSTRAINT PK_NOTIFICATION_SUBSCRIBER PRIMARY KEY (ID);

CREATE TABLE `MODULE_SUBSCRIBER_MAPPING`
(
  ID                VARCHAR (50)  NOT NULL COMMENT 'Unique ID for every entry',
  MODULE_ID         VARCHAR (100) NOT NULL COMMENT 'ID of the modules, E.g.: Alert ID, analysis id',
  MODULE_NAME       VARCHAR (50)  NOT NULL COMMENT 'Name of the modules, ALERT, ANALYSIS etc',
  SUBSCRIBER_ID     VARCHAR (50)  NOT NULL COMMENT 'Subscriber ID from notification_subscriber table',
  CHANNEL_TYPE      VARCHAR (20)  NOT NULL COMMENT 'Type of the channel, for E.g.: EMAIL, SMS',
  ACKNOWLEDGED      TINYINT(4)    NOT NULL DEFAULT 0 COMMENT 'Whether subscriber has acknowledged for the notification'
)ENGINE=InnoDB COMMENT='Purpose: MODULE_SUBSCRIBER_MAPPING is used to maintain the list of subscribers subscribed for a given module';

ALTER TABLE `MODULE_SUBSCRIBER_MAPPING`
ADD CONSTRAINT PK_MODULE_SUBSCRIBER_MAPPING PRIMARY KEY (ID);

ALTER TABLE `MODULE_SUBSCRIBER_MAPPING`
ADD CONSTRAINT FK_MODULE_SUBSCRIBER_MAPPING
FOREIGN KEY (SUBSCRIBER_ID) REFERENCES NOTIFICATION_SUBSCRIBER (ID) ON DELETE CASCADE ;
