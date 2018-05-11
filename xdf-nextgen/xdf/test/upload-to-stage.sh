#!/bin/bash

source ./host.sh
# Upload file to staging (raw data) area to specified direcory
# Directory will be created id not exists
# Directory can contain multiple sub-directories e.g. dir1/dir2/dir3
curl -XPOST \
   -F "file=@$2" \
   "$HOST/dl/upload/raw?prj=$1&cat=$3"

echo

