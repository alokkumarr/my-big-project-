#!/bin/bash
# Script to stop local SAW-SERVICE service on the given port

[[ ${1:-x} = '-h' ]] && {
    echo "usage: $(basename $0) [<saw_service_port>]"
    echo "       stops saw service, if optional <service_port> if not given,"
    echo "       using SAW_SERVICE_PORT from /etc/saw/service.env"
    exit
}

source /etc/saw/service.env || exit

(( $# > 0 )) && SAW_SERVICE_PORT=$1
: ${SAW_SERVICE_PORT:?value empty, must be set in /etc/saw/service.env or provided as an argument}

SAW_SERVICE_HOST=$(/bin/hostname -s)

#
pid_fnm=$(cd $(dirname $0)/.. ; pwd)/var/run/saw.$SAW_SERVICE_PORT.pid
[[ -s $pid_fnm ]]                           && echo FNM: $pid_fnm       &&
pid=$( <$pid_fnm )                          && echo PID: $pid           &&
[[ -d /proc/${pid:-000} ]]                  && echo OK: -d /proc/$pid   &&
kill $pid                                   && echo OK: kill $pid       &&
echo stopped: SAW-SERVICE on $SAW_SERVICE_HOST:$SAW_SERVICE_PORT &&
exit 0
#
echo NO SAW-SERVICE on $SAW_SERVICE_HOST:$SAW_SERVICE_PORT
exit 1
