import os
from xlrd import open_workbook
import sys
import json
import logging
import MySQLdb
#-------------------------------------------------------------------------------
# Name :        Data Entry Automation Utilities
# Purpose :     to seperate the function to improve the coding structure
#				and we can use this file from others scrips
#
# Author:      Kiran PS
#
# Created:     17-11-2016
# Copyright:   (c) Kiran.PS 2016
# Licence:     <your licence>
#-------------------------------------------------------------------------------

################################################################################
#
# purpose : checking the node name is exist in the table structure excel file or not
#
#
# template load_config("file_name.json")
#
# example load_config("fileDataValidator.json")
#
################################################################################
def get_excel_file_data(node_name,excel_file_path,excel_sheet_name):
    print "inside function"
    print excel_sheet_name
    strting_row=0
    wb = open_workbook(excel_file_path)

    excel_sheet_flag = 0
    for s in wb.sheets():
        print 'The Processing Sheet Name is ::' + s.name
        # check if the sheet is expected sheet given by the user
        if excel_sheet_name.upper() == s.name.upper():
            keys = [s.cell(0, col_index).value for col_index in xrange(s.ncols)]

            dict_list = []
            for row_index in xrange(1, s.nrows):
                d = {keys[col_index]: s.cell(row_index, col_index).value
                     for col_index in xrange(s.ncols)}
                dict_list.append(d)

            excel_sheet_flag = 1
        else:
            print "The " + s.name + " is not the Expected Excel Sheet Name"
            # exit with a error code as 14 if it did not fine the expected sheet name
    if excel_sheet_flag == 0:
        msg = "  The " + excel_sheet_name + " Sheet does not exist in the given " + excel_file_path + " excel file"
        logging.exception(msg)
    return dict_list
################################################################################

################################################################################
#
# purpose : read a json file and retun the json formated data
#
#
# template get_value(array_of_lists,search_field,search_value,value_field)
#
# example load_config("fileDataValidator.conf")
#
################################################################################

def load_config(filename):
    file_data =open(filename).read()
    json_data = json.loads(file_data)
    return json_data
################################################################################

################################################################################
#
# purpose : checking the node name is exist in the table structure excel file or not
#
#
# template load_config("file_name.json")
#
# example load_config("fileDataValidator.json")
#
################################################################################
def validate_node_name(config_file_column_name,source_excel_data,node_name,required_input_column_name,table_name,column_name,default_value,insert_required_column):
    validation_flag=0
    columns_cnt=0
    table_name_column='N/A'
    input_required_columns=[]
    default_column_values={}
    for row in source_excel_data:
        if row[config_file_column_name].upper()==node_name.upper():
            validation_flag = 1
            table_name_column = row[table_name]
            if row[required_input_column_name]==1:
                columns_cnt=columns_cnt+1
                input_required_columns.append(row[column_name])
            if row[required_input_column_name]==0 and row[insert_required_column]==1 and row[default_value] <> '':
                default_column_values[row[column_name]]=row[default_value]

    return validation_flag,columns_cnt,table_name_column,input_required_columns,default_column_values
################################################################################

################################################################################
#
# purpose : get the connection to the maria database.
#
#
# template get_maria_db_connection(db_details_dict)
#
# example get_maria_db_connection(db_details_dict)
#
################################################################################
def get_maria_db_connection(db_details_dict):
    print "inside db connection"
    print db_details_dict
    try:
        cnxn = MySQLdb.connect(db_details_dict['server'], db_details_dict['user_name'], db_details_dict['user_password'], db_details_dict['db_name'])  # MYSQL Server Authentication
        print "Mariadb Connection Success"

    except:
        raise Exception("PARAMETER ISSUE:Please verify DB parameters provided.")
    return cnxn
################################################################################

################################################################################
#
# purpose : check the table validation
#
#
# template check_table_validation(db_conx,row,inserting_data_with_column)
#
# example check_table_validation(db_conx,row,inserting_data_with_column)
#
################################################################################
def check_table_validation(db_conx,row,inserting_data_with_column):
    validation_query=''
    print inserting_data_with_column
    if row['validation_required'].upper()=='YES':
        print str(row['name'])+" table has the validation check "
        validation_query= row['validation_query']
        validation_query=query_modification(validation_query,inserting_data_with_column)
        print "validation query is :" + str(validation_query)
        validation_result=execute_sql(db_conx, validation_query)
        print "validation query result is : "+validation_result
        if validation_result.upper()==row['validation_success_result'].upper():
            print "the validation is successfull"
            return 1
        elif validation_result.upper()==row['validation_failure_result'].upper():
            print "validation query is failed for the table "+str(row['name'])
            return 0
    else:
        return -1
################################################################################

################################################################################
#
# purpose : excute the select sql query
#
#
# template execute_sql(db_conx,validation_query)
#
# example execute_sql(db_conx,validation_query)
#
################################################################################
def execute_sql(db_conx,validation_query):
    db_select_conx = db_conx.cursor()
    db_select_conx.execute(validation_query)
    db_select_result = db_select_conx.fetchone()[0]
    db_select_conx.close()
    return db_select_result
################################################################################

################################################################################
#
# purpose : execute the insert table sql query
#
#
# template execute_insert_sql(db_conx,insert_query)
#
# example execute_insert_sql(db_conx,insert_query)
#
################################################################################
def execute_insert_sql(db_conx,insert_query):
    db_insert_conx = db_conx.cursor()
    affected_count=db_insert_conx.execute(insert_query)
    print insert_query
    logging.warn("%d", affected_count)
    db_conx.commit()
    db_insert_conx.close()
    return affected_count
################################################################################

################################################################################
#
# purpose : modifying the validation query with the proper data.
#
#
# template query_modification(validation_query,inserting_data_with_column)
#
# example query_modification(validation_query,inserting_data_with_column)
#
################################################################################
def query_modification(validation_query,inserting_data_with_column):
    for name in inserting_data_with_column:
        validation_query = validation_query.replace(name + "_INPUT", inserting_data_with_column[name])
        validation_query = validation_query.encode('ascii', 'ignore')
    return validation_query
################################################################################

################################################################################
#
# purpose : merging the two dictionary into a single dictionary
#
#
# template get_final_inserting_data(inserting_data_with_column,default_column_values)
#
# example get_final_inserting_data(inserting_data_with_column,default_column_values)
#
################################################################################
def get_final_inserting_data(inserting_data_with_column,default_column_values):
    final_data=inserting_data_with_column
    for row in default_column_values:
        final_data[row]=default_column_values[row]
    return final_data
################################################################################

################################################################################
#
# purpose : insert the data to given table
#
#
# template insert_table(db_conx,row,table_name,final_inserting_data_dict)
#
# example insert_table(db_conx,row,table_name,final_inserting_data_dict)
#
################################################################################
def insert_table(db_conx,row,table_name,final_inserting_data_dict):
    insert_query=row['insert_query']
    print insert_query
    insert_query=query_modification(insert_query,final_inserting_data_dict)
    print insert_query
    insert_result=execute_insert_sql(db_conx,insert_query)
    return insert_result
################################################################################

################################################################################
#
# purpose : split the data into required columns data
#
#
# template check_split_by_column_data(db_conx,row,final_inserting_data_dict)
#
# example check_split_by_column_data(db_conx,row,final_inserting_data_dict)
#
################################################################################
def check_split_by_column_data(db_conx,row,final_inserting_data_dict):
    split_dict={}
    for col_name in final_inserting_data_dict:
        if col_name.upper()==row['split_data_mapping']['name'].upper():
            split_col_data=final_inserting_data_dict[col_name].split(',')
            split_col_name= row['split_data_mapping']['split_values']
            if len(split_col_name) <> len(split_col_data):
                print "th Split column data and column names list are not same"
                sys.exit(14)
            for cnt,col in enumerate(split_col_name):
                split_dict[col] = split_col_data[cnt]
    final_inst_with_split_data=final_inserting_data_dict
    if len(split_dict) >=1:
        final_inst_with_split_data=get_final_inserting_data(final_inserting_data_dict,split_dict)
    return final_inst_with_split_data

################################################################################