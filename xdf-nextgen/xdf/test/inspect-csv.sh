#!/bin/bash

source ./host.sh

# run CSV inspector, this call is synchronous
curl -XPOST \
    -d '@./inspector.jconf' \
   $HOST/preview/raw/inspect?prj=project2 \
   -o ./inspector-results.json

echo

