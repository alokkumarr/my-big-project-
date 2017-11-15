#!/bin/bash


source ./host.sh

# List all data sources
curl -XGET -H "Content-Type: application/text" "$HOST/dl/sources?prj=$1"

echo
