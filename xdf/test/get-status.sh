#!/bin/bash


source ./host.sh

#Get status of long raning job providing job id
curl -XGET -H "Content-Type: application/json" \
    "$HOST/run/status?id=$1"

echo

