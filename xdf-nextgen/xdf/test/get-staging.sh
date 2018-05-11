#!/bin/bash
TEST_DIR=$( cd $(dirname $0)/../test && pwd -P )
: ${TEST_DIR:?no value}
source ${TEST_DIR}/host.sh

# List all files/directories in staging area under specified directory (catalog)
# directory can have sub-directories e.g. dir1/dir2/dir3
curl -XGET -H "Content-Type: application/text" "$HOST/dl/raw?prj=$1&cat=$2"

echo

