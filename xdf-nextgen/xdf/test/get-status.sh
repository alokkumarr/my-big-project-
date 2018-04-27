#!/bin/bash
TEST_DIR=$( cd $(dirname $0)/../test && pwd -P )
: ${TEST_DIR:?no value}
source ${TEST_DIR}/host.sh

#Get status of long raning job providing job id
curl -XGET -H "Content-Type: application/json" \
    "$HOST/run/status?id=$1"

echo

