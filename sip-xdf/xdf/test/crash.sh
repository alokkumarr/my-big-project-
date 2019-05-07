#!/bin/bash
TEST_DIR=$( cd $(dirname $0)/../test && pwd -P )
: ${TEST_DIR:?no value}
source ${TEST_DIR}/host.sh

#Simulate internal crash - to test internal stability
curl -XGET -H "Content-Type: application/text" "$HOST/dl/crash?prj=whatever"

echo
