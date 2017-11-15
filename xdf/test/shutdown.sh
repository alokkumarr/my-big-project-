#!/bin/bash


source ./host.sh

curl -XGET -H "Content-Type: application/text" $HOST/admin/shutdown

echo
