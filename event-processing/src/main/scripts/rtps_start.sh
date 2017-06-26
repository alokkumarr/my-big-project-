#!/bin/bash

CMD_DIR=$( cd $(dirname $0); pwd )
RTPS_HOME=$( cd $CMD_DIR/.. ; pwd )
( cd $RTPS_HOME/lib ) || exit

# RTPS script to be executed on mapr box to start application instance run

VERBOSE=${VERBOSE:-}

# Check SPARK executable is in place
SPARK_SUBMIT_XPATH=/opt/mapr/spark/spark-current/bin/spark-submit
[[ -x $SPARK_SUBMIT_XPATH ]] || {
    echo 1>&2 error: not an executable: $SPARK_SUBMIT_XPATH
    exit 1
}

###############
# Application instance ID
APPL_INST=${1:-}
: ${APPL_INST:?argument missing}

[[ $APPL_INST = *.* ]] || {
    echo 1>&2 "invalid APPL_INST: '$APPL_INST', must be <APPL_NAME>.<APPL_INUM>"
    exit 1
}

# Application name
APPL_NAME=${APPL_INST%.[1-9]}
: ${APPL_NAME:?part of argument missing}

# Application instance number
APPL_INUM=${APPL_INST##*.}
: ${APPL_INUM:?part of argument missing}
################

# Get Real Time Processing System home dir
#RTPS_ENV=/etc/bda/rtps.env
#source $RTPS_ENV || {
#    echo 1>&2 "error in 'source $RTPS_ENV'"
#    exit 1
#}
#: ${RTPS_HOME?:not set in $RTPS_ENV}

# Get Application home dir
APPL_ENV=/etc/bda/$APPL_NAME.env
source $APPL_ENV || exit
: ${APPL_HOME?:not set in $APPL_ENV}

# Get first name of jar file
JAR=$( set -- $RTPS_HOME/lib/bda-rt-event-processing-*.jar ; echo $1 )

# log4j options
CONF_OPT="spark.driver.extraJavaOptions=-Dlog4j.configuration=file:$RTPS_HOME/conf/log4j.properties"

# Application log
log=/var/bda/$APPL_NAME/log/$APPL_INST.log
rotate_log ()
{
    log=$1;
    let num=${2:-5};
    if [ -f "$log" ]; then # rotate logs
        while [ $num -gt 1 ]; do
            let prev=num-1
            [ -f "$log.$prev" ] && mv "$log.$prev" "$log.$num"
            num=$prev
        done
        mv "$log" "$log.$num";
    fi
}
rotate_log "$log"
[[ $VERBOSE ]] && eval "echo LOG files: ; /bin/ls -s $log*"

# -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -Dcom.sun.management.jmxremote

CONF=$APPL_HOME/conf/$APPL_INST.conf
# Validate checkpoint.path parent directory exists
( cd /var/bda/rtps/cpt ) || exit

MAIN_CLASS=synchronoss.spark.drivers.rt.EventProcessingApplicationDriver

VERBOSE_OPT=
if [[ $VERBOSE ]] ; then
    VERBOSE_OPT='--verbose'
    echo "=== ENV BEGIN"
    /bin/env
    echo "=== ENV END"
fi

(
    [[ $VERBOSE ]] && set -vx
    #################################
    $SPARK_SUBMIT_XPATH </dev/null  \
        $VERBOSE_OPT                \
        --jars $RTPS_HOME/lib/*     \
        --conf $CONF_OPT            \
        --class $MAIN_CLASS         \
        $JAR $CONF &>"$log"         &
    #################################
)
echo "$(date +%FT%T%z) rtps_start '$APPL_INST' sent 'spark-submit $MAIN_CLASS'" 
echo "log: '$log'"
exit 0
