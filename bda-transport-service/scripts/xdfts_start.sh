#!/usr/bin/env bash
# Script to start local XDFTS service on given port

# Command line optional argument (http.port)
# If not specified, must be set in /etc/bda/xdfts.env file
declare XDFTS_PORT
# Option: <XDFTS_PORT>

source /etc/bda/xdfts.env || exit

declare -r THIS_FILE_PATH="$0"
declare -r THIS_FILE_NAME="${THIS_FILE_PATH##*/}"
declare -r THIS_FILE_DIR="$( /usr/bin/dirname $THIS_FILE_PATH )"
( cd ${THIS_FILE_DIR:?dirname error} ) || exit

# CONSTs
declare -r APP_MAINCLASS=play.core.server.ProdServerStart
XDFTS_HOST=$(/bin/hostname -s)
: ${XDFTS_HOST:?value no set}

## Debug helpers
VERBOSE=${VERBOSE:-} # to be used in [[$VERBOSE]] construct
# Option: -v

declare DRYRUN=''
# Option: -d

# Use foreground 'exec' to run java process
declare FG_EXECJ=
# Option: --fxj

###  ------------------------------- ###
###  Helper methods for BASH scripts ###
###  ------------------------------- ###

function usage () {
  echo "Usage: $THIS_FILE_NAME [-h] [-v] [-d] [<xdfts_port>]"
  cat <<'EEUUMM'
  -h          print this message
  -v          verbose output
  -d          print java command without running it
  --fxj       use Foreground eXec to start Java
  <xdfts_port> optional, XDFTS port number, if not provided
              using XDFTS_PORT from /etc/bda/xdfts.env
Environment vars:
  JAVA_HOME   optional, \$JAVA_HOME/bin/java to be executed, default: '/usr' ;
  JAVA_OPTS   optional java options, default: '' ;
  XDFTS_HOME   optional, default: <script dir>/.. ;
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
    -*) usage; exit 1 ;;
     *) XDFTS_PORT="$1"
        break ;;
    esac
    shift
  done
  : ${XDFTS_PORT:?value empty, must be set in /etc/bda/xdfts.env or provided as an argument}

}

function realpath ()
{
    perl -e 'use Cwd "realpath"; print realpath($ARGV[0])' ${1:?arg missing}
}

# Create PID file name based on XDFTS_PORT value
function mk_pidfile_name ()
{
    # Use XDFTS_PORT value
    echo xdfts.${XDFTS_PORT:?}.pid
}

###  ------------------------------- ###
###  Main script                     ###
###  ------------------------------- ###
process_args "$@"

vlog CMD: $THIS_FILE_PATH "$@"

# Check if HTTP service is running on the box
/usr/bin/curl -qs http://$XDFTS_HOST:$XDFTS_PORT/sr &>/dev/null && {
    die "XDFTS is already running on $XDFTS_HOST:$XDFTS_PORT"
}

declare -r xdfts_home="${XDFTS_HOME:-$(realpath "$(dirname $THIS_FILE_PATH)/..")}"
vlog xdfts_home: $xdfts_home
( cd $xdfts_home ) || exit

declare -r pidfile_path=${xdfts_home}/var/run/$(mk_pidfile_name)
vlog pidfile_path: $pidfile_path

# Check pid file exists and service process with stored pid is running
[[ -s $pidfile_path ]]     &&
pid=$( <$pidfile_path )    &&
[[ -d /proc/${pid:-000} ]] &&
die "XDFTS process ($pid) is still running on $XDFTS_HOST"

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

declare -r user_dir="${xdfts_home}"
vlog user_dir: $user_dir

declare -r conf_dir="${xdfts_home}/conf"
vlog conf_dir: $conf_dir
( cd $conf_dir ) || exit

declare -r lib_dir="${xdfts_home}/lib"
vlog lib_dir: $lib_dir
( cd $lib_dir ) || exit

declare -r log_dir="${xdfts_home}/var/log"
vlog log_dir: $log_dir
( cd $log_dir ) || exit

# Create ':' separated list of all files in $lib_dir
declare -r lib_classpath=$(
  /usr/bin/perl -e 'use Cwd "realpath";
    # list all files in directory separated by colon(':')
    print join(":", map { realpath($_) } glob($ARGV[0] . "/*"))' \
    $lib_dir
  )
declare -r app_classpath="$conf_dir:$lib_classpath"
vlog app_classpath: $app_classpath

declare -r java_args=$( echo \
    $java_opts \
    -Dhttp.port=$XDFTS_PORT \
    -Dpidfile.path=$pidfile_path \
    -Dlog.dir=${log_dir} \
    -Duser.dir=${user_dir}
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

/bin/rm -f $pidfile_path

# Additinal DEV key: X^kFEdvnivVbWVv5o^9wQylyz@h4G0vPjzpX@hDkkNWSom_^iOh^1ic>]@K94mSI
export APPLICATION_SECRET="y=5L3Lrezk1j0KsBo8K>YHR6JIxfcb=ax]0sT7m2NZHcafHZM73_=fqnNcGP8r<x"
if [[ $VERBOSE ]] ; then
  elog=$log_dir/$(date +%y%m%dT%H%M%S).xdfts-$XDFTS_PORT.elog
  vlog EXECLOG: $elog
else
  elog=/dev/null
fi
#
if [[ $FG_EXECJ ]] ; then
  eval $exec_cmd &>$elog </dev/null
  echo never gets here
fi

# Daemon mode
( eval $exec_cmd &>$elog </dev/null & )

echo XDFTS started on $XDFTS_HOST:$XDFTS_PORT
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