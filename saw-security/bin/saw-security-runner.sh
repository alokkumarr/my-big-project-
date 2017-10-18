#!/bin/bash
##
# saw-security to be started in
# /etc/rc.d/rc.local
##
# NB: hardcoded user name
APPL_USER=saw
(( $(id -u) == 0 )) && {
    echo rerun with $APPL_USER: "$0 $@"
    exec /bin/su $APPL_USER -c "$0 $@"
    never gets here
    exit 1
}

CMD_ARG="$0"
CMD=${CMD_ARG##*/}    # strip dir/
BAS=${CMD%.sh}  # strip .sh
APPL_NAME=saw-security

usage() {
    echo usage:
    echo "  $CMD - start $APPL_NAME runner in background (daemon mode)"
    echo "  $CMD CONT_LOOP - continuosly start $APPL_NAME in foreground"
    exit
}
[[ ${1:-x} =~ --?[hH] ]] && usage

APPL_HOME=/opt/bda/$APPL_NAME # soft link
( cd $APPL_HOME ) || exit
[[ -x $APPL_HOME/bin/appl_info ]] || {
    $APPL_HOME/bin/appl_info
    exit
}
appl_info() {
    $APPL_HOME/bin/appl_info ${1:?arg missing}
}
# YYMMDD hhmmss.TZ
dtz() { date +'%D %T.%Z'; }
# time in secs
tms() { date +'%s'      ; }

# Top level run
(( $# < 1 )) && {
    vardir=$( appl_info vardir )
    ( cd $vardir ) || exit
    log=$vardir/log/$BAS.log
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
        echo "# starting runner loop"
        $0 CONT_LOOP "$@" &
        )
    exit 0
}

[[ ${1:-x} = CONT_LOOP ]] || usage

# Assume stdout, stderr redirected
let cnt=0
let tm_beg=0
echo $(dtz) INFO.001 - $APPL_NAME runner loop started
APPL_CMD=${APPL_HOME}/bin/$APPL_NAME.sh
echo APPL_CMD=$APPL_CMD
#
while : ; do
    let ++cnt
    echo $(dtz) - starting: $CMD, cnt=$cnt
    let tm_beg=$(tms)
    # start service in foreground
    # pid will be printed in log
    /bin/sh -xc "let pid=\$\$; exec $APPL_CMD start --fg"
    rc=$?
    let tm_end=$(tms)
    let ss_run=$((tm_end-tm_beg))
    echo $(dtz) - ended: $APPL_CMD, rc=$rc, dt=${ss_run}s
    # Restart if was running for more than 10 min
    (( ss_run > 600 )) && {
        let cnt=0
        cat <<EEOOMM2
$(dtz) WARNING - $APPL_NAME service stopped after ${ss_run}s, restarting

EEOOMM2
        sleep 1
        continue
    }
    # check was started more than 3 times
    (( cnt > 2 )) && {
        echo
        echo $(dtz) ERROR - short running $APPL_NAME service was restarted $cnt times;
        echo Restart limit exceeded.  Check log files in /var/bda/$APPL_NAME/log/.
        echo Fix the $APPL_NAME problem and restart runner manually:
        echo "$ sudo $CMD_ARG"
        echo Exiting
        #
        exit 1
    }
    # restart
    cat <<EEOOMM4
$(dtz) WARNING.004 - restarting $APPL_NAME after short run,
short run count: $cnt, limit: 3;
Fix the $APPL_NAME restart reason to avoid manual restart.

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
# start BDA saw-security server
( /opt/bda/saw-security/bin/saw-security-runner.sh &>/tmp/rc.local.saw-security-runner.log & )
