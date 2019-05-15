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
echo "create $XDF_DATA_ROOT/.metadata/transformations" | mapr dbshell
echo "create $XDF_DATA_ROOT/.metadata/datapods" | mapr dbshell
echo "create $XDF_DATA_ROOT/.metadata/datasegments" | mapr dbshell
echo "create $XDF_DATA_ROOT/.metadata/auditlog" | mapr dbshell
echo "create $XDF_DATA_ROOT/.metadata/projects" | mapr dbshell

exit 0
