#!/bin/bash

source ./host.sh

# Get list of projects
curl -XGET -H "Content-Type: application/text" "$HOST/dl/projects"

echo
