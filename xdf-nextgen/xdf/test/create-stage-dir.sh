#!/bin/bash
TEST_DIR=$( cd $(dirname $0)/../test && pwd -P )
: ${TEST_DIR:?no value}
source ${TEST_DIR}/host.sh

# Create directory in staging (raw data) area
curl -XPOST \
  "$HOST/dl/create/raw?prj=$1&cat=$2"
echo

