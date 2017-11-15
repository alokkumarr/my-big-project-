#!/bin/bash


source ./host.sh

curl -XGET -H "Content-Type: application/json" \
    "$HOST/run/status?id=$1"

echo

