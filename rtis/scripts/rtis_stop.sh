#!/bin/bash
# Script to stop local RTIS service on the given port

[[ ${1:-x} = '-h' ]] && {
    echo "usage: $(basename $0) [<rtis_port>]"
    echo "       stops rtis service, if optional <rtis_port> if not given,"
    echo "       using RTIS_PORT from /etc/bda/rtis.env"
    exit
}

source /etc/bda/rtis.env || exit

(( $# > 0 )) && RTIS_PORT=$1
: ${RTIS_PORT:?value empty, must be set in /etc/bda/rtis.env or provided as an argument}

RTIS_HOST=$(/bin/hostname -s)

#
pid_fnm=/var/bda/rtis/run/rtis.$RTIS_PORT.pid
[[ -s $pid_fnm ]]                           && echo FNM: $pid_fnm       &&
pid=$( <$pid_fnm )                          && echo PID: $pid           &&
[[ -d /proc/${pid:-000} ]]                  && echo OK: -d /proc/$pid   &&
kill $pid                                   && echo OK: kill $pid       &&
echo stopped: RTIS on $RTIS_HOST:$RTIS_PORT &&
exit 0
#
echo NO RTIS on $RTIS_HOST:$RTIS_PORT
exit 1
