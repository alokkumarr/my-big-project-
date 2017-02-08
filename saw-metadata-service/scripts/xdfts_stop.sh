#!/bin/bash
# Script to stop local XDFTS service on the given port

[[ ${1:-x} = '-h' ]] && {
    echo "usage: $(basename $0) [<xdfts_port>]"
    echo "       stops xdfts service, if optional <xdfts_port> if not given,"
    echo "       using XDFTS_PORT from /etc/bda/xdfts.env"
    exit
}

source /etc/bda/xdfts.env || exit

(( $# > 0 )) && XDFTS_PORT=$1
: ${XDFTS_PORT:?value empty, must be set in /etc/bda/xdfts.env or provided as an argument}

XDFTS_HOST=$(/bin/hostname -s)

#
pid_fnm=$(cd $(dirname $0)/.. ; pwd)/var/run/xdfts.$XDFTS_PORT.pid
[[ -s $pid_fnm ]]                           && echo FNM: $pid_fnm       &&
pid=$( <$pid_fnm )                          && echo PID: $pid           &&
[[ -d /proc/${pid:-000} ]]                  && echo OK: -d /proc/$pid   &&
kill $pid                                   && echo OK: kill $pid       &&
echo stopped: XDFTS on $XDFTS_HOST:$XDFTS_PORT &&
exit 0
#
echo NO XDFTS on $XDFTS_HOST:$XDFTS_PORT
exit 1
