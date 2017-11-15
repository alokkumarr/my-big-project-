#!/bin/bash

source ./host.sh
#Delete data set in located in default data source and default catalog
curl -XDELETE \
  "$HOST/dl/drop/set?prj=$1&set=$2"
echo

