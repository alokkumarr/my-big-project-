#!/bin/bash

APP_HOME=/mapr/poc/apps/xdf-rest
new_rq=$APP_HOME/bin/new-request.json

rm ../log/*

curl -o ./result.json -XGET -H "Content-Type: application/text" http://10.48.72.61:15005/dl/objects?prj=$1

echo

