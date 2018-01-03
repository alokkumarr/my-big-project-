import shutil
import sys
import os
import MySQLdb 
import re
import smtplib
import RSMailer # User created script for RZ mail implementation imported as module
from time import gmtime, strftime, localtime
import glob
import traceback 
import subprocess
import Object_Creation
import logging
cmd_arg = sys.argv[0]
cmd_dir = os.path.dirname(os.path.abspath(cmd_arg))
sys.path.insert(0, os.path.dirname(os.path.abspath(cmd_dir)))
import bda_init

# Author - Vinnie Ganesh
#        - Kiran PS
# Date- 
# Purpose Initial Setup for SAW

log1 = bda_init.get_logger()
log1.debug("Starting the initial python script")
log1.info("Reading the pyton script location")
log1.debug("get the saw security vars file path")

#vars_file_path="/mapr/bda_lab_batch/dl.dev/app/saw/etc/bda/saw-security.vars"
###########################################################
# Usage:
# Initial_Setup.py [--vars]
# If --vars specified - read params from /etc/bda/saw-security.vars file,
# otherwise read them from terminal

##
# Special file names
VARS_FNM = "/etc/bda/saw-security.vars"
#LOG_DIR  = "/var/bda/saw-security/log"

use_vars_file = False
if (len(sys.argv) > 1) and (sys.argv[1] == "--vars"):
    use_vars_file = True
    print "Read params from: ", VARS_FNM, "\n"

log1.debug("checking the vars file is present or not")
vars_dict = None
if use_vars_file:
    log1.debug("Start reading the vars file")
    vars_dict = bda_init.read_vars_file( VARS_FNM )
    print "VARS:", vars_dict


log1.debug("validating the vars file data ")
if not len(vars_dict) > 0:
   print "The vars file does not have data"
   log1.debug("The vars file does not have data")
 
err_msg = ""

#get the current directory
log1.info("Get the absolute path of initial setup parent directory")
cur_dir=os.path.dirname(os.path.realpath(__file__))

# Validate length of customer after taking input from console
customer = "SAW"

# email = "kiran.ps2@synchronoss.com"
log1.info("Get value for Email Address from the vars file")
if use_vars_file:
    #email = vars_dict['db.init.email']
    # Note: This email is unused and only set to avoid breaking the rest of the script.
    email = 'noreply@example.com'
    #print "Email ID to which notifications are to be sent:", email
else:
    email = raw_input("Please enter Email ID to which notifications are to be sent: ")

# Validate Email ID format. email.username@company.com & email@company.com are valid email ID's
m = re.search('^[a-zA-Z0-9]+[\.\-\_]?[a-zA-Z0-9]+[\@]+[a-zA-Z0-9]+[\.co]+[a-zA-Z0-9]?',email)

# If email ID is invalid, result appears as Undef i.e. None
if m is None:
    raise Exception("PARAMETER ISSUE:Incorrect Email ID format.")
else:
    print "Email ID format validated.\n"

email_subject = "%s Initial DB Deployment" %(customer)

#read the config json file path
log1.info("get the abolute path for sql directory")
proc_folder = os.path.join(cur_dir, "sql")

#secondary_path= os.getcwd()
#proc_folder = os.path.join(secondary_path,"INITIAL_SETUP")

log1.debug("checking the sql folder exists or not ")
if os.path.exists(proc_folder):
    print "Processing folder path validated: '%s'. \n" %(proc_folder)
    log1.debug(str(proc_folder)+" directory is present")
else:
    raise Exception("PARAMETER ISSUE:Please verify the path provided.")

# db_info = "vm-dwhdevblr|DEPLOY_AUTO_TEST|TRUE"



sql_server_type = "MYSQL"

sql_cmd_bat_file = ''

db_conn_type = ''

if sql_server_type == "MYSQL":
    log1.debug("Get the server|database name| user| password details from vars file")
    with open('/etc/bda/saw-security-db-password', 'r') as f:
        db_info_password = f.readline().strip()
    db_info=str(vars_dict['db.init.server'])+'|'+str(vars_dict['db.init.dbname'])+'|'+str(vars_dict['db.init.user'])+'|'+db_info_password

    sql_str_list = db_info.split("|")

    db_server = sql_str_list[0]
    db_name = sql_str_list[1]

    db_user = "NOTREQUIRED" # Initialize DB User Name variable to dummy value
    db_password = "NOTREQUIRED" # Initialize DB Password variable to dummy value

    db_conn_type = "FALSE"

    if db_conn_type == "FALSE":
        db_user = sql_str_list[2]
        db_password = sql_str_list[3]

    if db_conn_type == "FALSE":
        try:
            cnxn = MySQLdb.connect(db_server,db_user,db_password,db_name) # MYSQL Server Authentication
            #cnxn = MySQLdb.connect(DRIVER='com.mysql.jdbc.Driver',SERVER=db_server,DATABASE=db_name,UID=db_user,PWD=db_password, autocommit=True) # MYSQL Server Authentication
            
        except:
            raise Exception("PARAMETER ISSUE:Please verify DB parameters provided.")
    else:
        try:
            #cnxn = MySQLdb.connect(db_server,db_user,db_password,db_name) # MYSQL Server Authentication
            cnxn = MySQLdb.connect(DRIVER='com.mysql.jdbc.Driver',SERVER=db_server,DATABASE=db_name,Trusted_Connection="yes", autocommit=True) # Windows Authentication
        except:
            raise Exception("PARAMETER ISSUE:Please verify DB parameters provided.")

    log1.info("Database details were validated")

    sql_cmd_bat_file = os.path.join(proc_folder,"SQL_Script_Execution.bat")

    #if os.path.isfile(sql_cmd_bat_file):
     #   print "Batch file to execute SQL files is present.\n"
    #else:
     #   print "Batch file to execute SQL files is not present.\n"

# If logs folder exists on the processing folder, rename it with current date appended as string & then re-create logs folder with appropriate branches underneath
log_folder = os.path.join(proc_folder,"logs")

log_folder_dt = log_folder + "_" + strftime("%Y%m%d_%H%M%S", localtime())

if os.path.exists(log_folder):
    shutil.copytree(log_folder,log_folder_dt)
    shutil.rmtree(log_folder)
    
log1.info("Started creating the required folders for logging")
os.makedirs(os.path.join(log_folder,"TABLES"))
os.makedirs(os.path.join(log_folder,"SEQUENCES"))
os.makedirs(os.path.join(log_folder,"VIEWS"))
os.makedirs(os.path.join(log_folder,"SYNONYMS"))
os.makedirs(os.path.join(log_folder,"PROCEDURES"))
os.makedirs(os.path.join(log_folder,"INDEXES"))
os.makedirs(os.path.join(log_folder,"CONSTRAINTS"))
os.makedirs(os.path.join(log_folder,"FUNCTIONS"))
os.makedirs(os.path.join(log_folder,"TRIGGERS"))
os.makedirs(os.path.join(log_folder,"OTHERS"))
os.makedirs(os.path.join(log_folder,"DATA","APP"))
os.makedirs(os.path.join(log_folder,"DATA","CONFIG"))

#db_sp_log_folder = os.path.join(log_folder,"DB_SETUP")

others_log_file_folder = os.path.join(log_folder,"OTHERS")



data_app_log_folder = log_folder
data_cfg_log_folder = os.path.join(log_folder,"DATA","CONFIG")

#db_setup_folder = os.path.join(proc_folder,"DB_SETUP")

#db_setup_flag = raw_input("Is DB Setup required (YES/NO): ") # Check if DB Setup is required

#db_setup_flag = db_setup_flag.upper()            

# db_setup_flag = "YES"
     

#db_setup_drop_qry = os.path.join(db_setup_folder,"DROP_DB_SETUP.sql") # Configure file path of Drop statement for DB Setup objects

#----------------------------------------------------
#deploy_req_flag = raw_input("Is Object Creation Setup required (YES/NO): ") # Check if Object Creation Setup is required

deploy_req_flag = "YES"

if deploy_req_flag == "NO":
    print "Object Creation Setup stopped.\n"
else:
    #----------------------- Execute DB Object creation scripts

    if sql_server_type == "MYSQL":

        cnxn.close()
    
        if db_conn_type == "FALSE":
            try:
                cnxn = MySQLdb.connect(db_server,db_user,db_password,db_name) # MYSQL Server Authentication
            #cnxn = MySQLdb.connect(DRIVER='com.mysql.jdbc.Driver',SERVER=db_server,DATABASE=db_name,UID=db_user,PWD=db_password, autocommit=True) # MYSQL Server Authentication
            except:
                raise Exception("PARAMETER ISSUE:Please verify DB parameters provided.")
        else:
            try:
                #cnxn = MySQLdb.connect(db_server,db_user,db_password,db_name) # MYSQL Server Authentication
                cnxn = MySQLdb.connect(DRIVER='com.mysql.jdbc.Driver',SERVER=db_server,DATABASE=db_name,Trusted_Connection="yes") # Windows Authentication
            except:
                raise Exception("PARAMETER ISSUE:Please verify DB parameters provided.")

    chk_tab_sql_file = os.path.join(proc_folder,'TABLES.sql')

    chk_tab_exec_method = ''

    ti = 0
            
    if os.path.isfile(chk_tab_sql_file):
        with open(chk_tab_sql_file) as f:
            for ti, l in enumerate(f):
                ti = ti+1
                pass
            
        f.close()

    if ti > 0:
        chk_tab_exec_method = 'Order based on file'

    chk_constraints_sql_file = os.path.join(proc_folder,'CONSTRAINTS.sql')

    chk_view_exec_method = ''

    vi = 0

    if os.path.isfile(chk_constraints_sql_file):
        with open(chk_constraints_sql_file) as f:
            for vi, l in enumerate(f):
                vi = vi+1
                pass
            
        f.close()

    if vi > 0:
        chk_view_exec_method = 'Order based on file'

    si = 0

    chk_sp_sql_file = os.path.join(proc_folder,'PROCEDURES.sql')

    chk_sp_exec_method = ''

    if os.path.isfile(chk_sp_sql_file):
        with open(chk_sp_sql_file) as f:
            for si, l in enumerate(f):
                si = si+1
                pass
            
        f.close()

    if si > 0:
        chk_sp_exec_method = 'Order based on file'

    # Check if Initial Setup table is already present. If not, create again with appropriate static data
    log1.debug("Check if Initial Setup table is already present. If not, create again with appropriate static data")
    querystring = """ SELECT count(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME ='RS_HL_INITIAL_SETUP' and TABLE_SCHEMA = '%s' """%(db_name) # Check for table in the system table repository
    cur = cnxn.cursor()
    cur.execute(querystring)
    cnt = cur.fetchone()[0]

    cur.close()
    if cnt == 1:
        mail_msg= "Object Creation Structure already exists. Please use fresh database for initial setup."
        #RSMailer.mail(email_subject,email,mail_msg)  
        raise Exception(mail_msg)
    else:
        log1.debug("started executing the create and insert script for RS_HL_INITIAL_SETUP table")
        querystring1 = """CREATE TABLE RS_HL_INITIAL_SETUP
                            (
                                OBJECT_NAME  VARCHAR(100)
                                ,OBJECT_VALUE INT
                                ,EXEC_ORDER INT
                            );""" 
        cur1 = cnxn.cursor()
        print querystring1
        log1.debug(querystring1)
        cur1.execute(querystring1)
        cur1.close()

        querystring2 = """INSERT INTO %s.RS_HL_INITIAL_SETUP(OBJECT_NAME,OBJECT_VALUE,EXEC_ORDER)VALUES('TABLES',0,1); """ %(db_name)
        cur2_data = cnxn.cursor()
        cur2_data.execute(querystring2)
        print querystring2
        log1.debug(querystring2)
        cnxn.commit()
        cur2_data.close()

        querystring3 = """INSERT INTO %s.RS_HL_INITIAL_SETUP (OBJECT_NAME,OBJECT_VALUE,EXEC_ORDER)  VALUES('SEQUENCES',1,2);""" %(db_name)
        cur3_data = cnxn.cursor()
        cur3_data.execute(querystring3)
        print querystring3
        log1.debug(querystring3)
        cnxn.commit()
        cur3_data.close()

        querystring4= """INSERT INTO %s.RS_HL_INITIAL_SETUP (OBJECT_NAME,OBJECT_VALUE,EXEC_ORDER)  VALUES('VIEWS',1,6); """ %(db_name)
        cur4_data = cnxn.cursor()
        cur4_data.execute(querystring4)
        print querystring4
        log1.debug(querystring4)
        cnxn.commit()
        cur4_data.close()

        querystring5 = """INSERT INTO %s.RS_HL_INITIAL_SETUP (OBJECT_NAME,OBJECT_VALUE,EXEC_ORDER) VALUES('FUNCTIONS',1,4); """ %(db_name)
        cur5_data = cnxn.cursor()
        cur5_data.execute(querystring5)
        print querystring5
        log1.debug(querystring5)
        cnxn.commit()
        cur5_data.close()

        querystring6 = """INSERT INTO %s.RS_HL_INITIAL_SETUP (OBJECT_NAME,OBJECT_VALUE,EXEC_ORDER) VALUES('PROCEDURES',1,7); """ %(db_name)
        cur6_data = cnxn.cursor()
        cur6_data.execute(querystring6)
        print querystring6
        log1.debug(querystring6)
        cnxn.commit()
        cur6_data.close()

        querystring7 = """INSERT INTO %s.RS_HL_INITIAL_SETUP (OBJECT_NAME,OBJECT_VALUE,EXEC_ORDER) VALUES('CONSTRAINTS',0,3); """ %(db_name)
        cur7_data = cnxn.cursor()
        cur7_data.execute(querystring7)
        print querystring7
        log1.debug(querystring7)
        cnxn.commit()
        cur7_data.close()

        querystring8 = """INSERT INTO %s.RS_HL_INITIAL_SETUP (OBJECT_NAME,OBJECT_VALUE,EXEC_ORDER)  VALUES('INDEXES',1,9); """ %(db_name)
        cur8_data = cnxn.cursor()
        cur8_data.execute(querystring8)
        print querystring8
        log1.debug(querystring8)
        cnxn.commit()
        cur8_data.close()

        querystring9 = """INSERT INTO %s.RS_HL_INITIAL_SETUP (OBJECT_NAME,OBJECT_VALUE,EXEC_ORDER)  VALUES('SYNONYMS',1,5); """ %(db_name)
        cur9_data = cnxn.cursor()
        cur9_data.execute(querystring9)
        print querystring9
        log1.debug(querystring9)
        cnxn.commit()
        cur9_data.close()

        querystring10 = """INSERT INTO %s.RS_HL_INITIAL_SETUP (OBJECT_NAME,OBJECT_VALUE,EXEC_ORDER)  VALUES('TRIGGERS',1,10); """ %(db_name)
        cur10_data = cnxn.cursor()
        cur10_data.execute(querystring10)
        print querystring10
        log1.debug(querystring10)
        cnxn.commit()
        cur10_data.close()

    # Loop on folders present for each object type under processing folder. The count should be equal to the Static data inserted in the Initial Setup table
    obj_name_list = ['TABLES', 'SEQUENCES', 'INDEXES', 'CONSTRAINTS', 'FUNCTIONS', 'SYNONYMS', 'VIEWS', 'PROCEDURES', 'TRIGGERS']

    # Check if scripts have been executed before for any object type
    log1.debug("Checking if scripts have been executed before for any object type")
    for obj_name in obj_name_list: # Loop on object type
        querystring2 = """ SELECT 
                              OBJECT_VALUE
                          FROM
                              RS_HL_INITIAL_SETUP
                          WHERE
                              OBJECT_NAME = '%s';""" %(obj_name)
        cur2 = cnxn.cursor()
        print querystring2
        cur2.execute(querystring2)
        obj_exec_check = cur2.fetchone()[0]

        # if obj_exec_check == 1: # If object value is 1, scripts have executed fully for that object type
            # print "%s "%(obj_name)
        if obj_exec_check == 0:
            log1.debug("Started executing %s scripts" %(obj_name))

            obj_proc_folder = os.path.join(proc_folder,obj_name) # Move directory control to the relevant object type folder

            if os.path.exists(obj_proc_folder): # Check if folder actually exists
                
                os.chdir(obj_proc_folder)
                log1.debug("Started execution of table creation script")
                if obj_name == 'TABLES' and chk_tab_exec_method == 'Order based on file':
                    f1 = open(chk_tab_sql_file,'rU')
                    for line in f1:
                        data_tab_obj = line.strip()
                        file2 = data_tab_obj+".sql"

                        Object_Creation.init_setp_obj(customer, proc_folder, obj_proc_folder, file2, log_folder, obj_name, sql_server_type, email, email_subject, sql_cmd_bat_file, db_info, db_conn_type, others_log_file_folder)
                        
                    f1.close()
                    log1.debug("Completed execution of table creation script")

                elif obj_name == 'CONSTRAINTS' and chk_view_exec_method == 'Order based on file':
                    log1.debug("Started execution of Constraints creation script")
                    f1 = open(chk_constraints_sql_file,'rU')
                    for line in f1:
                        data_vw_obj = line.strip()
                        file2 = data_vw_obj+".sql"

                        Object_Creation.init_setp_obj(customer, proc_folder, obj_proc_folder, file2, log_folder, obj_name, sql_server_type, email, email_subject, sql_cmd_bat_file, db_info, db_conn_type, others_log_file_folder)
                    
                    f1.close()
                    log1.debug("Completed execution of Constraints creation script")

                elif obj_name == 'PROCEDURES' and chk_sp_exec_method == 'Order based on file':
                    log1.debug("Started execution of Procedures creation script")
                    f1 = open(chk_sp_sql_file,'rU')
                    for line in f1:
                        data_sp_obj = line.strip()
                        file2 = data_sp_obj+".sql"

                        Object_Creation.init_setp_obj(customer, proc_folder, obj_proc_folder, file2, log_folder, obj_name, sql_server_type, email, email_subject, sql_cmd_bat_file, db_info, db_conn_type, others_log_file_folder)
                    
                    f1.close()
                    log1.debug("Completed execution of Procedures creation script")
                else:
                
                    for file2 in glob.glob('*.[sS][qQ][lL]*'): # Loop on all SQL files in the folder
                        Object_Creation.init_setp_obj(customer, proc_folder, obj_proc_folder, file2, log_folder, obj_name, sql_server_type, email, email_subject, sql_cmd_bat_file, db_info, db_conn_type, others_log_file_folder)
                    
                querystring_succ_upd = """
                                    UPDATE
                                        RS_HL_INITIAL_SETUP
                                    SET
                                        OBJECT_VALUE = 1
                                    WHERE
                                        OBJECT_NAME = '%s' """ %(obj_name) # Update Success status in inital setup table

                cur4 = cnxn.cursor()
                cur4.execute(querystring_succ_upd)
                cnxn.commit()
                cur4.close()

            else:
                print "%s folder does not exist." % (obj_name) # The specified object type folder is not present
                log1.debug("%s folder does not exist." % (obj_name))
                raise Exception("Folder does not exist")

    querystring_succ_chk = " SELECT COUNT(DISTINCT OBJECT_VALUE) FROM RS_HL_INITIAL_SETUP" # Take the number of records with success value in initial setup table & compare with known value
    cur5 = cnxn.cursor()
    cur5.execute(querystring_succ_chk)
    succ_cnt = cur5.fetchone()[0]
    cur5.close()

    if succ_cnt == 1:
        log1.debug("All deployment objects created successfully")
       
    else:
        log1.debug("All deployment objects not created successfully")
        raise Exception("All deployment objects not created successfully")  

    # Start Static data insertion deployment
    log1.debug("Start Static data insertion deployment")

    data_folder = os.path.join(proc_folder,"DATA")

    # Check for existence for Static data insertion check repository. Create table if not present & add values.
    log1.debug("Check for existence for Static data insertion check repository. Create table if not present & add values")
    querystring_dt_chk = """SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME ='RS_HL_STATICTBL' and TABLE_SCHEMA = '%s' """%(db_name) 
    cur6 = cnxn.cursor()
    cur6.execute(querystring_dt_chk)
    dt_chk_cnt = cur6.fetchone()[0]

    log1.debug("create RS_HL_STATICTBL if not present")
    if dt_chk_cnt == 0:
        querystring_dt_ins = """ CREATE TABLE RS_HL_STATICTBL
                            (
                                TABLE_NAME  VARCHAR(100)
                                ,TABLE_VALUE INT
                                ,EXEC_ORDER INT
                            );
                            
                             """
                    
        cur7 = cnxn.cursor()
        cur7.execute(querystring_dt_ins)
        cur7.close()
        log1.debug("insert the static data record to RS_HL_STATICTBL table")
        querystring_static = """INSERT INTO RS_HL_STATICTBL (TABLE_NAME,TABLE_VALUE,EXEC_ORDER)   VALUES('STATIC_DATA',1,1); """
        curl_st_data = cnxn.cursor()
        curl_st_data.execute(querystring_static)
        log1.debug(querystring_static)
        cnxn.commit()
        curl_st_data.close()
    querystring_dt_chk1 = """select COUNT(TABLE_VALUE) from RS_HL_STATICTBL WHERE TABLE_VALUE=1""" 
    cur11 = cnxn.cursor()
    cur11.execute(querystring_dt_chk1)
    dt_chk_cnt1 = cur11.fetchone()[0]
    cur11.close
    if dt_chk_cnt1 == 1:
	log1.debug("STATIC DATA scripts are already executed")
	mail_msg = """ Scripts Deployment failed in  script execution. STATIC DATA scripts are already executed."""
	log1.debug("Scripts Deployment failed in  script execution. STATIC DATA scripts are already executed")
	#RSMailer.mail(email_subject,email,mail_msg)
	#raise Exception(mail_msg)
    if os.path.exists(data_folder): # Check for existence of data folder


        data_app_folder = data_folder
        log1.debug("check if the STATIC_DATA.SQL file exists or not in the given directory")
        data_app_file = os.path.join(data_folder,"STATIC_DATA.SQL") # Check for SQL file which contains the names of static data files required for application to run. Also, check for number of records in file
	if (not os.path.exists(data_app_file)):
	    data_app_file = os.path.join(data_folder,"STATIC_DATA.sql")
        if (os.path.exists(data_app_file)):

            with open(data_app_file) as f:
		i=0
                for i, l in enumerate(f):
                    i = i+1
                    pass

            f.close()
            dt_exec_check=0
            if i == 0:
                log1.debug("App Static data config file does not contain any records")
            else:

                if os.path.exists(data_app_folder):
                    f1 = open(data_app_file,'rU')
                    for line in f1:
                        # items=f1.readline().split()
                        if dt_exec_check == 0:
                            data_obj="STATIC_DATA"	
                            app_stat_dt_file = os.path.join(data_app_folder,data_obj+".SQL")
                            app_stat_wo_ext = data_obj+".SQL"

                            app_stat_dt_log_file = os.path.join(data_app_log_folder,"logs_"+data_obj+".txt") # Logging file for Application static data insertion
                            if (not os.path.exists(app_stat_dt_file)):
			        app_stat_dt_file=os.path.join(data_app_folder,data_obj+".sql")			
                            if (os.path.exists(app_stat_dt_file)):

                                # Execute the static data SQL file which will insert data into the required table & log errors if any into the log file configured above
                                log1.debug("Execute the static data SQL file which will insert data into the required table & log errors if any into the log file configured above")
                                sqlQuery = ''

                                err_str_data = ''
                                
                                with open(app_stat_dt_file, 'r') as inp:
                                    for line in inp:
                                        if (';' in line):
                                            
                                            try:
                                                sqlQuery = sqlQuery + line
                                                curobj = cnxn.cursor()
                                                curobj.execute(sqlQuery)
                                                print sqlQuery
                                                cnxn.commit()
                                                curobj.close()
                                                sqlQuery = ''

                                            except:
                                                err_str_data = "SQL Execution failed: " + sqlQuery
                                                with open(app_stat_dt_log_file,'a') as outfile:
                                                    outfile.write(err_str_data)
                                                outfile.close()
                                                
                                                mail_msg = """%s Scripts Deployment failed in '%s' script execution""" % (customer,app_stat_dt_file)
                                                log1.debug("""%s Scripts Deployment failed in '%s' script execution""" % (customer,app_stat_dt_file))
                                                #RSMailer.mail(email_subject,email,mail_msg,app_stat_dt_log_file)
                                                raise Exception(mail_msg)
                                            
                                            sqlQuery = ''
                                        elif 'PRINT' in line:
                                            disp = line.split("'")[1]
                                            print(disp, '\r')
                                        else:
                                            sqlQuery = sqlQuery + line
                                inp.close()
                            
                            else:
                                mail_msg = "'%s' data insertion file not present" %app_stat_dt_file
                                log1.debug("'%s' data insertion file not present" %app_stat_dt_file)
                                #RSMailer.mail(email_subject,email,mail_msg)
                                raise Exception(mail_msg)
                        dt_exec_check=1    
                        app_dt_succ_upd = """
                                    UPDATE
                                        RS_HL_STATICTBL
                                    SET
                                        TABLE_VALUE = 1
                                    WHERE
                                        TABLE_NAME = '%s' """ %(data_obj) # Update Success status in inital setup table

                        app_dt_cur = cnxn.cursor()
                        app_dt_cur.execute(app_dt_succ_upd)
                        cnxn.commit()
                        app_dt_cur.close()
                    	
                    f1.close()                      
                else:
                    log1.debug("App Data folder not present")
        else:
            mail_msg = "App Static data config file not present"
            log1.debug("App Static data config file not present")
            #RSMailer.mail(email_subject,email,mail_msg)
            raise Exception(mail_msg)
        
        # The process used for Application static data insertion will be used for Config static data insertion process
        
        data_cfg_folder = os.path.join(data_folder,"CONFIG")

        data_cfg_file = os.path.join(data_folder,"CONFIG_DATA.SQL")
        
        if (os.path.exists(data_cfg_file)):
            with open(data_cfg_file) as f:
                for i3, l in enumerate(f):
                    i3 = i3+1
                    pass

            f.close()
            
            if i3 == 0:
                print "Config Static data config file does not contain any records"
                log1.debug("Config Static data config file does not contain any records")
            else:
                if os.path.exists(data_cfg_folder):
                    f2 = open(data_cfg_file,'rU')
                    for line in f2:
                        # items1=f2.readline().split()
                        cfg_data_obj = line.strip()

                        querystring_cfg_ins_chk = """ SELECT 
                                              TABLE_VALUE
                                          FROM
                                              RS_HL_STATICTBL
                                          WHERE
                                              TABLE_NAME = '%s';""" %cfg_data_obj
                        cur9 = cnxn.cursor()
                        
                        try:
                            cur9.execute(querystring_cfg_ins_chk)
                            cfg_dt_exec_check = cur9.fetchone()[0]
                        except:
                            mail_msg = """ Please check CONFIG_DATA config file """
                            log1.debug(""" Please check CONFIG_DATA config file """)
                            #RSMailer.mail(email_subject,email,mail_msg)
                            raise Exception(mail_msg)
                        
                        cur9.close()

                        if cfg_dt_exec_check == 1: 
                            log1.debug("%s data insertion script already executed in the specified database" %cfg_data_obj)
                        else:
                         
                            cfg_stat_dt_file = os.path.join(data_cfg_folder,cfg_data_obj+".SQL")

                            cfg_stat_wo_ext = cfg_data_obj+".SQL"

                            cfg_stat_dt_log_file = os.path.join(data_cfg_log_folder,"logs_"+cfg_data_obj+".txt")

                            logging.basicConfig(filename=cfg_stat_dt_log_file, level=logging.DEBUG)

                            if (os.path.exists(cfg_stat_dt_file)):

                                sqlQuery = ''

                                err_str_data = ''
                                
                                with open(cfg_stat_dt_file, 'r') as inp:
                                    for line in inp:
                                        if (';' in line):
                                            
                                            try:
                                                sqlQuery = sqlQuery + line
                                                curobj = cnxn.cursor()
                                                curobj.execute(sqlQuery)
                                                #cnxn.commit()
                                                curobj.close()
                                            except:
                                                err_str_data = "SQL Execution failed: " + sqlQuery
                                                with open(cfg_stat_dt_log_file,'a') as outfile:
                                                    outfile.write(err_str_data)
                                                outfile.close()
                                                
                                                mail_msg = """%s Scripts Deployment failed in '%s' script execution""" % (customer,cfg_stat_dt_file)
                                                log1.debug("""%s Scripts Deployment failed in '%s' script execution""" % (customer,cfg_stat_dt_file))
                                                #RSMailer.mail(email_subject,email,mail_msg,cfg_stat_dt_log_file)
                                                raise Exception(mail_msg)
                                            
                                            sqlQuery = ''
                                        elif 'PRINT' in line:
                                            disp = line.split("'")[1]
                                            print(disp, '\r')
                                        else:
                                            sqlQuery = sqlQuery + line
                                inp.close()
                            
                            else:
                                mail_msg = "'%s' data insertion file not present" %cfg_stat_dt_file
                                log1.debug("'%s' data insertion file not present" %cfg_stat_dt_file)
                                #RSMailer.mail(email_subject,email,mail_msg)
                                raise Exception(mail_msg)
                            
                        cfg_dt_succ_upd = """
                                    UPDATE
                                        RS_HL_STATICTBL
                                    SET
                                        TABLE_VALUE = 1
                                    WHERE
                                        TABLE_NAME = '%s' """ %(cfg_data_obj) # Update Success status in inital setup table

                        cfg_dt_cur = cnxn.cursor()
                        cfg_dt_cur.execute(cfg_dt_succ_upd)
                        cnxn.commit()
                        cfg_dt_cur.close()
                                
                    f2.close()
                else:
                    log1.debug("Config Data folder not present")
        else:
            mail_msg = "Config Static data config file not present"
            log1.debug("Config Static data config file not present")
            # RSMailer.mail(email_subject,email,mail_msg)
            # raise Exception(mail_msg)
    else:
        log1.debug("Data folder not present")

    # In the below query, a count of 2 indicates that only success value is present in both object creation as well as static data insertion metadata tables & hence the process is successful

    querystring_tot_prc_chk = """
                                SELECT 
                                        COUNT(*)
                                FROM 
                                        (
                                        SELECT 
                                                DISTINCT TABLE_VALUE
                                        FROM 
                                                RS_HL_STATICTBL
                                        UNION ALL
                                        SELECT 
                                                DISTINCT OBJECT_VALUE
                                        FROM 
                                                RS_HL_INITIAL_SETUP
                                        ) A """

    cur10 = cnxn.cursor()
    cur10.execute(querystring_tot_prc_chk)
    tot_prc_chk_cnt = cur10.fetchone()[0]

    if tot_prc_chk_cnt == 2:
        mail_msg = "%s deployment completed successfully" %customer
        log1.debug("%s deployment completed successfully" %customer)
        print mail_msg
        #RSMailer.mail(email_subject,email,mail_msg)
    else:
        mail_msg = "%s deployment not completed successfully" %customer
        print mail_msg
        log1.debug("%s deployment not completed successfully" %customer)
        #RSMailer.mail(email_subject,email,mail_msg)
        raise Exception(mail_msg)

    cur10.close()    
