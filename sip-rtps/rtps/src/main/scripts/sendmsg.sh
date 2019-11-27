#!/bin/bash

CMD_DIR=$( cd $(dirname $0); pwd )
RTPS_HOME=$( cd $CMD_DIR/.. ; pwd )
( cd $RTPS_HOME/lib ) || exit

MAPR_HOME=/opt/mapr
isSecure=$(head -1 ${MAPR_HOME}/conf/mapr-clusters.conf | grep -o 'secure=\w*' | cut -d= -f2)
 if [ "$isSecure" = "true" ] && [ -f "${MAPR_HOME}/conf/mapruserticket" ]; then
        export MAPR_TICKETFILE_LOCATION="${MAPR_HOME}/conf/mapruserticket"
 fi
# Check SPARK executable is in place
SPARK_SUBMIT_XPATH=/opt/mapr/spark/spark-current/bin/spark-submit
[[ -x $SPARK_SUBMIT_XPATH ]] || {
    echo 1>&2 error: not an executable: $SPARK_SUBMIT_XPATH
    exit 1
}

# Parameters
TOPIC=$1        # MapR stream and topic maprfs://path/to/stream:topic
NUMMSG=$2       # Number of messages to send
MSG=$3          # Message text

# Get first name of jar file
JAR=$( set -- $RTPS_HOME/lib/sip-rtps.jar ; echo $1 )

MAIN_CLASS=synchronoss.spark.drivers.rt.SimpleProducer

(
    #################################
    $SPARK_SUBMIT_XPATH </dev/null  \
        --class $MAIN_CLASS         \
        $JAR $TOPIC $NUMMSG $MSG    || exit
    #################################
)
