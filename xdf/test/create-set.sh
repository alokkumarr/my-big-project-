#!/bin/bash

source ./host.sh

#Create empty data set with default data source and catalog
curl -XPOST \
  -d '@bda_meta.json' \
  "$HOST/dl/create/set?prj=$1&set=$2"
echo

