/*******************************************************************************
 Filename:  V21__DDL_USER_INACTIVE_COUNT.sql
 Purpose:   Inactivate the account for some time if unsuccessful login-attempt reaches to certain number
 Date:      18-10-2019
********************************************************************************/

  /*******************************************************************************
   ALTER Table Scripts Starts
  ********************************************************************************/

  ALTER TABLE USERS
       ADD COLUMN INVALID_PASSWORD_COUNT bigint(20);

  UPDATE USERS SET INVALID_PASSWORD_COUNT = 0;

  ALTER TABLE USERS MODIFY COLUMN INVALID_PASSWORD_COUNT bigint(20) NOT NULL ;

  ALTER TABLE USERS
       ADD COLUMN LAST_UNSUCCESS_LOGIN_TIME DATETIME;

  UPDATE USERS SET LAST_UNSUCCESS_LOGIN_TIME = sysdate();

  ALTER TABLE USERS MODIFY COLUMN LAST_UNSUCCESS_LOGIN_TIME DATETIME NOT NULL;

  /*******************************************************************************
   ALTER Table Scripts Ends
  ********************************************************************************/

/*******************************************************************************
 TABLE Scripts Ends
********************************************************************************/
