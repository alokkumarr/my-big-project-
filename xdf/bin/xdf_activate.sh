#!/bin/bash

#
# VARS params:
#   dl.root
#   http.port
#==> bin/xdf_info

#
# Activate application: generate bin/xdf_info
# from bin/templates/xdf_info.templates and
# /etc/bda/<APP_NAME>.vars and /etc/bda/bda.vars files
#
CMD_DIR=$( cd $(dirname $0) && pwd -P )
: ${CMD_DIR:?no value}
APPL_INFO=$CMD_DIR/xdf_info

[[ ${1:-x} = deactivate ]] && {
    exit 0
}

MK_CONF=$CMD_DIR/mk_conf.sh
[[ -x $MK_CONF ]] || {
    $MK_CONF # print error on execute
    exit
}

# Check appl_info template
( <$CMD_DIR/templates/xdf_info.template ) || exit

# Extract application name for first time
APPL_NAME=$( awk '/^name:/ { print $2 }' $CMD_DIR/templates/xdf_info.template )
: ${APPL_NAME:?no value}

# Check vars files
APPL_VARS_FILE=/etc/bda/$APPL_NAME.vars
( <$APPL_VARS_FILE ) || exit
VARS_FILES=( $APPL_VARS_FILE )

# Cluster vars file
BDA_VARS_FILE=/etc/bda/bda.vars
[[ -f $BDA_VARS_FILE ]] &&
VARS_FILES+=( $BDA_VARS_FILE )

echo Activation vars: "${VARS_FILES[@]}"

# Run mk_conf to create bin/xdf_info
$MK_CONF >$APPL_INFO \
    $CMD_DIR/templates/xdf_info.template \
    ${VARS_FILES[@]} || {
        echo 1>&2 error in $APPL_INFO generation
        exit 1
    }
chmod 0755 $APPL_INFO

CONF_DIR=$( cd $CMD_DIR/../conf && pwd )
: ${CONF_DIR:?no value}


exit 0
