#!/bin/bash

source ./host.sh
# Create directory in staging (raw data) area
curl -XPOST \
  "$HOST/dl/create/raw?prj=$1&cat=$2"
echo

