#!/bin/bash
###
# Create file from template and key values file
###

CMD=$(basename $0)

errout() {
    echo 1>&2 "$@"
}

usage() {
    echo "\
Usage: $CMD {TEMPL_FILE|-} {VARS_FILE|VAR=VAL}..
    read TEMPL_FILE or stdin (-),
    substitute \${:VAR:} patterns with VAL,
    print result to stdout,
    log on stderr"
    exit 0
}
[[ $# -lt 1 || ${1:-x} = -h ]] && usage

TEMPL_FILE=${1:-}
: ${TEMPL_FILE:?template file arg missing}
shift

if [[ $TEMPL_FILE = '-' ]] ; then
    INP_STR=$(cat -)
else
    INP_STR=$(cat $TEMPL_FILE)
fi

errout "# Generated $(date +'%D %T') by $CMD"
[[ -z "$INP_STR" ]] && {
    echo "# empty template file"
    errout "WARNING: Empty template file"
    exit 1
}

# extended pattern matching operators
shopt -s extglob

subs_name() {
    sn=${1:?parameter name arg missing}
    sv=${2:?parameter value arg missing}
    # replace placeholders
    nstr=${INP_STR//\$\{:$sn:\}/$sv}
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
        : ${sn:?parameter name missing before =}
        sv=${s#*=}
        : ${sv:?"parameter value after '$sn=' missing"}
        subs_name "$sn" "$sv"
    done < $vars_file
}

for arg in "$@" ; do
    # skip empty arg
    [[ $arg ]] || continue
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
    errout WARNING: '${:XXX:}' placeholders remains after processing "'$TEMPL_FILE'"
    let rc=rc+1
}
exit $rc
