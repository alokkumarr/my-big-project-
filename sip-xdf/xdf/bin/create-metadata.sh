#!/bin/bash

####################################
#
#  input parameters:
#  1 - XDF data root
#
####################################

MAPR_HOME=/opt/mapr
isSecure=$(head -1 ${MAPR_HOME}/conf/mapr-clusters.conf | grep -o 'secure=\w*' | cut -d= -f2)
 if [ "$isSecure" = "true" ] && [ -f "${MAPR_HOME}/conf/mapruserticket" ]; then
        export MAPR_TICKETFILE_LOCATION="${MAPR_HOME}/conf/mapruserticket"
 fi
XDF_DATA_ROOT=${1:=/data/bda/datalake}

hadoop fs -mkdir /data/bda/datalake/.metadata

echo "create $XDF_DATA_ROOT/.metadata/datasets" | mapr dbshell
echo "create $XDF_DATA_ROOT/.metadata/transformations" | mapr dbshell
echo "create $XDF_DATA_ROOT/.metadata/datapods" | mapr dbshell
echo "create $XDF_DATA_ROOT/.metadata/datasegments" | mapr dbshell
echo "create $XDF_DATA_ROOT/.metadata/auditlog" | mapr dbshell
echo "create $XDF_DATA_ROOT/.metadata/projects" | mapr dbshell

exit 0
