#!/bin/bash

source ./host.sh

curl -XPOST \
   -F "file=@$2" \
   "$HOST/dl/upload/raw?prj=$1&cat=$3"

echo

