#!/bin/bash

TEST_DIR=$( cd $(dirname $0)/../test && pwd -P )
: ${TEST_DIR:?no value}
source ${TEST_DIR}/host.sh


# List all in specified data set (for review)
curl -XGET -H "Content-Type: application/text" "$HOST/dl/category?prj=$1&category=$2"
echo

