#-------------------------------------------------------------------------------
# Name :        SAW Data Entry Automation 
# Purpose :     To Automate the Add/Update/Delete the metadata 
# Arguments :
#                1. Customer Name
#                2. Database Server Details
#                3. Node Name
#                4. Mapping Excel(xlsx) file absolute path
#                5. Configuration file absolute path
#                6. Inserting value for the given table
#
# Author:      Kiran PS
#
# Created:     17-11-2016
# Copyright:   (c) Kiran.PS 2016
# Licence:     <your licence>
#-------------------------------------------------------------------------------
import os
import sys
import re
import logging
import Data_Entry_Automation_Utilities
import MySQLdb
import logging
cmd_arg = sys.argv[0]
cmd_dir = os.path.dirname(os.path.abspath(cmd_arg))
sys.path.insert(0, os.path.dirname(os.path.abspath(cmd_dir)))
import bda_init

log1 = bda_init.get_logger()

log1.debug("Starting the Data Entry Automation Script")

#read the Customer Name
cur_dir=os.path.dirname(os.path.realpath(__file__))
#read db server details
db_server_details=sys.argv[1]

db_details=db_server_details.split('|')

log1.debug("reading the database details")
if len(db_details)<> 4:
    msg="The Entered Database details are not in correct format \n The Expected format is SERVER|DATABASE|USER NAME|PASSWORD"
    log1.debug("The Entered Database details are not in correct format \n The Expected format is SERVER|DATABASE|USER NAME|PASSWORD")
db_details_dict={}
db_details_dict['server'] = db_details[0]
db_details_dict['db_name'] = db_details[1]
db_details_dict['user_name'] = db_details[2]
db_details_dict['user_password'] = db_details[3]
print db_details_dict

log1.debug("Get the Table/Node name ")
#read the node name
node_name=sys.argv[2].upper()
print node_name

log1.debug("Started execution fot the "+str(node_name)+" table ")

log1.debug("Get table structure mapping file absolute file path")
#read the tables structure excel file absolute full path
csv_file_path=os.path.join(cur_dir, "table_structure_mapping.csv")
print csv_file_path

#read the config json file path
log1.debug("Get the configuratoion json file absolute file path")
config_file_path = os.path.join(cur_dir, "conf/configuration.json")

log1.debug("reading the inserting values for the "+str(node_name)+"table name")
#read the inserting data for the given table
inserting_data = sys.argv[3]
inserting_data_list=inserting_data.split('|')

print inserting_data_list
print ""
print ""
print "##############################################################################"
print ""
print "parameter 1  Database server details               : "+ str(db_server_details)
print "parameter 2  Node/Table Name                       : "+ str(node_name)
print "parameter 3  Source Excel(xlsx) file absolute path : "+ str(csv_file_path)
print "parameter 4  Configuration file absolute path      : "+ str(config_file_path)
print "parameter 5  Inserting value for the given table      : "+ str(inserting_data)
print""
print "##############################################################################"

#check if excel file exists in the given path
if(not(os.path.isfile(csv_file_path))):
    msg="table structure excel file not present in the given location : " + csv_file_path
    log1.debug(msg)
    log1.exception(msg)

#check if the Config json file exists in the given path
if(not(os.path.isfile(config_file_path))):
    msg="the configuration json file not present in the given location : " + config_file_path
    log1.debug("the configuration json file not present in the given location : " + config_file_path)
    logging.exception(msg)

#read the config json data
log1.debug("reading the configuration json file ")
config_data=Data_Entry_Automation_Utilities.load_config(config_file_path)
#print config_data

sheet_name="SAW_Table_Mapping"

#read table mapping excel file data
log1.debug("get the list of columns for the '%s' table  from the table structure csv file" %(node_name))
source_excel_data = Data_Entry_Automation_Utilities.get_excel_file_data(node_name,csv_file_path,sheet_name)

config_file_column_name = config_data['node_column_name']
table_name = config_data['table_name']
column_name = config_data['column_name']
default_value = config_data['default_value']
insert_required_column = config_data['required_insert_column_name']
required_input_column_name = config_data['required_input_column_name']
node_name_check_flg,columns_cnt,table_name_column,input_required_columns,default_column_values = Data_Entry_Automation_Utilities.validate_node_name(config_file_column_name,source_excel_data,node_name,required_input_column_name,table_name,column_name,default_value,insert_required_column)

log1.debug(node_name_check_flg)
log1.debug(columns_cnt)
log1.debug(table_name_column)
log1.debug(input_required_columns)
log1.debug(default_column_values)

log1.debug("prepareing the insert query for the table")
inserting_data_with_column = dict(zip(input_required_columns, inserting_data_list))
log1.debug(inserting_data_with_column)

#combine the inserting columns values dictionary with default column values
log1.debug("combine the inserting columns values dictionary with default column values")
final_inserting_data_dict=Data_Entry_Automation_Utilities.get_final_inserting_data(inserting_data_with_column,default_column_values)
log1.debug(final_inserting_data_dict)

#checking the node name is exist or not in the table mapping excel file
if (int(node_name_check_flg)==0):
    msg="The"+str(node_name)+" is not available in the table structure mapping excel document:"+str(csv_file_path)
    log1.debug(msg)
    sys.exit(11)

#checking the inserting value for the given table
if columns_cnt<>len(inserting_data_list):
    msg="The given values for the "+node_name+" is not proper.\n please check the values. it expecting values for "+str(columns_cnt)+" Columns"
    log1.debug(msg)
    sys.exit(12)
#get the database connection
db_conx=Data_Entry_Automation_Utilities.get_maria_db_connection(db_details_dict)
object_exists_flag=0

#loop through each objects
for row in config_data['add_objects']:
    if table_name_column.upper()==row['name'].upper():
        log1.debug(row['name'].upper())
        object_exists_flag=1
        if row['split_required'].upper()=='YES':
            final_inst_with_split_data= Data_Entry_Automation_Utilities.check_split_by_column_data(db_conx,row,final_inserting_data_dict)
            log1.debug(final_inst_with_split_data)
            #sys.exit(15)
        validation_result=Data_Entry_Automation_Utilities.check_table_validation(db_conx,row,inserting_data_with_column)
        if validation_result ==-1:
            log1.debug("Validation query is not available for the table: "+str(row['name'].upper()))
        if validation_result==0:
            log1.debug("Validation is failed for the table: "+str(row['name'].upper())+"\n The inserting value's were already exists in the database")
            sys.exit(13)
        if validation_result==1:
            log1.debug("validation success")
            insert_result=Data_Entry_Automation_Utilities.insert_table(db_conx,row,table_name,final_inserting_data_dict)
            log1.debug(str(insert_result) + " Records got inserted to the " + str(row['name'].upper()) + " table")

if object_exists_flag==0:
    log1.debug("the object mapping is not available in the given configuration json file for the :"+str(config_data['add_objects'])+" table ")