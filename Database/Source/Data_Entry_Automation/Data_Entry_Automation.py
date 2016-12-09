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
#read the Customer Name
cust_name = sys.argv[1]
print cust_name

#read db server details
db_server_details=sys.argv[2]
print db_server_details

db_details=db_server_details.split('|')

print len(db_details)
if len(db_details)<> 4:
    msg="The Entered Database details are not in correct format \n The Expected format is SERVER|DATABASE|USER NAME|PASSWORD"
    logging.exception(msg)
db_details_dict={}
db_details_dict['server'] = db_details[0]
db_details_dict['db_name'] = db_details[1]
db_details_dict['user_name'] = db_details[2]
db_details_dict['user_password'] = db_details[3]
print db_details_dict
#read the node name
node_name=sys.argv[3].upper()
print node_name

#read the tables structure excel file absolute full path
excel_file_path=sys.argv[4]
print excel_file_path

#read the config json file path
config_file_path = sys.argv[5]

#read the inserting data for the given table
inserting_data = sys.argv[6]
inserting_data_list=inserting_data.split('|')

print inserting_data_list
print ""
print ""
print "##############################################################################"
print ""
print "parameter 1  Customer Name                         : "+ str(cust_name)
print "parameter 2  Database server details               : "+ str(db_server_details)
print "parameter 3  Node/Table Name                       : "+ str(node_name)
print "parameter 4  Source Excel(xlsx) file absolute path : "+ str(excel_file_path)
print "parameter 5  Configuration file absolute path      : "+ str(config_file_path)
print "parameter 6  Inserting value for the given table      : "+ str(inserting_data)
print""
print "##############################################################################"

#check if excel file exists in the given path
if(not(os.path.isfile(excel_file_path))):
    msg="table structure excel file not present in the given location : " + excel_file_path
    logging.exception(msg)

#check if the Config json file exists in the given path
if(not(os.path.isfile(config_file_path))):
    msg="the configuration json file not present in the given location : " + config_file_path
    logging.exception(msg)

#read the config json data
config_data=Data_Entry_Automation_Utilities.load_config(config_file_path)
print config_data

excel_sheet_name="SAW_2_0_Table_Mapping"

#read table mapping excel file data
source_excel_data = Data_Entry_Automation_Utilities.get_excel_file_data(node_name,excel_file_path,excel_sheet_name)

config_file_column_name = config_data['node_column_name']
table_name = config_data['table_name']
column_name = config_data['column_name']
default_value = config_data['default_value']
insert_required_column = config_data['required_insert_column_name']
required_input_column_name = config_data['required_input_column_name']
node_name_check_flg,columns_cnt,table_name_column,input_required_columns,default_column_values = Data_Entry_Automation_Utilities.validate_node_name(config_file_column_name,source_excel_data,node_name,required_input_column_name,table_name,column_name,default_value,insert_required_column)

print node_name_check_flg
print columns_cnt
print table_name_column
print input_required_columns
print "below is the default values"
print default_column_values

inserting_data_with_column = dict(zip(input_required_columns, inserting_data_list))
print "below is the inserting_data"
print inserting_data_with_column

#combine the inserting columns values dictionary with default column values
final_inserting_data_dict=Data_Entry_Automation_Utilities.get_final_inserting_data(inserting_data_with_column,default_column_values)
print "below is the final inserting data"
print final_inserting_data_dict

#checking the node name is exist or not in the table mapping excel file
if (int(node_name_check_flg)==0):
    msg="The"+str(node_name)+" is not available in the table structure mapping excel document:"+str(excel_file_path)
    logging.exception(msg)
    sys.exit(11)

#checking the inserting value for the given table
if columns_cnt<>len(inserting_data_list):
    msg="the given values for the "+node_name+" is not proper.\n please check the values. it expecting values for "+str(columns_cnt)+" Columns"
    print msg
    logging.exception(msg)
    sys.exit(12)
#get the database connection
db_conx=Data_Entry_Automation_Utilities.get_maria_db_connection(db_details_dict)
object_exists_flag=0

#loop through each objects
for row in config_data['add_objects']:
    print row['name'].upper()
    if table_name_column.upper()==row['name'].upper():
        object_exists_flag=1
        if row['split_required'].upper()=='YES':
            final_inst_with_split_data= Data_Entry_Automation_Utilities.check_split_by_column_data(db_conx,row,final_inserting_data_dict)
            print final_inst_with_split_data
            #sys.exit(15)
        validation_result=Data_Entry_Automation_Utilities.check_table_validation(db_conx,row,inserting_data_with_column)
        if validation_result ==-1:
            print "validation query is not available for the table: "+str(row['name'].upper())
        if validation_result==0:
            print "validation is failed for the table: "+str(row['name'].upper())+"\nthe inserting value's were already exists in the database"
            sys.exit(13)
        if validation_result==1:
            print "validation success"
            insert_result=Data_Entry_Automation_Utilities.insert_table(db_conx,row,table_name,final_inserting_data_dict)
            print str(insert_result) + " Records got inserted to the " + str(row['name'].upper()) + " table"

if object_exists_flag==0:
    print "the object mapping is not available in the given configuration json file for the :"+str(config_data['add_objects'])+" table "
