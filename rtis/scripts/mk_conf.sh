#!/usr/bin/env bash

CMD=$(basename $0)

errout() {
    echo 1>&2 "$@"
}

usage() {
    errout "\
Usage: $CMD {TMPL_FILE|-} {VARS_FILE|VAR=VAL}..
    read TMPL_FILE or stdin (-),
    substitute \${:VAR:} and <VAR> patterns with VAL,
    print result to stdout,
    log on stderr"
    exit 1
}
[[ $# -lt 1 || ${1:-x} = -h ]] && usage

TMPL_FILE=${1:-}
: ${TMPL_FILE:?arg missing}
shift

if [[ $TMPL_FILE = '-' ]] ; then
    INP_STR=$(cat -)
else
    INP_STR=$(cat $TMPL_FILE)
fi

errout "INFO: Generated $(date +'%D %T') by $CMD"
[[ -z "$INP_STR" ]] && {
    echo "# empty template file"
    errout "WARNING: Empty template file"
    exit 1
}

# extended pattern matching operators
shopt -s extglob

subs_name() {
    sn=${1:?arg missing}
    sv=${2:?arg missing}
    # replace placeholders
    nstr=${INP_STR//\$\{:$sn:\}/$sv}
# early test-app <LG-GT> special
#    nstr=${INP_STR//<$sn>/$sv} 
    #
    [[ "$nstr" = "$INP_STR" ]] && return
    errout "INFO: $sn = $sv" 
    INP_STR="$nstr"
}

subs_vars() {
    vars_file=${1:-}
    : ${vars_file:?argument missing}
    while read s ; do
        # trim comment
        s=${s%%\#*}

        # Check assignment
        [[ $s == *=* ]] || continue

        #
        s=${s##+( )} # strip leading blanks
        s=${s%%+( )} # strip trailing blanks
        s=${s/*( )=*( )/=} # strip blanks arounf first '='

        # extract name and value
        sn=${s%%=*} 
        sv=${s#*=}

        subs_name "$sn" "$sv"
    done < $vars_file
}

for arg in "$@" ; do
    case "$arg" in
    (*=*)
        subs_name "${arg%%=*}" "${arg#*=}" ;;
    (*)
        subs_vars "${arg}" ;;
    esac
done
#
# Print result to stdout
echo "$INP_STR"
#
let rc=0
[[ $INP_STR == *\$\{:[[:word:]]+([.[:word:]]):\}* ]] && {
    errout WARNING: '${:XXX:}' placeholders remains after processing "'$TMPL_FILE'"
    let rc=rc+1
}
[[ $INP_STR == *\<[[:word:]]+([.[:word:]])\>* ]] && {
    errout WARNING: '<XXX>' placeholders remains after processing "'$TMPL_FILE'"
    let rc=rc+2
}
exit $rc
