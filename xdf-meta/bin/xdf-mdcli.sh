#!/bin/bash
# Executable script for xdf version 2+
set -x

CMD_DIR=$( cd $(dirname $0); pwd )

HADOOP_HOME=/opt/mapr/hadoop-$(cat /opt/mapr/hadoop/hadoopversion)

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

LIB_DIR=$XDF_DIR/lib
( cd $LIB_DIR ) || exit

VAR_DIR=$( xdf_info vardir )
: ${VAR_DIR:?no value}

CONF_DIR=$XDF_DIR/conf
( cd $CONF_DIR ) || exit

LOG_DIR=$VAR_DIR/log/meta
( cd $LOG_DIR ) || mkdir -p $LOG_DIR

export XDF_DATA_ROOT=hdfs:///data/bda

JARS=$( echo $LIB_DIR/*.jar | tr ' ' : )
# CONF_DIR for log4j.dtd, log4j.xml
CLPATH=$CONF_DIR:$JARS:$(/opt/mapr/bin/mapr classpath)

$JAVA_CMD -cp $CLPATH -Dcomp.log.dir="$LOG_DIR" -Dxdf.core=meta sncr.xdf.cli.CommandExecutor "$@"

