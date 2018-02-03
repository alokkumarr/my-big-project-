#!/bin/bash
# Executable script for xdf version 2+
DRYRUN=${DRYRUN:-}
#VERBOSE=${VERBOSE:-}

CMD_DIR=$( cd $(dirname $0) && pwd -P )
: ${CMD_DIR:?no value}
source $CMD_DIR/app_env || exit

COMPONENT_JAR=$APP_JAR

# Validate spark
SPARK_HOME=/opt/mapr/spark/spark-current
( cd $SPARK_HOME ) || exit
( <$SPARK_HOME/bin/spark-submit ) || exit
#( <$SPARK_HOME/conf/spark-env.sh ) || exit

# Component -> main class table
declare -A COMP_MC=(
    [sql]=sncr.xdf.sql.SQLComponent
    [spark-sql]=sncr.xdf.sql.SQLComponent
    [zero]=sncr.xdf.component.ZeroComponent
    [parser]=sncr.xdf.parser.Parser
	[transformer]=sncr.xdf.transformer.TransformerComponent
    )

function usage()
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
  Components: ${!COMP_MC[*]}
     spark-sql  - XDF Spark SQL Component
     parser     - XDF Parser
     zero       - XDF Zero component

Example:
    $0 -m zero -a project1 -b BATCHID0001 -c app_conf.jcfg -r hdfs:///data/bda
----------------------------------------------------------------
EEOOUU
    exit ${1:-0}
}

## Mandatory
APPLICATION_ID= # -a
BATCH_ID=       # -b
CONFIG_FILE=    # -c
COMPONENT_NAME= # -m

## Can be Optional
LOG4J_CONF=     # -j
XDF_DATA_ROOT=hdfs:///data/bda # -r

## CLI Args
while getopts a:b:c:j:m:r:nh opt
do
    case "$opt" in
    # mandatory common
    a)  APPLICATION_ID=$OPTARG;;
    b)  BATCH_ID=$OPTARG;;
    c)  CONFIG_FILE=$OPTARG;;
    m)  COMPONENT_NAME=$OPTARG;;
    # optional parameters
    r)  XDF_DATA_ROOT=$OPTARG;;
    j)  LOG4J_CONF=$OPTARG
        [[ $LOG4J_CONF = /* ]] || {
           error absolute file name required for -j, got "'$LOG4J_CONF'"
           exit 1
        }
        ;;
    n)  DRYRUN=1;;
    h)  usage;;

    \?) usage 1;;
    esac
done
#(( OPTIND > 1)) && shift $(( OPTIND - 1 ))

# Check common mandatory
: ${APPLICATION_ID:?ERROR: Application ID (-a) is not specified}
: ${BATCH_ID:?ERROR: Batch ID (-b) is not specified}
: ${CONFIG_FILE:?ERROR: Config file (-c) name is not specified}
: ${COMPONENT_NAME:?ERROR: Component (-m) is not specified}
#####

# validate COMPONENT_NAME
MAIN_CLASS=${COMP_MC[$COMPONENT_NAME]}
[[ $MAIN_CLASS ]] || {
    error "Unknown XDF component (-m): $COMPONENT_NAME"
    usage 1
}

#SR?? Validate config file
#?? file://
#( <${CONFIG_FILE} ) || exit

# validate LOG4J_CONF
: ${LOG4J_CONF:=$XDF_DIR/conf/log4j.properties}
( <$LOG4J_CONF ) || exit

# validate XDF_DATA_ROOT
hadoop fs -stat $XDF_DATA_ROOT >/dev/null || exit 1

# 'export SPARK_HOME' inside spark-env.sh
##?? not needed
#source $SPARK_HOME/conf/spark-env.sh

cat<<EEOOTT
XDF COMPONENT SHELL SCRIPT EXECUTING AT $(date --rfc-3339=sec)
----------------------------------------------------------------
Command                 : $0 $@
-------
XDF home                : $XDF_DIR
XDF version             : $VERSION
-------
Component               : $COMPONENT_NAME ($MAIN_CLASS)
Batch ID                : $BATCH_ID
Application             : $APPLICATION_ID
Configuration file      : $CONFIG_FILE
-------
Log4j log file          : ${LOG4J_CONF}
----------------------------------------------------------------
EEOOTT

#######################################
JAVA_PROPS=(
  -Dlog4j.configuration=file:$LOG4J_CONF #SR?? file://
  -DXDF_DATA_ROOT=$XDF_DATA_ROOT
  -Djava.security.auth.login.config=/opt/mapr/conf/mapr.login.conf
  )
# TODO: conditionally add -Dxdf.json.subs.params=true option
# JAVA_PROPS+=( -Dxdf.json.subs.params=true )
# use: --conf "spark.driver.extraJavaOptions=${CONF_OPTS[*]}"

CMD=(
    $SPARK_HOME/bin/spark-submit
    --verbose
#    --driver-java-options "${JAVA_PROPS[@]}"
    --conf "spark.driver.extraJavaOptions=${JAVA_PROPS[*]}"
    --class $MAIN_CLASS
    $COMPONENT_JAR
    # main args
    -a $APPLICATION_ID
    -b $BATCH_ID
    -c $CONFIG_FILE
    )

#####################################

echo "Run XDF Component $COMPONENT_NAME, command:
${CMD[@]}"

[[ ${DRYRUN:-0} != 0 ]] && {
    echo CMD WORDS: ${#CMD[@]}
    for a in "${CMD[@]}"; do echo -- "$a"; done
    echo DRYRUN exit
#test: DRYRUN=1 ./execute-component.sh -m zero -a p1 -b 01 -c c.cfg
    exit
}

export XDF_DATA_ROOT
"${CMD[@]}"
RETVAL=$?

### These CMD work if I run directly from terminal ( not through script )
#XDF_HOME=/dfs/opt/bda/xdf-ngsr-current /opt/mapr/spark/spark-current/bin/spark-submit --verbose --driver-java-options '-Djava.security.auth.login.config=/opt/mapr/conf/mapr.login.conf -DXDF_DATA_ROOT=hdfs:///data/bda -Dlog4j.configuration=file:/dfs/opt/bda/xdf-ngsr/xdf-ngsr-1.0.0_dev/conf/log4j.properties' --class sncr.xdf.sql.SQLComponent --jars /dfs/opt/bda/xdf-ngsr/xdf-ngsr-1.0.0_dev/lib/xdf-rest-1.0.0_dev-all.jar /dfs/opt/bda/xdf-ngsr/xdf-ngsr-1.0.0_dev/lib/xdf-rest-1.0.0_dev-all.jar -b SQLTEST1 -c file:///dfs/opt/bda/apps/xda-ux-sr-comp-dev-1.0.0_dev/conf/sqlComponentWithMeta.jconf -a xda-ux-sr-comp-dev
#XDF_HOME=/dfs/opt/bda/xdf-ngsr-current /opt/mapr/spark/spark-current/bin/spark-submit --verbose --driver-java-options '-Djava.security.auth.login.config=/opt/mapr/conf/mapr.login.conf -DXDF_DATA_ROOT=hdfs:///data/bda -Dlog4j.configuration=file:/dfs/opt/bda/xdf-ngsr/xdf-ngsr-1.0.0_dev/conf/log4j.properties' --class sncr.xdf.component.ZeroComponent --jars /dfs/opt/bda/xdf-ngsr/xdf-ngsr-1.0.0_dev/lib/xdf-rest-1.0.0_dev-all.jar /dfs/opt/bda/xdf-ngsr/xdf-ngsr-1.0.0_dev/lib/xdf-rest-1.0.0_dev-all.jar -b TEST2 -c file:///dfs/opt/bda/apps/xda-ux-sr-comp-dev-1.0.0_dev/conf/zeroComponent.jconf -a xda-ux-sr-comp-dev

cat<<EEOORR
----------------------------------------------------------------
RETURN CODE: $RETVAL ($COMPONENT_NAME)
----------------------------------------------------------------
EEOORR
exit $RETVAL
