#!/bin/bash
source ./host.sh

#Simulate internal crash - to test internal stability
curl -XGET -H "Content-Type: application/text" "$HOST/dl/crash?prj=whatever"

echo
