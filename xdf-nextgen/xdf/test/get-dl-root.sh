#!/bin/bash
TEST_DIR=$( cd $(dirname $0)/../test && pwd -P )
: ${TEST_DIR:?no value}
source ${TEST_DIR}/host.sh

# Get root directory (also simulates CORS)
curl -XGET -v \
  -H "Content-Type: application/text" \
  -H "Origin:http://evil.com/" \
  $HOST/admin/root

echo
