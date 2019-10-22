/*******************************************************************************
 Filename:  V21__DDL_USER_INACTIVE_COUNT.sql
 Purpose:   Inactivate the account for some time if unsuccessful login-attempt reaches to certain number
 Date:      18-10-2019
********************************************************************************/

  /*******************************************************************************
   ALTER Table Scripts Starts
  ********************************************************************************/

  ALTER TABLE USERS
       ADD COLUMN UNSUCCESSFUL_LOGIN_ATTEMPT bigint(20);

  UPDATE USERS SET UNSUCCESSFUL_LOGIN_ATTEMPT = 0;

  ALTER TABLE USERS MODIFY COLUMN UNSUCCESSFUL_LOGIN_ATTEMPT bigint(20) NOT NULL DEFAULT 0;

  ALTER TABLE USERS
       ADD COLUMN LAST_UNSUCCESS_LOGIN_TIME DATETIME;

  UPDATE USERS SET LAST_UNSUCCESS_LOGIN_TIME = sysdate();

  ALTER TABLE USERS MODIFY COLUMN LAST_UNSUCCESS_LOGIN_TIME DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP();

  /*******************************************************************************
   ALTER Table Scripts Ends
  ********************************************************************************/

/*******************************************************************************
 TABLE Scripts Ends
********************************************************************************/
