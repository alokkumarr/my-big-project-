#!/bin/bash


source ./host.sh

# Shutdown XDF REST services, Services must be started
curl -XGET -H "Content-Type: application/text" $HOST/admin/shutdown

echo
