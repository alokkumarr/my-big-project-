#!/bin/bash
echo "Starting xdf-rest service JVM"

# Executable script for xdf version 2+
CMD_DIR=$( cd $(dirname $0); pwd )

## Calculate XDF_DIR, SERVER_DIR, VERSION
# Check xdf_info executable
$CMD_DIR/xdf_info || exit
xdf_info() {
    $CMD_DIR/xdf_info ${1:?xdf_info argument missing}
}

export XDF_DATA_ROOT=hdfs:///data/bda

XDF_DIR=$( xdf_info optdir )
: ${XDF_DIR:?no value}
SERVER_DIR=$XDF_DIR/server
( cd $SERVER_DIR ) || exit

CONF_DIR=$XDF_DIR/conf
( cd $CONF_DIR ) || exit

VERSION=$( xdf_info version )
: ${VERSION:?no value}

LOG4J_CONF=$XDF_DIR/conf/log4j.xml
APPLIB=$SERVER_DIR/xdf-rest-${VERSION}-all.jar

COMP_NAME=$2
LOG_DIR=/dfs/var/bda/xdf-ux/log
COMP_LOG_DIR=/dfs/var/bda/xdf-ux/log/$COMP_NAME

( cd $COMP_LOG_DIR ) || mkdir -p $COMP_LOG_DIR

CONF_OPT="-Dlog.dir=$LOG_DIR -Dxdf.core=$3 -Dlog4j.configuration=file:$LOG4J_CONF -Dcomp.log.dir=$COMP_LOG_DIR"
/opt/mapr/spark/spark-current/bin/spark-submit \
    --driver-java-options "$CONF_OPT" \
    --class sncr.xdf.rest.Server \
    $APPLIB \
    task \
    $XDF_DIR/conf/xdf-rest.conf \
    $@