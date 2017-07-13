#!/bin/bash
# RTPS script to be executed on mapr box to start application run

CMD_DIR=$( cd $(dirname $0); pwd )
CMD_NAM=$( basename $0 )

# execute_rtps.sh <APPL_CONF> [<log4j_conf>]
usage() {
    echo Usage: $CMD_NAM <APPL_CONF> [<log4j_conf>]
    exit ${1:-0}
}
[[ $# = 0 || $1 = -[hH] ]] && usage

# Check Application configuration file
APPL_CONF=${1:?required argument missing}
( $<APPL_CONF ) || exit

L4J_CONF=${2:-}

##
VERBOSE=${VERBOSE:-}
#
RTPS_HOME=$( cd $CMD_DIR/.. ; pwd )
( cd $RTPS_HOME/lib ) || exit

# Check LOG4J configuration file
: ${L4J_CONF:=$RTPS_HOME/conf/log4j.properties}
( <$L4J_CONF ) || exit

# Check SPARK executable is in place
SPARK_SUBMIT_XPATH=/opt/mapr/spark/spark-current/bin/spark-submit
[[ -x $SPARK_SUBMIT_XPATH ]] || {
    echo 1>&2 error: not an executable: $SPARK_SUBMIT_XPATH
    exit 1
}

# Get first name of jar file
jars=( $RTPS_HOME/lib/rtps-*.jar )
JAR="@{jars[0]}"
( <"$JAR" ) || exit

# log4j options
CONF_OPTS=(
    --conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:$L4J_CONF"
    )

# -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -Dcom.sun.management.jmxremote

MAIN_CLASS=synchronoss.spark.drivers.rt.EventProcessingApplicationDriver

VERBOSE_OPT=
if [[ $VERBOSE ]] ; then
    VERBOSE_OPT='--verbose'
    echo "=== ENV BEGIN"
    /bin/env
    echo "=== ENV END"
fi

echo "$(date +%FT%T%z) sent 'spark-submit $MAIN_CLASS'"
(
    [[ $VERBOSE ]] && set -vx
    #################################
    $SPARK_SUBMIT_XPATH </dev/null  \
        $VERBOSE_OPT                \
        ${CONF_OPTS[@]}             \
        --class $MAIN_CLASS         \
        $JAR $APPL_CONF             &
    #################################
)
exit 0
