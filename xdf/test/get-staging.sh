#!/bin/bash

source ./host.sh

curl -XGET -H "Content-Type: application/text" "$HOST/dl/raw?prj=$1&cat=$2"

echo

