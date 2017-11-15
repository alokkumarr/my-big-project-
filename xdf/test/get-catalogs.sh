#!/bin/bash

source ./host.sh

curl -XGET -H "Content-Type: application/text" $HOST/dl/catalogs?prj=$1
echo

curl -XGET -H "Content-Type: application/text" "$HOST/dl/catalogs?prj=$1&src=$2"
echo
