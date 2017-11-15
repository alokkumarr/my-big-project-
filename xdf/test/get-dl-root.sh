#!/bin/bash

source ./host.sh

curl -XGET -v \
  -H "Content-Type: application/text" \
  -H "Origin:http://evil.com/" \
  $HOST/admin/root

echo
