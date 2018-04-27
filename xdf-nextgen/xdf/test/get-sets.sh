#!/bin/bash
TEST_DIR=$( cd $(dirname $0)/../test && pwd -P )
: ${TEST_DIR:?no value}
source ${TEST_DIR}/host.sh

# Get list of al data sets
curl -XGET -H "Content-Type: application/text" "$HOST/dl/sets?prj=$1"
echo

# Get list of al data sets in specified data source and catalog
curl -XGET -H "Content-Type: application/text" "$HOST/dl/sets?prj=$1&src=fs&cat=data"
echo

