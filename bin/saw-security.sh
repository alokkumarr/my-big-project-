#!/bin/bash
###
# saw-security operations script
# implements start/stop/status
# commands to operate saw-security microservice
###
CMD_ARG="$0"

###
# Print usage
function usage()
{
    echo "
Usage:
    $CMD_ARG [--verbose] [ status:DFLT / start [--fg] / stop ]
Env vars:
    VERBOSE=1
    DRYRUN=1
"
    exit ${1:-0}
}
# Check -h / --h
[[ ${1:-x} =~ --?[hH] ]] && usage

###
# Script directory
CMD_DIR=$( cd $( dirname $CMD_ARG ); pwd )
# Script file name
CMD=$( basename $CMD_ARG )

###
# Check appl_info is available
$CMD_DIR/appl_info || exit

###
# Get Configurable
appl_info() {
    $CMD_DIR/appl_info ${1:?arg missing}
}
###
# Get configurables
APPL_NAME=$( appl_info name )
: ${APPL_NAME:?no value}
#
#optdir=$( appl_info optdir )
#: ${optdir:?no value}
#( cd $optdir ) || exit
#
vardir=$( appl_info vardir )
: ${vardir:?no value}

###
# Set variables
LOG_DIR=$vardir/log
( cd $LOGDIR ) || exit
LOG_FILE=$LOG_DIR/$APPL_NAME.log
#
PID_DIR=$vardir/run
( cd $PID_DIR ) || exit
PID_FILE=$PID_DIR/$APPL_NAME.pid

###
# To start verbose printing to verbose log file:
VERBOSE=${VERBOSE:-}
if [[ ${1:-x} = --verbose ]] ; then
    # 1. use --verbose arg
    shift
    VERBOSE=1
elif [[ $VERBOSE ]] ; then
    # 2. set VERBOSE=1 in environment..
    # Strip user content
    VERBOSE=1
else
    # 3. or create XXX.verbose file..
    [[ -f $CMD_ARG.verbose ]] && {
        echo file detected: $CMD_ARG.verbose, set VERBOSE=1
        VERBOSE=1
    }
fi

###
# Date-time stamp
# ex: 2017-04-13T18:26:57-0400
dts() { date +%FT%T%z; }
###
# Print error message and exit
function error {
    msg="error: $@"
    echo 1>&2 "$msg"
    vlog "$msg"
    vlog "exit 1"
    exit 1
}
function warning {
    msg="warning: $@"
    echo 1>&2 "$msg"
    vlog "$msg"
}
###
# Verbose print
VLOG_FNM=/dev/null
if [[ $VERBOSE ]] ; then
    VLOG_FNM=$vardir/log/${CMD%.sh}.vlog
    echo "see VERBOSE log file: '$VLOG_FNM'"

    function vlog { echo "$@" >>$VLOG_FNM ; }

    # Start output in verbose log file
    vlog ''
    vlog "$(dts) === STARTED: $0 $@"
else
    function vlog { :; }
fi
###
# Log to console and vlog
function log {
    echo "$@"
    vlog "$@"
}

#######################################
# Check if process with pid listed in $PID_FILE is running,
# set APPL_PID if so
function appIsUp
{
    APPL_PID=
    [[ -f $PID_FILE ]] || {
        vlog "pid file not found: '$PID_FILE'"
        return 1
    }
    read APPL_PID < $PID_FILE
    [[ $APPL_PID ]] || {
        error "no PID found in pid file '$PID_FILE'"
        #return 1
    }
    vlog APPL_PID: $APPL_PID
    # Check process in memory
    [[ -d /proc/$APPL_PID ]] || {
        vlog "process directory not found: '/proc/$APPL_PID'"
        return 1
    }
    # Process is in memory
    vlog "process directory exists: '/proc/$APPL_PID'"
    return 0
}

# start
declare FG_EXECJ=
DRYRUN=${DRYRUN:-}
dry_exit() {
    rc=${1:-1}
    log exit on previous error
    [[ $DRYRUN ]] || exit $rc
}
let WAIT_SECS=${WAIT_SECS:-0}
appl_start() {
    appIsUp && {
        log "OK: '$APPL_NAME' running already, PID: $APPL_PID"
        exit 0
    }
    ### Start Application Instance
    log starting "'$APPL_NAME'"
    #
    optdir=$(appl_info optdir)
    libdir=$optdir/lib
    ( cd $libdir ) || dry_exit

    confdir=$optdir/conf
    ( cd $confdir ) || dry_exit

    ( <$confdir/application.properties ) || dry_exit

    war_fnm=( $libdir/*saw-security*.war )
    # use first file only
    war_file=${war_fnm[0]}
    ( <${war_file} ) || dry_exit

    java_args=(
        -Xms32M -Xmx2048M
        -Djava.net.preferIPv4Stack=true
        -Dspring.config.location=$confdir/application.properties
        -Dlogging.config=$confdir/logback.xml
        -Dsaw.log.file=$LOG_DIR/saw-security
        -Dquartz.properties.location=$confdir
        -jar $war_file
        -name saw-security
        )
    exec_cmd="java ${java_args[@]}"
    [[ $DRYRUN ]] && {
        log CMD: "$exec_cmd"
        log DRYRUN exit
        exit 0
    }
    vlog CMD: "$exec_cmd"
    START_LOG=$LOG_DIR/start-$APPL_NAME.log
    vlog START_LOG: $START_LOG
    /bin/rm -f $PID_FILE

    # Run in foreground
    if [[ $FG_EXECJ ]] ; then
        vlog 'run in foreground'
        exec $exec_cmd </dev/null &>$START_LOG
        echo never gets here
    fi 
    # Start service in daemon mode
    ( eval $exec_cmd </dev/null &>$START_LOG & )
    vlog $(dts) 'started in background'
    # No check for pid
    (( WAIT_SECS > 0 )) || {
        log "OK: '$APPL_NAME' started"
        exit 0
    }

    # Set wait time end
    let wte=$(date +%s)+WAIT_SECS
    # Wait till service start or time end come
    while true; do
        sleep 0.1
        appIsUp && break
        (( $(date +%s) > wte )) && {
            error failed to start after $WAIT_SECS secs
            exit 1
        }
    done
    log "OK: '$APPL_NAME' started, PID: $APPL_PID"
    exit 0
}

# stop: try to with kill SIGTERM for 5 secs,
# after that print WARNING and kill -9
appl_stop() {
    if appIsUp ; then
        let cc=0
        while true ; do
            let cc=cc+1
            vlog kill count updated: cc=$cc
            (( cc > 10 )) && {
                vlog too many kill attempts: $cc
                break
            }
            vlog "[$cc]" executing kill $APPL_PID
            [[ $DRYRUN ]] && {
                vlog DRYRUN exit
                exit 0
            }
            kill $APPL_PID &>/dev/null
            sleep 0.5
            appIsUp || break
        done
        [[ -d /proc/$APPL_PID ]] &&{
            warning "failed to stop '$APPL_NAME', PID:$APPL_PID with SIGTERM"
            kill -9 $APPL_PID &>/dev/null
            warning "using kill -9 $APPL_PID"
            appIsUp && error "failed to stop '$APPL_NAME' with kill -9"
        }
        vlog removing pid file: $PID_FILE
        rm -f $PID_FILE
    fi
    log "OK: '$APPL_NAME' stopped"
    exit 0
}

# status
appl_status() {
    rc=0
    if appIsUp ; then
        log "status: running: '$APPL_NAME', PID: $APPL_PID" 
    else
        rc=1
        log "status: down: '$APPL_NAME'"
    fi
    exit $rc
}

###############
# Main

oper=${1:-status}

case $oper in
    (status) appl_status ;;
    (start)
        # check FG run option
        [[ ${2:-x} == --fg ]] && FG_EXECJ=1
        appl_start ;;
    (stop)   appl_stop   ;;
    (*)
        error invalid operation argument: $oper
        usage 1 ;;
esac
