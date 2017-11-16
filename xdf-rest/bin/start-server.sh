#!/bin/bash

CMD_DIR=$( cd $(dirname $0); pwd -P ) || exit
source $CMD_DIR/app_env || exit

JAVA_CMD=$( command -v java )
: ${JAVA_CMD:?no value}

# CONF_DIR for log4j.dtd, log4j.xml
CLPATH=$CONF_DIR:$APP_JAR

(
#$JAVA_CMD -Dlog.dir="$LOG_DIR" -Dxdf.core=server -cp $CLPATH sncr.xdf.rest.Server server $XDF_DIR/conf/xdf-rest.conf
# Possible TODO: replace arg 'manager' with main class 'sncr.xdf.rest.Manager'
$JAVA_CMD </dev/null >& $LOG_DIR/server-start.$$ \
  -Dlog.dir="$LOG_DIR" \
  -Dxdf.core=server \
  -cp $CLPATH \
  sncr.xdf.rest.Server \
  server $CONF_DIR/xdf-rest.conf &
PID=$!
echo $PID>$VAR_DIR/server.pid
ps -fp $PID
echo server pid : $PID
)
