#!/bin/bash

####################################
#
#  input parameters:
#  1 - XDF data root
#
####################################

XDF_DATA_ROOT=${1:=/data/bda/datalake}

hadoop fs -mkdir /data/bda/datalake/.metadata

echo "create $XDF_DATA_ROOT/.metadata/datasets" | mapr dbshell
echo "create /data/bda/datalake/.metadata/transformations" | mapr dbshell
echo "create /data/bda/datalake/.metadata/datapods" | mapr dbshell
echo "create /data/bda/datalake/.metadata/datasegments" | mapr dbshell
echo "create /data/bda/datalake/.metadata/auditlog" | mapr dbshell

exit 0
