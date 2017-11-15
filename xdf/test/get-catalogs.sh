#!/bin/bash

source ./host.sh

# List all catalogs
curl -XGET -H "Content-Type: application/text" $HOST/dl/catalogs?prj=$1
echo

# List all in specified data set (for review)
curl -XGET -H "Content-Type: application/text" "$HOST/dl/catalogs?prj=$1&src=$2"
echo
