#!/bin/bash
##
# rtis to be started in
# /etc/rc.d/rc.local
##
RTIS_USER=rtis-user
(( $(id -u) == 0 )) && {
    echo rerun with $RTIS_USER: "$0 $@"
    exec /bin/su $RTIS_USER -c "$0 $@"
    exit 1
}

CMD=${0##*/}
BAS=${CMD%.sh}

usage() {
    echo usage:
    echo "  $CMD - start RTIS_LOOP in background (daemon mode)"
    echo "  $CMD RTIS_LOOP - continuosly run rtis_start.sh"
    exit
}
[[ ${1:x} = "-h" ]] && usage

source /etc/bda/rtis.env || exit 1
: ${RTIS_PORT:?variable not set in /etc/bda/rtis.env}

: ${RTIS_HOST:=$(/bin/hostname -s)}
: ${RTIS_HOST:?value no set}

# Check if RTIS service is running on the box
/usr/bin/curl -qs http://$RTIS_HOST:$RTIS_PORT/sr &>/dev/null && {
    echo 1>&2 "RTIS is already running on $RTIS_HOST:$RTIS_PORT"
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
        echo From: rtis-user
        echo "Subject: $subj"
        echo
        echo "Date: $sdtz"
        cat -
    } | sendmail $RTIS_USER
    #
    echo $sdtz - Email sent to $RTIS_USER
    echo "Subject: $subj"
}

: ${RTIS_HOME:?variable not set in /etc/bda/rtis.env}

# Top level run
(( $# < 1 )) && {
    log=/var/bda/rtis/log/$BAS.log
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
        $0 RTIS_LOOP "$@" &
        )
    exit 0
}

[[ $1 = RTIS_LOOP ]] || usage

# Assume stdout, stderr redirected
let cnt=0
let tm_beg=0
send_email "INFO.001 - RTIS on $RTIS_HOST runner loop started" <<EEOOMM1
$(dtz) INFO - rtis_runner.sh RTIS_LOOP started
EEOOMM1
while : ; do
    let ++cnt
    echo $(dtz) - starting: rtis_start.sh, cnt=$cnt
    let tm_beg=$(tms)
    # will use RTIS_PORT from /etc/bda/rtis.env
    /bin/sh -xc "let pid=\$\$; exec ${RTIS_HOME}/sbin/rtis_start.sh --fxj"
    rc=$?
    let tm_end=$(tms)
    let ss_run=$((tm_end-tm_beg))
    echo $(dtz) - ended: rtis_start.sh, rc=$rc, dt=${ss_run}s
    # Restart if was running for more than 10 min
    (( ss_run > 600 )) && {
        let cnt=0
        send_email "WARNING.002 - RTIS on $RTIS_HOST restarting" <<EEOOMM2
$(dtz) WARNING - rtis service stopped after ${ss_run}s, restarting
EEOOMM2
        sleep 1
        continue
    }
    # check was started more than 3 times
    (( cnt > 2 )) && {

        echo $(dtz) ERROR - short running RTIS service was restarted $cnt times;
        echo Restart limit exceeded, rtis_runner exiting.
        #
        send_email "ERROR.003 - RTIS on $RTIS_HOST too many stops" <<EEOOMM3
$(dtz) ERROR - short running RTIS service was restarted $cnt times;
Restart limit exceeded, rtis_runner LOOP exiting.
Fix the RTIS problem and restart $CMD manually.
EEOOMM3
        exit 1
    }
    # restart
    send_email "WARNING.004 - RTIS on $RTIS_HOST restarting after short run" <<EEOOMM4
$(dtz) WARNING - RTIS service was short running $cnt times, limit 3;
Fix the RTIS restart reason to avoid manual restart.
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
# start BDA front end server
/opt/bda/rtis/sbin/rtis_runner.sh &>/tmp/rc.local.rtis_runner.log 
