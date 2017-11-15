#!/bin/bash

source ./host.sh

# Get root directory (also simulates CORS)
curl -XGET -v \
  -H "Content-Type: application/text" \
  -H "Origin:http://evil.com/" \
  $HOST/admin/root

echo
