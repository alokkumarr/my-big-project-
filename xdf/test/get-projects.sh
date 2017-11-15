#!/bin/bash

source ./host.sh

curl -XGET -H "Content-Type: application/text" "$HOST/dl/projects"

echo
