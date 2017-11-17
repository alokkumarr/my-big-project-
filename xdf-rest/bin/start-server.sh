#!/bin/bash
CMD_NAME=$( basename $0 )
CMD_DIR=$( cd $(dirname $0) && pwd -P )
: ${CMD_DIR:?no value}
source $CMD_DIR/app_env || exit

JAVA_CMD=$( command -v java )
: ${JAVA_CMD:?no value}

( <$CONF_DIR/xdf-rest.conf )

# CONF_DIR for log4j.dtd, log4j.xml
CLPATH=$CONF_DIR:$APP_JAR

# Start log
SLOG_FILE=$LOG_DIR/${CMD_NAME%.*}.$(date +%y%m%d-%H%M%S).log

(
#$JAVA_CMD -Dlog.dir="$LOG_DIR" -Dxdf.core=server -cp $CLPATH sncr.xdf.rest.Server server $XDF_DIR/conf/xdf-rest.conf
# Possible TODO: replace arg 'manager' with main class 'sncr.xdf.rest.Manager'
(
exec >& $SLOG_FILE
exec </dev/null
echo COMMAND: $0 "$@"
echo PID: $$
set -x
exec $JAVA_CMD \
  -Dlog.dir="$LOG_DIR" \
  -Dxdf.core=server \
  -cp $CLPATH \
  sncr.xdf.rest.Server \
  server $CONF_DIR/xdf-rest.conf
) &
echo "start  log: $SLOG_FILE"
)
