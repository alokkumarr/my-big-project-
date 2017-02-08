#!/bin/bash
##
# xdfts to be started in
# /etc/rc.d/rc.local
##
XDFTS_USER=mapr
(( $(id -u) == 0 )) && {
    echo rerun with $XDFTS_USER: "$0 $@"
    exec /bin/su $XDFTS_USER -c "$0 $@"
    exit 1
}

CMD=${0##*/}
BAS=${CMD%.sh}

usage() {
    echo usage:
    echo "  $CMD - start XDFTS_LOOP in background (daemon mode)"
    echo "  $CMD XDFTS_LOOP - continuosly run xdfts_start.sh"
    exit
}
[[ ${1:x} = "-h" ]] && usage

source /etc/bda/xdfts.env || exit 1
: ${XDFTS_PORT:?variable not set in /etc/bda/xdfts.env}

: ${XDFTS_HOST:=$(/bin/hostname -s)}
: ${XDFTS_HOST:?value no set}

# Check if XDFTS service is running on the box
/usr/bin/curl -qs http://$XDFTS_HOST:$XDFTS_PORT/sr &>/dev/null && {
    echo 1>&2 "XDFTS is already running on $XDFTS_HOST:$XDFTS_PORT"
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
    } | sendmail $XDFTS_USER
    #
    echo $sdtz - Email sent to $XDFTS_USER
    echo "Subject: $subj"
}

: ${XDFTS_HOME:?variable not set in /etc/bda/xdfts.env}

# Top level run
(( $# < 1 )) && {
    log=$XDFTS_HOME/var/log/$BAS.log
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
        $0 XDFTS_LOOP "$@" &
        )
    exit 0
}

[[ $1 = XDFTS_LOOP ]] || usage

# Assume stdout, stderr redirected
let cnt=0
let tm_beg=0
send_email "INFO.001 - XDFTS on $XDFTS_HOST runner loop started" <<EEOOMM1
$(dtz) INFO - xdfts_runner.sh XDFTS_LOOP started
EEOOMM1
while : ; do
    let ++cnt
    echo $(dtz) - starting: xdfts_start.sh, cnt=$cnt
    let tm_beg=$(tms)
    # will use XDFTS_PORT from /etc/bda/xdfts.env
    /bin/sh -xc "let pid=\$\$; exec ${XDFTS_HOME}/sbin/xdfts_start.sh --fxj"
    rc=$?
    let tm_end=$(tms)
    let ss_run=$((tm_end-tm_beg))
    echo $(dtz) - ended: xdfts_start.sh, rc=$rc, dt=${ss_run}s
    # Restart if was running for more than 10 min
    (( ss_run > 600 )) && {
        let cnt=0
        send_email "WARNING.002 - XDFTS on $XDFTS_HOST restarting" <<EEOOMM2
$(dtz) WARNING - xdfts service stopped after ${ss_run}s, restarting
EEOOMM2
        sleep 1
        continue
    }
    # check was started more than 3 times
    (( cnt > 2 )) && {

        echo $(dtz) ERROR - short running XDFTS service was restarted $cnt times;
        echo Restart limit exceeded, xdfts_runner exiting.
        #
        send_email "ERROR.003 - XDFTS on $XDFTS_HOST too many stops" <<EEOOMM3
$(dtz) ERROR - short running XDFTS service was restarted $cnt times;
Restart limit exceeded, xdfts_runner LOOP exiting.
Fix the XDFTS problem and restart $CMD manually.
EEOOMM3
        exit 1
    }
    # restart
    send_email "WARNING.004 - XDFTS on $XDFTS_HOST restarting after short run" <<EEOOMM4
$(dtz) WARNING - XDFTS service was short running $cnt times, limit 3;
Fix the XDFTS restart reason to avoid manual restart.
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
/opt/bda/xdfts/sbin/xdfts_runner.sh &>/tmp/rc.local.xdfts_runner.log 
