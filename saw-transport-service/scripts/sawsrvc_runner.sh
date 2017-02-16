#!/bin/bash
##
# xdfts to be started in
# /etc/rc.d/rc.local
##
SAW_SERVICE_USER=mapr
(( $(id -u) == 0 )) && {
    echo rerun with $SAW_SERVICE_USER: "$0 $@"
    exec /bin/su $SAW_SERVICE_USER -c "$0 $@"
    exit 1
}

CMD=${0##*/}
BAS=${CMD%.sh}

usage() {
    echo usage:
    echo "  $CMD - start SAW_SERVICE_LOOP in background (daemon mode)"
    echo "  $CMD SAW_SERVICE_LOOP - continuosly run xdfts_start.sh"
    exit
}
[[ ${1:x} = "-h" ]] && usage

source /etc/saw/service.env || exit 1
: ${SAW_SERVICE_PORT:?variable not set in /etc/saw/service.env}

: ${SAW_SERVICE_HOST:=$(/bin/hostname -s)}
: ${SAW_SERVICE_HOST:?value no set}

# Check if SAW-SERVICE service is running on the box
/usr/bin/curl -qs http://$SAW_SERVICE_HOST:$SAW_SERVICE_PORT/sr &>/dev/null && {
    echo 1>&2 "SAW_SERVICE is already running on $SAW_SERVICE_HOST:$SAW_SERVICE_PORT"
    exit 2
}

# YYMMDD hhmmss.TZ
dtz() { date +'%D %T.%Z'; }
# time in secs
tms() { date +'%s'      ; }

send_email() { # Subject << BODY
    subj="${1:?arg missing}"
    sdtz=$(dtz)
    {
        echo From: mapr
        echo "Subject: $subj"
        echo
        echo "Date: $sdtz"
        cat -
    } | sendmail $SAW_SERVICE_USER
    #
    echo $sdtz - Email sent to $SAW_SERVICE_USER
    echo "Subject: $subj"
}

: ${SAW_SERVICE_HOME:?variable not set in /etc/saw/service.env}

# Top level run
(( $# < 1 )) && {
    log=/var/saw/log/$BAS.log
    # Backup log file
    [[ -f $log ]] && {
        /bin/cp -f --backup=numbered $log $log
    }
    # console output
    echo started: $0, log file: $log
    (
        # redirect std streams
        exec &>$log
        exec </dev/null
        # first log record
        echo "$(dtz) - started: $0"
        # start runner loop
        $0 SAW_SERVICE_LOOP "$@" &
        )
    exit 0
}

[[ $1 = SAW_SERVICE_LOOP ]] || usage

# Assume stdout, stderr redirected
let cnt=0
let tm_beg=0
send_email "INFO.001 - SAW_SERVICE on $SAW_SERVICE_HOST runner loop started" <<EEOOMM1
$(dtz) INFO - sawsrvc_runner.sh SAW_SERVICE_LOOP started
EEOOMM1
while : ; do
    let ++cnt
    echo $(dtz) - starting: sawsrvc_start.sh, cnt=$cnt
    let tm_beg=$(tms)
    # will use SAW_SERVICE_PORT from /etc/saw/service.env
    /bin/sh -xc "let pid=\$\$; exec ${SAW_SERVICE_HOME}/sbin/sawsrvc_start.sh --fxj"
    rc=$?
    let tm_end=$(tms)
    let ss_run=$((tm_end-tm_beg))
    echo $(dtz) - ended: sawsrvc_start.sh, rc=$rc, dt=${ss_run}s
    # Restart if was running for more than 10 min
    (( ss_run > 600 )) && {
        let cnt=0
        send_email "WARNING.002 - SAW_SERVICE on $SAW_SERVICE_HOST restarting" <<EEOOMM2
$(dtz) WARNING - SAW service stopped after ${ss_run}s, restarting
EEOOMM2
        sleep 1
        continue
    }
    # check was started more than 3 times
    (( cnt > 2 )) && {

        echo $(dtz) ERROR - short running SAW-SERVICE service was restarted $cnt times;
        echo Restart limit exceeded, xdfts_runner exiting.
        #
        send_email "ERROR.003 - SAW_SERVICE on $SAW_SERVICE_HOST too many stops" <<EEOOMM3
$(dtz) ERROR - short running SAW-SERVICE service was restarted $cnt times;
Restart limit exceeded, sawsrvc_runner LOOP exiting.
Fix the SAW-SERVICE problem and restart $CMD manually.
EEOOMM3
        exit 1
    }
    # restart
    send_email "WARNING.004 - SAW-SERVICE on $SAW_SERVICE_HOST restarting after short run" <<EEOOMM4
$(dtz) WARNING - SAW-SERVICE service was short running $cnt times, limit 3;
Fix the SAW-SERVICE restart reason to avoid manual restart.
EEOOMM4
    # wait 10 sec before restart
    sleep 10
    # continue loop
done

echo never gets here
exit -1

###
# lines to be added to /etc/rc.local
###
# start SAW front end server
/opt/saw/service/sbin/sawsrvc_runner.sh &>/tmp/rc.local.saw_service_runner.log
