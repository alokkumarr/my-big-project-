import argparse
import shutil
import sys
import os
import MySQLdb 
import re
import smtplib
from time import gmtime, strftime
import glob
import subprocess
import logging

def init_setp_obj(customer_mod, proc_folder_mod, obj_proc_folder_mod_mod, file2_mod, log_folder_mod, obj_name_mod, sql_server_type_mod, email_mod, email_subject_mod, sql_cmd_bat_file_mod = '', db_info_mod = [], db_conn_type_mod = '', others_log_file_folder_mod = ''):

    sys.path.append(os.path.dirname(proc_folder_mod))

    import RSMailer # User created script for RZ mail implementation imported as module

    if sql_server_type_mod == "MYSQL":
        
        sql_str_list_mod = db_info_mod.split("|")
        print db_info_mod

        db_server_mod = sql_str_list_mod[0]
        db_name_mod = sql_str_list_mod[1]

        db_user_mod = "DUMMYVALUE" # Initialize DB User Name variable to dummy value
        db_password_mod = "DUMMYVALUE" # Initialize DB Password variable to dummy value

        db_conn_type_mod = "FALSE"

        if db_conn_type_mod == "FALSE":
            db_user_mod = sql_str_list_mod[2]
            db_password_mod = sql_str_list_mod[3]

        if db_conn_type_mod == "FALSE":
            try:
             cnxn_mod = MySQLdb.connect(db_server_mod,db_user_mod,db_password_mod,db_name_mod) # MYSQL Server Authentication
            #cnxn = MySQLdb.connect(DRIVER='com.mysql.jdbc.Driver',SERVER=db_server,DATABASE=db_name,UID=db_user,PWD=db_password, autocommit=True) # MYSQL Server Authentication
            except:
                raise Exception("PARAMETER ISSUE:Please verify DB parameters provided.")
        else:
            try:
             #cnxn_mod = MySQLdb.connect(db_server_mod,db_user_mod,db_password_mod,db_name_mod) # MYSQL Server Authentication
             cnxn = MySQLdb.connect(DRIVER='com.mysql.jdbc.Driver',SERVER=db_server,DATABASE=db_name,UID=db_user,PWD=db_password, autocommit=True) # MYSQL Server Authentication
            except:
                raise Exception("PARAMETER ISSUE:Please verify DB parameters provided.")
           

    else:
        raise Exception("SQL Server Type provided is not supported")
    
    file2_mod_with_ext = os.path.join(obj_proc_folder_mod_mod,file2_mod) # Get the complete path for the SQL file under consideration

    tmp_obj_log_file = "log_" + os.path.splitext(file2_mod)[0] + ".txt" # Create ouput log file template. Format is log_obj_name_mod.txt

    obj_log_file = os.path.join(log_folder_mod,obj_name_mod,tmp_obj_log_file) # Get the complete path for the log file

    sqlQuery_mod = ''

    err_str = ''

    with open(file2_mod_with_ext, 'r') as inp:
        for line in inp:
            
            if ((';' in line and obj_name_mod <> "PROCEDURES") or ('END;' in line and obj_name_mod == "PROCEDURES") or ('END;' in line and obj_name_mod == "FUNCTIONS")):
                
                try:
                    sqlQuery_mod = sqlQuery_mod + line
                    curobj_mod = cnxn_mod.cursor()
                    curobj_mod.execute(sqlQuery_mod)
                    #print sqlQuery_mod
                    #curobj_mod.commit()
                    curobj_mod.close()
                    sqlQuery_mod = ''
                except:
                    err_str = "SQL Execution failed: " + sqlQuery_mod
                    with open(obj_log_file,'a') as outfile:
                        outfile.write(err_str)
                    outfile.close()

                    tmp_obj_err_log_file = "Error_" + os.path.splitext(file2_mod)[0] + ".txt" # Create error log file template. Format is Error_obj_name_mod.txt

                    obj_err_log_file = os.path.join(log_folder_mod,obj_name_mod,tmp_obj_err_log_file) # Get the complete path for the error log file
                    
                    shutil.copyfile(obj_log_file, obj_err_log_file) # Copy Log file contents into error log file

                    # Update failure status in initial setup table
                    querystring_fail_upd = """
                                            UPDATE
                                            RS_HL_INITIAL_SETUP
                                            SET
                                            OBJECT_VALUE = 0
                                            WHERE
                                            OBJECT_NAME = '%s' """ %(obj_name_mod) # Update Initial setup table with Failure status for the Object type

                    cur3 = cnxn_mod.cursor()
                    cur3.execute(querystring_fail_upd)
                    cnxn_mod.commit()
                    cur3.close()

                    # Start Drop objects script creation
                        
                    # temp_drop_qry_file = '\\'.join([proc_folder_mod,"DROP_QRY.sql"]) # Configure file path for a temp SQL file to hold creation of drop statements

                    drop_qry_file = os.path.join(proc_folder_mod,"DROP.sql")

                    if (os.path.exists(drop_qry_file)): # Drop the temp DROP SQL statement
                        os.unlink(drop_qry_file)

                    drop_row_str = ''

                    if obj_name_mod != "INDEX" and obj_name_mod != "CONSTRAINT" and 1==2: # Check for objects other than Index & Constraint to construct drop objects based on DROP "OBJECT" syntax
                        querystring_drop_obj = """
                                SELECT 
                                    DISTINCT CASE OBJTYPE WHEN 'TABLE' THEN 'TABLE' 
                                                    WHEN 'VIEW' THEN 'VIEW' 
                                                    WHEN 'PROCEDURE' THEN 'PROC' 
                                                    WHEN 'SYNONYM' THEN 'SYNONYM'  
                                                    WHEN 'SQL_DML_TRIGGER' THEN 'TRIGGER'
                                                    WHEN 'SEQUENCE' THEN 'SEQUENCE'
                                                    END AS OBJECT_TYPE
                                                    ,OBJNAME AS OBJECT_NAME
                                FROM 
                                        _V_object_data
                                WHERE 
                                        OBJTYPE = CASE '%s' WHEN 'TABLE' THEN 'TABLE' 
                                                    WHEN 'VIEW' THEN 'VIEW' 
                                                    WHEN 'PROCEDURE' THEN 'PROCEDURE' 
                                                    WHEN 'SYNONYM' THEN 'SYNONYM' 
                                                    WHEN 'TRIGGER' THEN 'SQL_DML_TRIGGER'
                                                    WHEN 'SEQUENCE' THEN 'SEQUENCE'
                                                    END
                                        AND OBJNAME NOT IN ('RS_SETUP_MD','DB_LINKED_SERVER_TBL')
                                        AND STRLEFT(OBJNAME,3) <> 'STG'
                                        AND STRLEFT(OBJNAME,3) <> 'OMD'
                                        AND DBNAME = '%s'
                                        AND OWNER = '%s' """ % (obj_name_mod, db_name_mod, db_user_mod) # Case statement is required since folder names will not match with SQL Server expected values
                        
                        cur_drp = cnxn_mod.cursor()
                        cur_drp.execute(querystring_drop_obj)
                        rows_drp = cur_drp.fetchall()
                        
                        with open(drop_qry_file,'a') as outfile:
                            for row in rows_drp:
                                drop_row_str = ' '.join(['DROP ',row.OBJECT_TYPE,row.OBJECT_NAME,';'])
                                outfile.write(drop_row_str)
                        outfile.close()

                        cur_drp.close()
                            
                    elif obj_name_mod == "INDEX_NOT": # Check for non clustered & unique index objects

                        querystring_drop_obj = """ 
                                SELECT DISTINCT INDEX_NAME, TABLE_NAME
                                FROM INFORMATION_SCHEMA.STATISTICS
                                WHERE UPPER(TABLE_SCHEMA) = 'RAWDEV' """
                        
                        cur_drp = cnxn_mod.cursor()
                        cur_drp.execute(querystring_drop_obj)
                        rows_drp = cur_drp.fetchall()
                        
                        with open(drop_qry_file,'a') as outfile:
                            for row in rows_drp:
                                drop_row_str = ' '.join(['DROP INDEX ',row.INDEX_NAME,' ON ',row.TABLE_NAME,';'])
                                outfile.write(drop_row_str)
                        outfile.close()

                        cur_drp.close()

                    elif obj_name_mod == "CONSTRAINTS_NOT": # Check for foreign key constraints

                        querystring_drop_obj = """ 

                                SELECT 
                                        T1.CONSTRAINT_NAME AS CONSTRAINT_NAME,
                                        T1.TABLE_NAME AS TABLE_NAME
                                FROM 
                                        INFORMATION_SCHEMA.TABLE_CONSTRAINTS T1
                                WHERE UPPER(T1.CONSTRAINT_SCHEMA) = 'RAWDEV' """
                        
                        cur_drp = cnxn_mod.cursor()
                        cur_drp.execute(querystring_drop_obj)
                        rows_drp = cur_drp.fetchall()
                        
                        with open(drop_qry_file,'a') as outfile:
                            for row in rows_drp:
                                drop_row_str = ' '.join(['ALTER TABLE ',row.TABLE_NAME,'DROP FOREIGN KEY ',row.CONSTRAINT_NAME,';'])
                                outfile.write(drop_row_str)
                        outfile.close()      

                        cur_drp.close()

                    others_log_file = os.path.join(others_log_file_folder_mod,"Others_log.txt") # Configure file path for Other scenario logging

                    # The below SQL CMD operation will create a DROP statement based on the dynamic SQL configured in the temp drop statement & add the statement to the DROP.sql file.

                    # subprocess.check_output([sql_cmd_bat_file_mod,temp_drop_qry_file,db_server_mod,db_user_mod,db_password_mod,db_name_mod,drop_qry_file])

                    # The below SQL CMD operation will execute the DROP.sql file to drop the objects created.

                    # sqlQuery_mod = ''

                    # err_str = ''

                    # with open(drop_qry_file, 'r') as inp:
                        # for line in inp:
                            # if ';' in line:

                                # try:
                                    # sqlQuery_mod = sqlQuery_mod + line
                                    # curobj = cnxn_mod.cursor()
                                    # #curobj.commit()
                                    # curobj.close()
                                    # sqlQuery_mod = ''
                                # except:
                                    # err_str = "SQL Execution failed: " + sqlQuery_mod

                                    # with open(others_log_file,'a') as outfile:
                                        # outfile.write(err_str)
                                    # outfile.close()
                                    
                                    # mail_msg = """%s Scripts Deployment failed in '%s' script execution""" % (customer_mod,drop_qry_file)
                                    # RSMailer.mail(email_subject_mod,email_mod,mail_msg_mod,others_log_file)
                                    # raise Exception(mail_msg)
                            # elif 'PRINT' in line:
                                # disp = line.split("'")[1]
                                # print(disp, '\r')
                            # else:
                                # sqlQuery_mod = sqlQuery_mod + line
                    # inp.close()

                    # if (os.path.exists(drop_qry_file)): # Drop the temp DROP SQL statement
                        # os.unlink(drop_qry_file) 

                    mail_msg_mod = """%s Scripts Deployment failed in '%s' script execution""" % (customer_mod,file2_mod) # Configure mail message to be sent

                    RSMailer.mail(email_subject_mod,email_mod,mail_msg_mod,obj_err_log_file) # Use Mailer module to send mail

                    raise Exception(mail_msg_mod)

            elif 'PRINT' in line:
                disp = line.split("'")[1]
                print(disp, '\r')
            else:
                sqlQuery_mod = sqlQuery_mod + line

    inp.close()

    cnxn_mod.close()

