#!/bin/bash
###
# Create file from template and key values file
###
CMD=$(basename $0)

errout() {
    echo 1>&2 "$@"
}

usage() {
    errout "\
Usage: $CMD {TMPL_FILE|-} {VARS_FILE|VAR=VAL}..
    read TMPL_FILE or stdin (-),
    substitute \${:VAR:} patterns with VAL,
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
    INP_STR=$(<$TMPL_FILE) || exit
fi

#errout "# Generated $(date +'%D %T') by $CMD"
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
    #
    [[ "$nstr" = "$INP_STR" ]] && return
    errout "INFO: $sn = $sv" 
    INP_STR="$nstr"
}

VAR_RX="[[:word:]]+([.[:word:]])"
VAL_RX="+([^ ])"
subs_vars() {
    vars_file=${1:-}
    : ${vars_file:?argument missing}
    while read ss ; do
        # Quick check
        [[ $ss = *( )$VAR_RX*( )[:=]* ]] || continue
        s=${ss##+( )} # strip leading blanks
        s=${s%%\#*}   # trim comment
        s=${s%%+( )}  # strip trailing blanks
        # strip blanks arounf first '[:=]', replace with '='
        s=${s/*( )[=:]*( )/=}
        # Check var=val syntax
        [[ $s = $VAL_RX=$VAL_RX ]] || {
            errout "$vars_file: invalid line:"
            errout "$ss"
            continue
        }

        # extract name and value
        sn=${s%%=*} 
        sv=${s#*=}

        subs_name "$sn" "$sv"
    done < $vars_file
}

for arg in "$@" ; do
    case "$arg" in
    (*[:=]*) # one var
        subs_name "${arg%%[:=]*}" "${arg#*[:=]}" ;;
    (*)   # vars in file
        subs_vars "${arg}" ;;
    esac
done
#
# Print result to stdout
echo "$INP_STR"
#
rc=0
[[ $INP_STR == *\$\{:[[:word:]]+([.[:word:]]):\}* ]] && {
    errout WARNING: '${:XXX:}' placeholder'(s)' remains after processing "'$TMPL_FILE'"
    rc=1
}
exit $rc
