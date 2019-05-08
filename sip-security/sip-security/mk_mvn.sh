#!/bin/bash

echo '########################'
echo 'EXECUTE MAVEN'
echo '########################'

bnum=${1:-} # build number from bamboo: ${bamboo.buildNumber}

btyp='b' # build number type 'bamboo'
[[ $bnum ]] || {
    btyp='c' # type 'commit count'
    last_tag=$( git for-each-ref --count=1 --sort='-*authordate' --format='%(tag)' )
    if [[ $last_tag ]] ; then
        bnum=$( git rev-list --count HEAD ^$last_tag )
    else
        bnum=$( git rev-list --count HEAD )
    fi
}

set -vx

# RPM release number:
# g<HEAD_HASH>_b<BAMBOO_BUILD>
# g<HEAD_HASH>_c<COMMIT_COUNT>

rpm_release=g$( git rev-parse --short=5 HEAD )_${btyp}${bnum}

# MAVEN COMMAND
[[ $DRYRUN ]] && {
    echo mvn -Dprod.release=${rpm_release} package
    echo DRYRUN exit
    exit 0
}
mvn -Dprod.release=${rpm_release} package || exit

# CREATE MD5
rpmpath=$( find target -name \*.rpm )
: ${rpmpath:?RPM file not found}

rpmdir=$( dirname $rpmpath )
rpmfnm=$( basename $rpmpath )
md5fnm=${rpmfnm%.rpm}.md5

cd $rpmdir

# CALCULATE MD5
md5sum $rpmfnm > $md5fnm || {
    echo WARNING: Failed to execute md5sum
    exit 0
}

cat <<EEOOMM
########################
Build results:
###
RPM: $rpmpath
MD5: ${rpmpath%.rpm}.md5
###
To validate md5:
( cd $rpmdir ; md5sum -c $md5fnm )
########################
DONE
EEOOMM
exit

########
Bamboo task:
#################################
set -vx
#################################
./mk_mvn.sh ${bamboo.buildNumber}
