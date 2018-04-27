#!/bin/bash
TEST_DIR=$( cd $(dirname $0)/../test && pwd -P )
: ${TEST_DIR:?no value}

#######################################
# 1 - DL Root
# 2 - Project Name
# 3 - Project Description
# 4 - (optional) JSON Property file for PLP
#######################################

XDF_DIR=/dfs/opt/bda/xdf-ngsr-current/
LOG4J_CONF=$XDF_DIR/conf/log4j.properties

CP=$XDF_DIR/conf/log4j.properties:$XDF_DIR/lib/xdf-rest-1.0.0_dev-all.jar
CP=$CP:$(mapr classpath)

java -cp ${CP} -Dlog4j.configuration=file:$LOG4J_CONF   sncr.xdf.metastore.ProjectStore "$1" "$2" "$3" $