/*******************************************************************************
 Filename:  V1__init.sql
 Purpose:   Creating of notification subscriber table
 Date:      26-03-2010
********************************************************************************/

DROP TABLE IF EXISTS `NOTIFICATION_SUBSCRIBER`;

CREATE TABLE `NOTIFICATION_SUBSCRIBER`
(
  ID                VARCHAR(50)  NOT NULL COMMENT 'Unique ID for the row',
  SUBSCRIBER_ID     VARCHAR(50)  NOT NULL COMMENT 'Unique subscriber id',
  SUBSCRIBER_NAME   VARCHAR(100) COMMENT 'Name of the subscriber',
  CHANNEL_TYPE      VARCHAR(20) NOT NULL COMMENT 'Type of the notification channel',
  CHANNEL_VALUE     TEXT NOT NULL COMMENT 'Channel value in stringified json form',
  CUSTOMER_CODE     VARCHAR(50) NOT NULL COMMENT 'Customer for which subscriber is associated with',
  ACTIVE            tinyint(4) NOT NULL DEFAULT 1 COMMENT 'Indicates whether subscriber is active or not',
  CREATED_TIME      DATETIME NULL COMMENT 'Created timestamp',
  MODIFIED_TIME     DATETIME COMMENT 'Modified timestamp'
)ENGINE=InnoDB COMMENT 'PURPOSE: NOTIFICATION_SUBSCRIBERS is used to hold the list of all subscribers';
