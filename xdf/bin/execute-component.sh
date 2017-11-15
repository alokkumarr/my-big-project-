#!/bin/bash
# Executable script for xdf version 2+
CMD_DIR=$( cd $(dirname $0); pwd -P )

DRYRUN=${DRYRUN:-}
XDF_DATA_ROOT=hdfs:///data/bda

## Mandatory
COMPONENT_NAME= # -m
BATCH_ID=       # -b
CONFIG_FILE=    # -c
APPLICATION_ID= # -processMap

## Can be Optional
LOG4J_CONF=                 # -j

function usage
{
    cat<<EEOOUU
----------------------------------------------------------------
usage: $0
    -a <Application ID>
    -b <Batch ID>
    -c <XDF Config. file>
    -m <Component name>
    -r <XDF datalake root>

    [ -j <Log4j config absolute filename> ]
    [ -n ] - print command, do Not execute (DRYRUN=1)
    -h - print this help and exit

 -a, -c, -b -m - mandatory parameters;
  Components:
     spark-sql  - XDF Spark SQL Component
     parser     - XDF Parser
     zero       - XDF Zero component



Example:
    $0 -m zero -a project1 -b BATCHID0001 -c app_conf.jcfg -r hdfs:///data/bda
----------------------------------------------------------------
EEOOUU
    exit ${1:-0}
}

errout(){
    echo 1>&2 "$@"
}
error(){
    errout ERROR: "$@"
}
warn(){
    errout WARNIG: "$@"
}

while getopts a:b:c:j:h:m:r:n opt
do
    case "$opt" in
    h)  usage;;
    n)  DRYRUN=1;;
    # mandatory common
    a)  APPLICATION_ID=$OPTARG;;
    b)  BATCH_ID=$OPTARG;;
    c)  CONFIG_FILE=$OPTARG;;
    m)  COMPONENT_NAME=$OPTARG;;
    # optional parameters
    r) XDF_DATA_ROOT=$OPTARG;;
    j) LOG4J_CONF=$OPTARG
        [[ $LOG4J_CONF = /* ]] || {
            error absolute file name required for -j, got "'$LOG4J_CONF'"
            exit 1
        }
        ;;

    \?) error unknown flag: -$opt
        usage 1;;
    esac
done
# Check common mandatory
: ${BATCH_ID:?ERROR: Batch ID (-b) is not specified}
: ${CONFIG_FILE:?ERROR: Config file (-c) name is not specified}
: ${APPLICATION_ID:?ERROR: Application ID (-a) is not specified}
: ${COMPONENT_NAME:?ERROR: Component (-m) is not specified}
#####

## Calculate XDF_DIR, LIB_DIR, VERSION
# Check xdf_info executable
$CMD_DIR/xdf_info || exit
xdf_info() {
    $CMD_DIR/xdf_info ${1:?xdf_info argument missing}
}

XDF_DIR=$( xdf_info optdir )
: ${XDF_DIR:?no value}
LIB_DIR=$XDF_DIR/lib
( cd $LIB_DIR ) || exit

VERSION=$( xdf_info version )
: ${VERSION:?no value}

cat<<EEOOTT
XDF COMPONENT SHELL SCRIPT EXECUTING AT $(date +"%m-%d-%Y %r")
----------------------------------------------------------------
Command                 : $0 $@
-------
XDF home                : $XDF_DIR
XDF version             : $VERSION
-------
Component               : $COMPONENT_NAME
Batch ID                : $BATCH_ID
Application             : $APPLICATION_ID
Configuration file      : $CONFIG_FILE
-------
EEOOTT

#######################################
CMD=
PARAM_LIST=( -b $BATCH_ID -c $CONFIG_FILE -a $APPLICATION_ID )
case "$COMPONENT_NAME" in
    sql)
        COMPONENT_JAR=${LIB_DIR}/xdf-sql-${VERSION}.jar
        MAIN_CLASS=sncr.xdf.sql.SQLComponent
        ;;
 
    zero)
        COMPONENT_JAR=${LIB_DIR}/xdf-component-${VERSION}.jar
        MAIN_CLASS="sncr.xdf.component.ZeroComponent"
        ;;

    parser)
        COMPONENT_JAR=${LIB_DIR}/xdf-component-${VERSION}.jar
        MAIN_CLASS="sncr.xdf.parser"
        ;;

    *)  echo "ERROR: Unknown XDF component: $COMPONENT_NAME"
        exit 1
        ;;
esac

export SPARK_HOME=/opt/mapr/spark/spark-current
( cd $SPARK_HOME ) || exit

[[ $CMD ]] || {
    : ${COMPONENT_JAR:?no value}
    : ${MAIN_CLASS:?no value}
    ( <$COMPONENT_JAR ) || exit

    : ${LOG4J_CONF:=$XDF_DIR/conf/log4j.properties}
    cat <<EELL250
-------
Log4j log file          : ${LOG4J_CONF}
EELL250
    ( <$LOG4J_CONF ) || exit

    # Build CSV value
    JARS=$( echo $LIB_DIR/*.jar | tr ' ' , )
    CONF_OPT="spark.driver.extraJavaOptions=-Dlog4j.configuration=file:$LOG4J_CONF"
    # TODO: use array
    # CONF_OPTS=( --conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:$LOG4J_CONF" )
    # TODO: conditionally add -Dxdf.json.subs.params=true option
    # CONF_OPTS+=( --conf spark.driver.extraJavaOptions=-Dxdf.json.subs.params=true )
    # use: "${CONF_OPTS[@]}""
    CMD=(
        $SPARK_HOME/bin/spark-submit
        --verbose
        --driver-java-options
        -Djava.security.auth.login.config=/opt/mapr/conf/mapr.login.conf
        --conf $CONF_OPT
        --class $MAIN_CLASS
        --jars $JARS
        $COMPONENT_JAR
        ${PARAM_LIST[@]}
    )
}
echo "\
----------------------------------------------------------------
"
#######################################

( <$SPARK_HOME/conf/spark-env.sh ) || exit
source $SPARK_HOME/conf/spark-env.sh

export XDF_DATA_ROOT

echo "Run XDF Component $COMPONENT_NAME, command:
${CMD[@]}"

[[ $DRYRUN ]] && {
    echo DRYRUN exit
    exit
}

${CMD[@]}
RETVAL=$?

cat<<EEOORR
----------------------------------------------------------------
RETURN CODE: $RETVAL ($COMPONENT_NAME)
----------------------------------------------------------------
EEOORR
exit $RETVAL
