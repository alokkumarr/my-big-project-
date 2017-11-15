#!/bin/bash

source ./host.sh

# Get list of al data sets
curl -XGET -H "Content-Type: application/text" "$HOST/dl/sets?prj=$1"
echo

# Get list of al data sets in specified data source and catalog
curl -XGET -H "Content-Type: application/text" "$HOST/dl/sets?prj=$1&src=fs&cat=data"
echo

