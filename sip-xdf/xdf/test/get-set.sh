#!/bin/bash
TEST_DIR=$( cd $(dirname $0)/../test && pwd -P )
: ${TEST_DIR:?no value}
source ${TEST_DIR}/host.sh

# Get list of al data sets
curl -XGET "$HOST/dl/set?prj=$1&name=$2"
echo
