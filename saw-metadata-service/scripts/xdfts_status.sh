#!/bin/bash
# Script to report XDFTS service status on $XDFTS_HOST:$XDFTS_PORT
# XDFTS_HOST can be specified in environment
# XDFTS_HOST defaults to `/bin/hostname -s`
# XDFTS_PORT can be specified in environment
# XDFTS_PORT can be given as command line argument

CMD=${0##*/}

usage() {
    cat<<EEUUSS
Usage: $CMD [[XDFTS_HOST:]XDFTS_PORT]
    XDFTS_HOST defaults to 'hostname -s'
    XDFTS_PORT defaults to value from /etc/bda/xdfts.env
EEUUSS
    exit 0
}
[[ $1 == -h* ]] && usage

(( $# > 0 )) && {
    arg="$1"
    if [[ $arg = *:* ]] ; then
        XDFTS_HOST=${arg%:*}
        XDFTS_PORT=${arg##*:}
    else
        XDFTS_PORT=$arg
    fi
}
: ${XDFTS_HOST:=$(/bin/hostname -s)}
: ${XDFTS_HOST:?host unknown}

[[ -z $XDFTS_PORT ]] && {
    [[ -f /etc/bda/xdfts.env ]] && {
        # XDFTS_PORT
        source /etc/bda/xdfts.env || exit
    }
}
: ${XDFTS_PORT:?value empty, must be set in /etc/bda/xdfts.env or provided as an argument}

# Standard Nagios exit codes
OK=0
WARNING=1
CRITICAL=2
UNKNOWN=3 

rc=$UNKNOWN
#
#$ curl -sS rtmapr101:9100/sr
# {"service":"XDF-Transport-Server","status":"Stale"}


rsp=$( /usr/bin/curl -qsS $XDFTS_HOST:$XDFTS_PORT/sr 2>&1 )
case "$rsp" in
(*Alive*)
    echo OK XDFTS on $XDFTS_HOST:$XDFTS_PORT is Alive
    rc=$OK ;;
(*Stale*)
    echo ERROR: XDFTS on $XDFTS_HOST:$XDFTS_PORT is Stale
    rc=$CRITICAL ;;
(*)
    echo ERROR: curl $XDFTS_HOST:$XDFTS_PORT/sr returns:
    echo "$rsp"
    rc=$CRITICAL
esac
exit $rc

#
