#!/bin/bash
##################################################
# Last update:
# 03/10/2017 VERSION and description added
#
VERSION=1.2-16

##################################################
# Helper script to display git and mvn commands
# to be executed for development or release built
##################################################
# Usage:
#  ./mkc_mvn.sh [--pom <pom-file>] <mvn-options>
### Output:
# ... script output here
# # Build command
# mvn -Dprod.release=0_g01ee
###
# Copy/paster to execute 'Build command'
##################################################
# Please update this script from:
# https://stash.synchronoss.net/projects/BDA/repos/bda-devtools/browse/bin/mkc_mvn.sh
##################################################

shopt -s extglob

##################
# Version in Git tag or <version> element in pom.xml:
# <MAJ-NUM>.<MIN-NUM>_dev               # dev build
# <MAJ-NUM>.<MIN-NUM>_wip               # dev build, changes not committed
# <MAJ-NUM>.<MIN-NUM>_rc<RC-NUM>        # pre-release
# <MAJ-NUM>.<MIN-NUM>.0                 # main release
# <MAJ-NUM>.<MIN-NUM>.<FIX-NUM>         # fix release (<FIX-NUM> != 0)
###
# Generic sytax:
# [v]<MAJ-NUM>.<MIN-NUM>[.<FIX-NUM>]*[(./_)[a-z]<ALNUM>]*   # local release
#
# <ALNUM>   :=: [0-9a-z]*
# <NZL_NUM> :=: [1-9][0-9]*    # non zero-leading number
# <VDG_NUM> :=: 0 / <NZL_NUM>  # 0 or non zero-leading number
# <MAJ-NUM> :=: <NZL_NUM>
# <MIN-NUM> :=: <VDG_NUM>
# <FIX-NUM> :=: <VDG_NUM>
# <RC-NUM>  :=: <VDG_NUM>
##
# Parse version in string
get_ver(){
    perl -e '
        if($ARGV[0]=~m/[a-z]?([1-9]\d*\.\d+(?:\.\d+)*(?:[._][a-z][a-z0-9]*)*)(?:$|[^.0-9])/i){
            print($1);
        }
    ' ${1:-}
}
# echo $(get_ver ${1:-'XDF_2.1.0.0_28Dec2016-1240-61-g4758-wip'}) ; exit

##################
# Canonical version string
read_ver_canon() {
  awk -F '[._]' 'BEGIN{rs="~"}  # empty version
    { for(i=1;i<=NF;++i){
        s = sprintf("%6s", $i)  # right align each part
        if( s ~ /[a-z]/ ) {     # any text
          gsub(" ","-",s)       # empty position less than any digit
        } else {
          gsub(" ","0",s)       # replace spaces with 0
        }
        rs = rs s "~"
      }
      exit
    }
    END{print rs}'
}
get_ver_canon() { echo "$1" | read_ver_canon; }

###############################################
# Main
###############################################

# Get git top-level directory
GIT_TOPDIR=$(git rev-parse --show-toplevel)
: ${GIT_TOPDIR:?unknown git toplevel in $(pwd)} 
echo "# Git top dir: $GIT_TOPDIR"

POM_FILE=
# pom file can be given in command line argument '--pom <POM>'
[[ ${1:-X} == '--pom' ]] && {
    shift
    POM_FILE=${1:-}
    : ${POM_FILE?POM file argument missing}
    shift
}

# initialize POM_FILE if not provided in argument
[[ $POM_FILE ]] || {
    POM_FILE=./pom.xml
    [[ -f $POM_FILE ]] || POM_FILE=$GIT_TOPDIR/pom.xml
}
# Check pom.xml
( <$POM_FILE ) || {
    echo "*** failed to read pom file: $POM_FILE"
    exit
}

echo "# Last commit:"
echo "# $(git log -1 --oneline)"

#set -x
SVER=$( head -10 $POM_FILE |
        perl -ne 'm/<version>\s*([1-9][^< ]+)\s*<\/version>/o and print($1) and exit;'
        )
[[ $SVER ]] || {
    echo "*** error: no version in $POM_FILE"
    exit 1
}
PVER=$( get_ver $SVER )
[[ $SVER ]] || {
    echo "*** error: invalid version in $POM_FILE: $SVER"
    exit 1
}

echo "# version from $POM_FILE   : $PVER"
PVER_CANON=$( get_ver_canon "$PVER" )
echo "# canon ver from $POM_FILE : $PVER_CANON"

#####################################
git_descr=$( git describe --long --always --candidates=3 --abbrev=4 --dirty=-wip --match '*[0-9].[0-9]*' )
echo "# git describe             : $git_descr"

### Prev version from git tag
GVER=$( get_ver "$git_descr" )
if [[ $GVER ]] ; then
    echo "# version from describe    : $GVER"
else
    GVER='1.0'
    echo "# no version from describe, using : $GVER"
    echo "# using GVER=$GVER"
fi
GVER_CANON=$( get_ver_canon "$GVER" )
echo    "# canon ver from describe  : $GVER_CANON"

### Calc Release
GREL=$( perl -e '
    if($ARGV[0]=~m/-(\d+-g[0-9a-f]+(?:-\w+)?)$/i){
        $s=$1;
        $s=~tr/-./_/;
        print($s);
    }
    ' "$git_descr" )
if [[ $GREL ]] ; then
    echo "# release from describe    : $GREL"
else
    GREL=999_g$(git rev-parse --short=8 HEAD)_unkn
    echo "# no release in describe, using    : $GREL"
fi
echo

##########################
# Check if it is REL build
IS_REL=1

echo '#############################'
echo '# Checking Ready for Release '
echo '#############################'

##########################
# In Rel it should not be:
[[ $GREL = *_wip ]] && {
    echo "# * index is dirty"
    IS_REL=
}
[[ $GREL = *_unkn ]] && {
    echo "# * release not found in describe output"
    IS_REL=
}
# TODO: Check Untracked files
#???
#git ls-files --other --directory --exclude-standard
#git status --porcelain
#

##########################
# In Rel it must be:
shopt -s extglob
VER_IS_REL=1
[[ $PVER = [1-9]*([0-9]).+([0-9]).+([0-9])*([._]+([a-z0-9])) || 
   $PVER = [1-9]*([0-9]).+([0-9])[._]rc+([0-9]) ]] || {
    echo "# * version ($PVER) is not M.N.K nor M.N[._]rcK"
    VER_IS_REL=
    IS_REL=
}
[[ $PVER_CANON > $GVER_CANON ]] || {
    echo "# * version in pom ($PVER) not greater than in tag ($GVER)"
    IS_REL=
}
[[ $IS_REL ]] && { # push can be lengthy
    echo "# checking push --dry-run"
    [[ $(git push -n 2>&1) = "Everything up-to-date" ]] || {
        echo "# * push is not up-to-date"
        IS_REL=
    }
}
echo # empty line

##########################
if [[ $IS_REL ]] ; then
    echo "###############"
    echo "# Release build"
    echo "###############"
    VER_TAG="v$PVER"
    echo "# New tag to be added: $VER_TAG"
    echo "git tag $VER_TAG -m 'New release'"
    echo "git push --tags"
    # Major.Minor for branch
    D2VER=$( echo ${PVER%[._]rc*} | awk -F. '{print $1 "." $2 }' )
    [[ $(git branch --list $D2VER) ]] || {
        echo "# New branch ($D2VER) to be created"
        echo "git branch $D2VER $VER_TAG"
        echo "git push --all -u"
    }
    echo "\
# When change needed, make them on $D2VER branch:
# $ git checkout -B $D2VER $VER_TAG
# $ git pull"
    #
else
    echo "###############"
    echo "# Dev build"
    echo "###############"
    [[ $VER_IS_REL ]]  && {
        echo "###"
        echo "### WARNING: using Release Version ($PVER) in Dev build"
        echo "###"
    }
fi 

echo "# Build command"
pom_arg=
[[ $POM_FILE = */pom.xml ]] || {
    pom_arg="-f $( basename $POM_FILE )"
}
POM_DIR=$( cd $(dirname $POM_FILE); pwd )
[[ $(pwd) = $POM_DIR ]] ||
    echo cd $POM_DIR
echo mvn $pom_arg -Dprod.release=$GREL "$@"

exit

## Use cases:
# Any: ./mkc_mvn.sh clean package
# XDF: ./mkc_mvn.sh -P xdf-c2 clean package
# 
