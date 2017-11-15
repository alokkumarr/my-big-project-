#!/bin/bash

source ./host.sh

curl -XPOST \
    -d '@./inspector.jconf' \
   $HOST/preview/raw/inspect?prj=project2 \
   -o ./inspector-results.json

echo

