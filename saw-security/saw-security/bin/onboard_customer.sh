#!/usr/bin/bash
#Script to run sql queries to onboard new customer
#Declaring mysql DB connection

MASTER_DB_USER='root'
MASTER_DB_PASSWD='root'
MASTER_DB_PORT='3160'
MASTER_DB_HOST='192.168.0.0'
MASTER_DB_NAME='saw_security'

read -p "Enter customer_code (Ex: SYCHRONOSS0001) : " n1
read -p "Enter product_name (Ex: SAWDEMO) : " n2
read -p "Enter product_code (Ex: workbench): " n3
read -p "Enter email (Ex: abc@xyz.com): " n4
read -p "Enter first_name : " n5
read -p "Enter middle_name : " n6
read -p "Enter last_name : " n7

#Prepare sql query
SQL_Query='call onboard_customer("$n1","$n2","$n3","$n4","$n5","$n6","$n7")'


#mysql command to connect to database

MYSQL -u$MASTER_DB_USER -p$MASTER_DB_PASSWD  -D$MASTER_DB_NAME <<EOF
$SQL_Query
EOF
echo "End of script"
