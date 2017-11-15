#!/bin/bash

source ./host.sh

curl -XGET -H "Content-Type: application/text" "$HOST/dl/sets?prj=$1"
echo


curl -XGET -H "Content-Type: application/text" "$HOST/dl/sets?prj=$1&src=fs&cat=data"
echo

