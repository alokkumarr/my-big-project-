#!/bin/bash

source ./host.sh

curl -XPOST \
  "$HOST/dl/create/raw?prj=$1&cat=$2"
echo

