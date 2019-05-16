#!/usr/bin/env bash
# Script to start local SAW_SERVICE service on given port

# Command line optional argument (http.port)
# If not specified, must be set in /etc/saw/service.env file
declare SAW_SERVICE_PORT
# Option: <SAW_SERVICE_PORT>

source /etc/saw/service.env || exit

declare -r THIS_FILE_PATH="$0"
declare -r THIS_FILE_NAME="${THIS_FILE_PATH##*/}"
declare -r THIS_FILE_DIR="$( /usr/bin/dirname $THIS_FILE_PATH )"
( cd ${THIS_FILE_DIR:?dirname error} ) || exit

# CONSTs
declare -r APP_MAINCLASS=play.core.server.ProdServerStart
SAW_SERVICE_HOST=$(/bin/hostname -s)
: ${SAW_SERVICE_HOST:?value no set}

## Debug helpers
VERBOSE=${VERBOSE:-} # to be used in [[$VERBOSE]] construct
# Option: -v

declare DRYRUN=''
# Option: -d

# Use foreground 'exec' to run java process
declare FG_EXECJ=
# Option: --fxj


# Use foreground 'exec' to run java process, variable will be used for play framework
# based netty library to load at run time
declare FG_EXECJN=
# Option: --fxjnetty

###  ------------------------------- ###
###  Helper methods for BASH scripts ###
###  ------------------------------- ###

function usage () {
  echo "Usage: $THIS_FILE_NAME [-h] [-v] [-d] [<saw-service-port>]"
  cat <<'EEUUMM'
  -h          print this message
  -v          verbose output
  -d          print java command without running it
  --fxj       use Foreground eXec to start Java
  --fxjnetty  use run the services with compatible netty version
  <saw_service_port> optional, SAW_SERVICE port number, if not provided
              using SAW_SERVICE_PORT from /etc/saw/service.env
Environment vars:
  JAVA_HOME   optional, \$JAVA_HOME/bin/java to be executed, default: '/usr' ;
  JAVA_OPTS   optional java options, default: '' ;
  SAW_SERVICE_HOME   optional, default: <script dir>/.. ;
EEUUMM
}

function echoerr () { echo 1>&2 "$@" ; }
function die () { echoerr "$@" ; exit 1 ; }
function vlog () { [[ $VERBOSE ]] && echoerr "$@" ; }

# Processes incoming arguments and places them in appropriate global variables.
function process_args ()
{
  while (( $# > 0 )); do
    case "$1" in
    -h) usage; exit 0 ;;
    -v) VERBOSE=1     ;;
    -d) DRYRUN=1      ;;
    --fxj) FG_EXECJ=1    ;;
    --fxjnetty) FG_EXECJN=1 ;;
    -*) usage; exit 1 ;;
     *) SAW_SERVICE_PORT="$1"
        break ;;
    esac
    shift
  done
  : ${SAW_SERVICE_PORT:?value empty, must be set in /etc/saw/service.env or provided as an argument}

}

function realpath ()
{
    perl -e 'use Cwd "realpath"; print realpath($ARGV[0])' ${1:?arg missing}
}

# Create PID file name based on SAW_SERVICE_PORT value
function mk_pidfile_name ()
{
    # Use SAW_SERVICE_PORT value
    echo saw.${SAW_SERVICE_PORT:?}.pid
}

###  ------------------------------- ###
###  Main script                     ###
###  ------------------------------- ###
process_args "$@"

vlog CMD: $THIS_FILE_PATH "$@"

declare -r saw_service_home="${SAW_SERVICE_HOME:-$(realpath "$(dirname $THIS_FILE_PATH)/..")}"
vlog xdfts_home: $saw_service_home
( cd $saw_service_home ) || exit

declare -r pidfile_path=/dev/null
vlog pidfile_path: $pidfile_path

# Validate Java
declare java_cmd="${JAVA_HOME:-/usr}/bin/java"
[[ -x $java_cmd ]] ||
  java_cmd=$( /usr/bin/which java ) ||
  die "java executable not found"
vlog java_cmd: $java_cmd

# Now we check to see if there are any java opts on the environment.
# These get listed first, with the script able to override them.
declare java_opts=''
if [[ -n "${JAVA_OPTS:-}" ]]; then
    java_opts="${JAVA_OPTS}"
    vlog java_opts: $java_opts
fi

declare -r user_dir="${saw_service_home}"
vlog user_dir: $user_dir

declare -r conf_dir="${saw_service_home}/conf"
vlog conf_dir: $conf_dir
( cd $conf_dir ) || exit

declare -r lib_dir="${saw_service_home}/lib"
vlog lib_dir: $lib_dir
( cd $lib_dir ) || exit

# Create ':' separated list of all files in $lib_dir
declare lib_classpath=$(
  /usr/bin/perl -e 'use Cwd "realpath";
    # list all files in directory separated by colon(':')
    print join(":", map { realpath($_) } glob($ARGV[0] . "/*"))' \
    $lib_dir
  )

# Workaround : Load the netty library at runtime for saw-transport-service
# required play version. Added as part of spark 2.3.2 version upgrade , since
# latest spark netty lib are not compatible with used play framework.
if [[ $FG_EXECJN ]] ; then
 for j in `ls ${saw_service_home}/netty-lib/*.jar`; do
 lib_classpath=${lib_classpath}:"${j}"
 done
fi

for j in `ls /opt/mapr/spark/spark-2.3.2/jars/*.jar`; do
 lib_classpath=${lib_classpath}:"${j}"
done
lib_classpath=${lib_classpath}:$(mapr classpath):$(hadoop classpath):$(hbase classpath)


declare -r app_classpath="$conf_dir:$lib_classpath"



vlog app_classpath: $app_classpath

declare -r java_args=$( echo \
    $java_opts \
    -Dhttp.port=$SAW_SERVICE_PORT \
    -Dpidfile.path=$pidfile_path \
    -Duser.dir=${user_dir} \
    -Daggr.es.size=1000 \
    -Durl=http://localhost:9800/ \
    -Djava.library.path=/opt/mapr/lib \
    -Dschema.pivot=/opt/saw/service/schema/pivot_querybuilder_schema.json \
    -Dschema.chart=/opt/saw/service/schema/chart_querybuilder_schema.json \
    -Dschema.report=/opt/saw/service/schema/report_querybuilder_schema.json \
    -Dhadoop.home.dir=/opt/mapr/hadoop/hadoop-2.7.0
    )
vlog java_args: $java_args

vlog APP_MAINCLASS: $APP_MAINCLASS

export CLASSPATH="$app_classpath"
vlog "CLASSPATH=$CLASSPATH"

# Actually runs the script.
declare -r exec_cmd="exec $java_cmd $java_args $APP_MAINCLASS"
vlog "EXEC_CMD: $exec_cmd"

[[ $DRYRUN ]] && {
  echo "# DRY RUN END"
  exit 0
}

#/bin/rm -f $pidfile_path

# Additinal DEV key: X^kFEdvnivVbWVv5o^9wQylyz@h4G0vPjzpX@hDkkNWSom_^iOh^1ic>]@K94mSI
export APPLICATION_SECRET="y=5L3Lrezk1j0KsBo8K>YHR6JIxfcb=ax]0sT7m2NZHcafHZM73_=fqnNcGP8r<x"
export SAW_EXECUTOR_HOME=${saw_service_home}

elog=/dev/null
#
if [[ $FG_EXECJ ]] || [[ $FG_EXECJN ]] ; then
  $exec_cmd
  echo never gets here
fi

# Daemon mode
( eval $exec_cmd &>$elog </dev/null & )

echo SAW_SERVICE started on $SAW_SERVICE_HOST:$SAW_SERVICE_PORT
exit

## https://www.playframework.com/documentation/2.5.x/ProductionConfiguration

## Changing the path of RUNNING_PID (PID file)
# -Dpidfile.path=/var/run/play.pid

## Config file
# -Dconfig.file=/opt/conf/prod.conf

## Specifying the HTTP server address and port using system properties
# -Dhttp.port=1234
# -Dhttp.address=127.0.0.1

## To play with other system params:
# echo "include \"$conf_dir/application.conf\"" >$CFG_FNM
# $ JAVA_OPTS=-Dconfig.file=$CFG_FNM this-script-command

## To check service:
# curl -qsS http://localhost:9999/sr
#
