#!/bin/bash
# Script to report RTIS service status on $RTIS_HOST:$RTIS_PORT
# RTIS_HOST can be specified in environment
# RTIS_HOST defaults to `/bin/hostname -s`
# RTIS_PORT can be specified in environment
# RTIS_PORT can be given as command line argument

CMD=${0##*/}

usage() {
    cat<<EEUUSS
Usage: $CMD [[RTIS_HOST:]RTIS_PORT]
    RTIS_HOST defaults to 'hostname -s'
    RTIS_PORT defaults to value from /etc/bda/rtis.env
EEUUSS
    exit 0
}
[[ $1 == -h* ]] && usage

(( $# > 0 )) && {
    arg="$1"
    if [[ $arg = *:* ]] ; then
        RTIS_HOST=${arg%:*}
        RTIS_PORT=${arg##*:}
    else
        RTIS_PORT=$arg
    fi
}
: ${RTIS_HOST:=$(/bin/hostname -s)}
: ${RTIS_HOST:?host unknown}

[[ -z $RTIS_PORT ]] && {
    [[ -f /etc/bda/rtis.env ]] && {
        # RTIS_PORT
        source /etc/bda/rtis.env || exit
    }
}
: ${RTIS_PORT:?value empty, must be set in /etc/bda/rtis.env or provided as an argument}

# Standard Nagios exit codes
OK=0
WARNING=1
CRITICAL=2
UNKNOWN=3 

rc=$UNKNOWN
#
#$ curl -sS rtfe101:9100/sr
# {"service":"FrontEnd-Server","status":"Stale"}
# {"service":"FrontEnd-Server","status":"Alive"}

rsp=$( /usr/bin/curl -qsS $RTIS_HOST:$RTIS_PORT/sr 2>&1 )
case "$rsp" in
(*Alive*)
    echo OK RTIS on $RTIS_HOST:$RTIS_PORT is Alive
    rc=$OK ;;
(*Stale*)
    echo ERROR: RTIS on $RTIS_HOST:$RTIS_PORT is Stale
    rc=$CRITICAL ;;
(*)
    echo ERROR: curl $RTIS_HOST:$RTIS_PORT/sr returns:
    echo "$rsp"
    rc=$CRITICAL
esac
exit $rc

#
