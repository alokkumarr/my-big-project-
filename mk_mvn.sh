#!/bin/bash

VERSION=1.3.0626
# 0626 fix git HEAD for empty repository

# This script calculates product release number and
# executes mvn with -Dprod.release=<release_number argument>

bnum=
btyp='c' # build number type 'commit count'
# First arg, if it is numeric', specifies bamboo build number
if (( $# )) ; then
    arg=$1
    # check if it is build number from bamboo
    if [[ $arg =~ ^[0-9]+$ ]] ; then
            # command in bamboo:
            # ./mk_mvn.sh ${bamboo.buildNumber} clean package
            bnum=$arg
            btyp='b' # build number type 'bamboo'
            shift
    else
        # skip '-' arg
        [[ $arg = '-' ]] && shift
    fi
else
    # default arguments
    set -- clean package
fi

# Empty if there is no object in repository
git_head=$( git rev-parse --short=5 HEAD -- . 2>/dev/null )

# Calculate bnum if not given
[[ $bnum ]] || {
    [[ $git_head ]] && {
        last_tag=$( git for-each-ref --count=1 --sort='-*authordate' --format='%(tag)' )
        if [[ $last_tag ]] ; then
            bnum=$( git rev-list --count HEAD ^$last_tag )
        else
            bnum=$( git rev-list --count HEAD )
        fi
    }
}

# provide values if still empty
[[ $git_head ]] || git_head=00000
[[ $bnum ]] || {
    btyp='u' # unknown
    bnum='999'
}


# Check dirty files in git directory
# add w<day-of-year>.<min-of-day>
wnum=
[[ $( git status --porcelain ) ]] && {
    dsu=$(date -u +%s)  # UTC
    ydu=$(date -u +%j -d @$dsu) # day of year (001-366)
    wnum=_w$ydu.$(( $dsu % 86400 / 60 ))
}

# RPM release number:
# ( b<BAMBOO_BUILD> / c<COMMIT_COUNT> / u999 )_g<HEAD_HASH>[ _w<DAY_OF_YEAR>.<MIN_OF_DAY> ]
rpm_release=${btyp}${bnum}_g${git_head}${wnum:-}

mvn_cmd=( mvn -Dprod.release=${rpm_release} "$@" )

echo '########################'
echo 'EXECUTE MAVEN'
echo '########################'

# MAVEN COMMAND
[[ ${DRYRUN:-0} != 0 ]] && {
    echo '$' "${mvn_cmd[@]}"
    echo DRYRUN exit
    exit 0
}

(
set -vx
${mvn_cmd[@]}
) || {
    echo ERROR return from mvn
    exit
}


# CREATE MD5
rpms=( $( find $( find . -name target -type d ) -name '*.rpm' ) )
: ${rpms:?no RPM file found}

cat <<EEOOHH
########################
Build results:
###
EEOOHH

for rpmpath in "${rpms[@]}" ; do

    rpmdir=$( dirname $rpmpath )
    rpmfnm=$( basename $rpmpath )
    md5fnm=${rpmfnm%.rpm}.md5

    (
        cd $rpmdir
        # CALCULATE MD5
        md5sum $rpmfnm >$md5fnm || {
            echo WARNING: Failed to execute md5sum
            exit 0
        }
    )

    cat <<EEOOMM
RPM: $rpmdir/$rpmfnm
MD5: $rpmdir/$md5fnm
###
To validate md5:
( cd $rpmdir; md5sum -c $md5fnm )
EEOOMM
done

cat <<EEOOTT
########################
DONE
EEOOTT

exit

########
# To use in Bamboo task:
########
./mk_mvn.sh ${bamboo.buildNumber}

########
# To build XDF with custom name:
########
./mk_mvn.sh -Dprod.name=xdf-sv clean package
# NB: no underscore in custom name

########
# To build xda-genapp with custom name:
########
./mk_mvn.sh -Dprod.shortName=my-own-app clean package
# NB: no underscore in custom name
