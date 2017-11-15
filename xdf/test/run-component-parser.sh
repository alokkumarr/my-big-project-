#!/bin/bash

source ./host.sh

BATCH=$(date +%Y%M%d-%H%m%S)

curl -XPOST -H "Content-Type: application/json" \
    -d '@parser.jconf' \
    "$HOST/run?prj=$1&component=parser&batch=$BATCH"

echo