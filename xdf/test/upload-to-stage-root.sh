#!/bin/bash
source ./host.sh

# Upload file to staging (raw data) top level directory
curl -XPOST \
   -F "file=@$2" \
   $HOST/dl/upload/raw?prj=$1


