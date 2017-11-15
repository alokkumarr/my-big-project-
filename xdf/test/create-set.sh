#!/bin/bash

source ./host.sh

curl -XPOST \
  -d '@bda_meta.json' \
  "$HOST/dl/create/set?prj=$1&set=$2"
echo

