#!/bin/bash
TEST_DIR=$( cd $(dirname $0)/../test && pwd -P )
: ${TEST_DIR:?no value}
source ${TEST_DIR}/host.sh


#Delete data set in located in default data source and default catalog
curl -XDELETE \
  "$HOST/dl/drop/set?prj=$1&set=$2"
echo

