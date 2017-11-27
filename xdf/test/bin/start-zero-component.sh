#!/bin/bash

#######################################
#
# 1 - Component name
# 2 - project name ( application ID ) 
# 3 - Batch ID
#
########################################

APP_HOME=/dfs/opt/bda/apps/xda-ux-sr-comp-dev-1.0.0_dev
conf=$APP_HOME/conf/zeroComponent.jconf

#PROJECT=$2
PROJECT=xda-ux-sr-comp-dev	
COMPONENT=zero
BATCH=TEST1
IP=10.238.42.86
PORT=15010

curl -XPOST -H "Content-Type: application/json" -d "@$conf" "http://$IP:$PORT/run?prj=$PROJECT&component=$COMPONENT&batch=$BATCH"

echo

curl -XGET http://$IP:$PORT/status

echo



