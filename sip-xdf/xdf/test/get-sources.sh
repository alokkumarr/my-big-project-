#!/bin/bash
TEST_DIR=$( cd $(dirname $0)/../test && pwd -P )
: ${TEST_DIR:?no value}
source ${TEST_DIR}/host.sh

# List all data sources
curl -XGET -H "Content-Type: application/text" "$HOST/dl/sources?prj=$1"

echo
