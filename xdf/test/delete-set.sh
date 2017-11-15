#!/bin/bash

source ./host.sh

curl -XDELETE \
  "$HOST/dl/drop/set?prj=$1&set=$2"
echo

