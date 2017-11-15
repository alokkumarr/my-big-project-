#!/bin/bash

#
# VARS params:
#   dl-root
#   http-port
#==> xdf_info
#==> xdf-rest.conf  

#
# Activate application: generate bin/appl_info
# from bin/templates/appl_info.templates and
# /etc/bda/<APP_NAME>.vars and /etc/bda/cluster.vars files
#
CMD_DIR=$(dirname $0)
APPL_INFO=$CMD_DIR/appl_info


[[ ${1:-x} = deactivate ]] && {
    rm -f $APPL_INFO
    echo Deactivation completed, removed $APPL_INFO
    exit 0
}

MK_CONF=$CMD_DIR/mk_conf.sh
[[ -x $MK_CONF ]] || {
    $MK_CONF # print error on execute
    exit
}

# Check appl_info template
( <$CMD_DIR/templates/appl_info.template ) || exit

# Extract application name for first time
APPL_NAME=$( awk '/^name:/ { print $2 }' $CMD_DIR/templates/appl_info.template )
: ${APPL_NAME:?no value}

# Check vars files
APPL_VARS_FILE=/etc/bda/$APPL_NAME.vars
( <$APPL_VARS_FILE ) || exit
VARS_FILES=( $APPL_VARS_FILE )

# Cluster vars file
CLST_VARS_FILE=/etc/bda/cluster.vars
[[ -f $CLST_VARS_FILE ]] &&
VARS_FILES+=( $CLST_VARS_FILE )

# Run mk_conf
$MK_CONF >$APPL_INFO \
    $CMD_DIR/templates/appl_info.template \
    ${VARS_FILES[@]}  || {
    echo 1>&2 Activation completed with error
    exit 1
}

chmod 0755 $APPL_INFO
echo Activation completed, created $APPL_INFO

exit 0
