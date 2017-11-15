#!/bin/bash

APP_HOME=/mapr/poc/apps/xdf-rest
new_rq=$APP_HOME/bin/new-request.json

rm ../log/*

#curl -XPOST -H "Content-Type: application/text" -d "@$new_rq" http://10.48.72.61:15005/run/DataProfiler/app/batch

echo

curl -XGET http://10.48.72.61:15005/status

echo

