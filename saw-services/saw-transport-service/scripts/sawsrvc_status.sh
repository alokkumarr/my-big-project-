#!/bin/bash
# Script to report SAW-SERVICE service status on $SAW_SERVICE_HOST:$SAW_SERVICE_PORT
# SAW_SERVICE_HOST can be specified in environment
# SAW_SERVICE_HOST defaults to `/bin/hostname -s`
# SAW_SERVICE_PORT can be specified in environment
# SAW_SERVICE_PORT can be given as command line argument

CMD=${0##*/}

usage() {
    cat<<EEUUSS
Usage: $CMD [[SAW_SERVICE_HOST:]SAW_SERVICE_PORT]
    SAW_SERVICE_HOST defaults to 'hostname -s'
    SAW_SERVICE_PORT defaults to value from /etc/saw/service.env
EEUUSS
    exit 0
}
[[ $1 == -h* ]] && usage

(( $# > 0 )) && {
    arg="$1"
    if [[ $arg = *:* ]] ; then
        SAW_SERVICE_HOST=${arg%:*}
        SAW_SERVICE_PORT=${arg##*:}
    else
        SAW_SERVICE_PORT=$arg
    fi
}
: ${SAW_SERVICE_HOST:=$(/bin/hostname -s)}
: ${SAW_SERVICE_HOST:?host unknown}

[[ -z $SAW_SERVICE_PORT ]] && {
    [[ -f /etc/saw/service.env ]] && {
        # SAW_SERVICE_PORT
        source /etc/saw/service.env || exit
    }
}
: ${SAW_SERVICE_PORT:?value empty, must be set in /etc/saw/service.env or provided as an argument}

# Standard Nagios exit codes
OK=0
WARNING=1
CRITICAL=2
UNKNOWN=3 

rc=$UNKNOWN
#
#$ curl -sS rtmapr101:9100/sr
# {"service":"SAW-Service","status":"Stale"}


rsp=$( /usr/bin/curl -qsS $SAW_SERVICE_HOST:$SAW_SERVICE_PORT/sr 2>&1 )
case "$rsp" in
(*Alive*)
    echo OK SAW_SERVICE on $SAW_SERVICE_HOST:$SAW_SERVICE_PORT is Alive
    rc=$OK ;;
(*Stale*)
    echo ERROR: SAW_SERVICE on $SAW_SERVICE_HOST:$SAW_SERVICE_PORT is Stale
    rc=$CRITICAL ;;
(*)
    echo ERROR: curl $SAW_SERVICE_HOST:$SAW_SERVICE_PORT/sr returns:
    echo "$rsp"
    rc=$CRITICAL
esac
exit $rc

#
