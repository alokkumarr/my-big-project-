#!/bin/bash

TEST_DIR=$( cd $(dirname $0)/../test && pwd -P )
: ${TEST_DIR:?no value}
source ${TEST_DIR}/host.sh

# Get list of projects
curl -XGET -H "Content-Type: application/text" "$HOST/dl/projects"

echo
