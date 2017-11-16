#!/bin/bash
echo "Starting xdf-rest service JVM"

CMD_DIR=$( cd $(dirname $0); pwd -P ) || exit
source $CMD_DIR/app_env || exit

LOG4J_CONF=$CONF_DIR/log4j.xml

COMP_NAME=$2
: ${COMP_NAME:?no value}

COMP_LOG_DIR=$LOG_DIR/$COMP_NAME

mkdir -p $COMP_LOG_DIR || exit

CONF_OPT="spark.driver.extraJavaOptions=-Dlog.dir=$LOG_DIR -Dxdf.core=$3 -Dlog4j.configuration=file:$LOG4J_CONF -Dcomp.log.dir=$COMP_LOG_DIR"

export XDF_DATA_ROOT=hdfs:///data/bda
hadoop fs -stat $XDF_DATA_ROOT >/dev/null || exit

/opt/mapr/spark/spark-current/bin/spark-submit \
    --conf "$CONF_OPT" \
    --class sncr.xdf.rest.Server \
    $APP_JAR \
    task $XDF_DIR/conf/xdf-rest.conf "${1:?no value}"
