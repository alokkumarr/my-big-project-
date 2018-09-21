#!/bin/bash

CMD_DIR=$( cd $(dirname $0); pwd )
source $CMD_DIR/prepare_env || exit
: ${BATCH_ID:?no value}

CMD_NAM=${CMD_FNM%.*}

${XD_STAGE:=${CMD_NAM#run-}}
: ${XD_STAGE:?no value}

CONF_NAME=$XD_STAGE.jconf

prep_conf_name
: ${CONF_FILE:?no value}

#cd /home/mapr

# run_stage <XDF-COMP> <ICTL_NAME> ..
Rscript \
  $R_HOME/exec/Correlater.R \
  -b $BATCH_ID \
  -a $APPL_NAME \
  -c $CONF_FILE \
  -o $R_HOME \
  -r $XDF_ROOT

rc=$?
echo Result: $rc
exit $rc

