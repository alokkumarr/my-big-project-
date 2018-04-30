#!/bin/bash

source ./host.sh

# run CSV inspector as component, this call is asynchronous (for bigger data sets)
curl -XPOST -H "Content-Type: application/json" \
    -d '@inspector.jconf' \
    "$HOST/run?prj=project1&component=csvInspector&batch=Batch1"

echo

