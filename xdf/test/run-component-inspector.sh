#!/bin/bash

source ./host.sh

curl -XPOST -H "Content-Type: application/json" \
    -d '@inspector.jconf' \
    "$HOST/run?prj=project1&component=csvInspector&batch=Batch1"

echo

