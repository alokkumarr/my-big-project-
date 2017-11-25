#!/bin/bash
TEST_DIR=$( cd $(dirname $0)/../test && pwd -P )
: ${TEST_DIR:?no value}
source ${TEST_DIR}/host.sh

# Create new project
curl -XPOST "$HOST/dl/create/project?prj=$1"

echo
