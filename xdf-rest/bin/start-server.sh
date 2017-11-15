#!/bin/bash
# Executable script for xdf version 2+
CMD_DIR=$( cd $(dirname $0); pwd )

JAVA_CMD=$( command -v java )
: ${JAVA_CMD:?no value}

## Calculate XDF_DIR, SRV_DIR, VERSION
# Check xdf_info executable
$CMD_DIR/xdf_info || exit
xdf_info() {
    $CMD_DIR/xdf_info ${1:?xdf_info argument missing}
}

APPL_NAME=$( xdf_info name )
: ${APPL_NAME:?no value}

VERSION=$( xdf_info version )
: ${VERSION:?no value}

XDF_DIR=$( xdf_info optdir )
: ${XDF_DIR:?no value}

VAR_DIR=$( xdf_info vardir )
: ${VAR_DIR:?no value}

SRV_DIR=$XDF_DIR/srv
( cd $SRV_DIR ) || exit

# Uber jar
SRV_JAR=$SRV_DIR/xdf-rest-${VERSION}-all.jar
( <$SRV_JAR ) || exit

CONF_DIR=$XDF_DIR/conf
( cd $CONF_DIR ) || exit

LOG_DIR=$VAR_DIR/log
( cd $LOG_DIR ) || exit

# CONF_DIR for log4j.dtd, log4j.xml
CLPATH=$CONF_DIR:$SRV_JAR

$JAVA_CMD -Dlog.dir="$LOG_DIR" -Dxdf.core=server -cp $CLPATH sncr.xdf.rest.Server server $XDF_DIR/conf/xdf-rest.conf
# Possible TODO: replace arg 'manager' with main class 'sncr.xdf.rest.Manager'
